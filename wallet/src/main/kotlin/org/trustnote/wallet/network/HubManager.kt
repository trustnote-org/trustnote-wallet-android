package org.trustnote.wallet.network

import android.net.NetworkInfo
import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.trustnote.wallet.TApp
import org.trustnote.wallet.biz.TTT
import org.trustnote.wallet.biz.js.JSApi
import org.trustnote.wallet.biz.wallet.WalletManager
import org.trustnote.wallet.network.pojo.*
import org.trustnote.wallet.util.MyThreadManager
import org.trustnote.wallet.util.Utils
import java.util.concurrent.TimeUnit

//TODO: test case when HubManager return empty\unknown result.

class HubManager {

    private val hubClients: MutableMap<String, HubClient> = mutableMapOf()
    lateinit var retrySubscription: Disposable
    private val mRequestMap = RequestMap()
    private var isNetworkConnected: Boolean = true

    init {
        setupRetryLogic()
        monitorNetwork()
    }

    companion object {
        val instance = HubManager()
    }

    fun sendHubMsg(hubMsg: HubMsg) {
        if (isNetworkConnected) {
            sendHubMsgFromHubClient(hubMsg)
        } else {
            hubMsg.networkErr()
        }
    }

    private fun sendHubMsgFromHubClient(hubMsg: HubMsg) {

        mRequestMap.put(hubMsg)
        hubMsg.lastSentTime = System.currentTimeMillis()

        val hubClient = hubClients[hubMsg.targetHubAddress]
        if (hubClient != null) {
            hubClient.sendHubMsg(hubMsg)
        } else {
            //TODO: for no request type msg, discard it?
            connectHub(hubMsg.targetHubAddress)
        }

    }

    fun hubClosed(mHubAddress: String) {

        val disconnectedHub = hubClients.remove(mHubAddress)
        disconnectedHub?.dispose()

    }

    fun hubOpened(hubClient: HubClient) {

        if (hubClients.containsKey(hubClient.mHubAddress)) {
            hubClients.remove(hubClient.mHubAddress)
        }

        hubClients[hubClient.mHubAddress] = hubClient

        sendHubMsgInQueue(hubClient.mHubAddress)

    }

    private fun updateMyTempPubkey() {

        //TODO: should we update the temp key every connect?
        val myProfile = WalletManager.model.mProfile
        if (myProfile.prevTempPrivkey.isEmpty()
                || myProfile.tempPrivkey.isEmpty()) {

            val api = JSApi()
            val priv = api.genPrivKeySync()
            val pub = api.genPubKeySync(priv)

            sendHubMsg(ReqTempPubkey(pub, priv))
        } else {
            val api = JSApi()
            val priv = api.genPrivKeySync()
            val pub = api.genPubKeySync(priv)

            sendHubMsg(ReqTempPubkey(pub, priv))
//            sendHubMsg(ReqTempPubkey(myProfile.tempPubkey, myProfile.tempPrivkey))
        }

    }

    private fun sendHubMsgInQueue(hubAddress: String) {
        val reqs = mRequestMap.getRetryMap()
        for (req in reqs.values) {
            if (req.shouldSendWithThisHub(hubAddress)) {
                hubClients[hubAddress]?.sendHubMsg(req)
            }
        }
    }

    fun setupRetryLogic() {

        retrySubscription = Observable
                .interval(TTT.HUB_REQ_RETRY_CHECK_SECS, TimeUnit.SECONDS)
                .observeOn(Schedulers.computation())
                .subscribe {
                    retryOrTimeout()
                }
    }

    private fun isTimeout(hubMsg: HubMsg): Boolean {
        return (System.currentTimeMillis() - hubMsg.lastSentTime) > TTT.HUB_TIMEOUT_SECS * 1000
    }

    @Synchronized
    private fun retryOrTimeout() {
        for ((tag, hubMsg) in mRequestMap.getRetryMap()) {
            if (isTimeout(hubMsg) && hubMsg is HubRequest && hubMsg.msgType == MSG_TYPE.request) {
                mRequestMap.remove(hubMsg.tag)
                hubMsg.setResponse(HubResponse(MSG_TYPE.timeout))
            }
        }
    }

    fun dispose() {
        //SHOULD never be called.
        if (!retrySubscription.isDisposed) {
            retrySubscription.dispose()
        }

    }

    fun connectHubWithDelay(hubAddress: String) {
        if (isHubActive(hubAddress)) {
            return
        } else {

        }

        MyThreadManager.instance.runDealyed(
                Utils.random.nextInt(TTT.HUB_WAITING_SECONDS_RECCONNECT).toLong()
        )
        {
            connectHub(hubAddress)
        }
    }

    private fun connectHub(hubAddress: String) {
        if (isHubActive(hubAddress)) {
            return
        } else {
            val hubClient = HubClient(hubAddress)
            hubClient.connect()
            hubClients[hubAddress] = hubClient
        }
    }

    private fun isHubActive(hubAddress: String): Boolean {
        return hubClients.containsKey(hubAddress)
    }

    fun onMessage(hubAddress: String, message: String) {

        //TODO: how to filter heartbeat response?? ["response",{"tag":"RANDOM:-130514320"}]
        //TODO: send heartbean from hub manager?

        if (!hubClients.containsKey(hubAddress)) {
            Utils.logW("onMessage with unknown hubclient. hubaddress is: $hubAddress, message is: $message")
            return
        }

        val hubMsg = HubMsgFactory.parseHubMsg(hubAddress, message)

        if (hubMsg.msgType == MSG_TYPE.response) {
            val tag = (hubMsg as HubResponse).tag
            val relatedRequest = mRequestMap.getHubRequest(tag)
            if (relatedRequest != null) {
                relatedRequest.setResponse(hubMsg)
                relatedRequest.handleResponse()
                mRequestMap.remove(tag)
            } else {
                //Utils.logW("onMessage with unknown request. hubaddress is: $hubAddress, message is: $message")
            }
            return
        }

        //["justsaying",{"subject":"hub/challenge","body":"m0dxaegeZyT/GZI//j2cMK0CdK57iF6dLqEI51gk"}]
        if (hubMsg is HubJustSaying && HubMsgFactory.SUBJECT_HUB_CHALLENGE == hubMsg.subject) {
            hubClients[hubAddress]?.mChallenge = hubMsg.bodyJson.asString

            if (hubMsg.bodyJson.asString.isNotEmpty() && HubModel.instance.mDefaultHubAddress == hubAddress) {
                loginMyDefaultHub()
            }
            return

        }

        HubModel.instance.onMessage(hubMsg)

    }

    private fun loginMyDefaultHub() {
        val hub = hubClients[HubModel.instance.mDefaultHubAddress]

        if (WalletManager.model != null && hub != null && hub.mChallenge.isNotEmpty()) {
            val msg = JustSayingLogin(hub.mChallenge)

            sendHubMsg(msg)

            updateMyTempPubkey()

        }
    }

    fun monitorNetwork() {
        ReactiveNetwork.observeNetworkConnectivity(TApp.context)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { connectivity ->
                    isNetworkConnected = (connectivity.state == NetworkInfo.State.CONNECTED)
                }
    }

    fun clear() {
        mRequestMap.clear()
        hubClients.clear()
    }

}

