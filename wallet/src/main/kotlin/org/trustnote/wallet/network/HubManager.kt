package org.trustnote.wallet.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.trustnote.wallet.TApp
import org.trustnote.wallet.biz.TTT
import org.trustnote.wallet.biz.js.JSApi
import org.trustnote.wallet.biz.upgrade.newVersionFound
import org.trustnote.wallet.biz.wallet.TestData
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
        connectHubInBackground(TTT.hubStable)
    }

    companion object {
        var isAlreadyUpdateMyTempPubkey:Boolean = false
        val instance = HubManager()
    }

    fun sendHubMsg(hubMsg: HubMsg) {
        Utils.debugHub("sendHubMsg")
        if (!isNetworkConnected) {
            updateNetworkStatus()
        }

        if (isNetworkConnected) {
            sendHubMsgFromHubClient(hubMsg)
        } else {
            hubMsg.networkErr()
        }
    }

    private fun sendHubMsgFromHubClient(hubMsg: HubMsg) {
        Utils.debugHub("sendHubMsgFromHubClient")

        var hubClient = hubClients[hubMsg.targetHubAddress]

        if (hubClient == null || !hubClient.isOpen) {
            connectHubInBackground(hubMsg.targetHubAddress)
        }

        if ((hubClient == null || !hubClient.isOpen) && hubMsg is HubRequest && hubMsg.canUseBackupHub) {
            hubClient = hubClients[TTT.hubStable]

            if (hubClient == null || !hubClient.isOpen) {
                connectHubInBackground(TTT.hubStable)
            }
        }

        if (hubClient == null || !hubClient.isOpen) {

            hubMsg.networkErr()
            return
        }

        mRequestMap.put(hubMsg)
        hubMsg.lastSentTime = System.currentTimeMillis()

        if (hubClient != null) {
            hubClient.sendHubMsg(hubMsg)
        } else {
            //TODO: for no request type msg, discard it?
            connectHubInBackground(hubMsg.targetHubAddress, false)
        }

    }

    fun hubClosed(mHubAddress: String) {
        Utils.debugHub("HubManager::hubClosed::hubAddress=$mHubAddress")

        val disconnectedHub = hubClients.remove(mHubAddress)
        disconnectedHub?.dispose()

        connectHubInBackground(mHubAddress, true)

    }

    fun hubOpened(hubClient: HubClient) {
        Utils.debugHub("HubManager::hubClosed::hubAddress=${hubClient.mHubAddress}")

        if (hubClients.containsKey(hubClient.mHubAddress)) {
            hubClients.remove(hubClient.mHubAddress)
        }

        hubClients[hubClient.mHubAddress] = hubClient

        sendHubMsgInQueue(hubClient.mHubAddress)

    }

    private fun updateMyTempPubkey() {

        //TODO: should we update the temp key every connect?
        val myProfile = WalletManager.model.mProfile
        if (!isAlreadyUpdateMyTempPubkey && myProfile.tempPrivkey.isEmpty()) {

            val api = JSApi()
            val priv = api.genPrivKeySync()
            val pub = api.genPubKeySync(priv)

            sendHubMsg(ReqTempPubkey(pub, priv))
        }
//        else {
//            val api = JSApi()
//            val priv = myProfile.tempPrivkey
//            val pub = myProfile.tempPubkey
//
//            sendHubMsg(ReqTempPubkey(pub, priv))
////            sendHubMsg(ReqTempPubkey(myProfile.tempPubkey, myProfile.tempPrivkey))
//        }

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

        try {

            for ((tag, hubMsg) in mRequestMap.getRetryMap()) {
                if (isTimeout(hubMsg) && hubMsg is HubRequest && hubMsg.msgType == MSG_TYPE.request) {
                    mRequestMap.remove(hubMsg.tag)
                    hubMsg.setResponse(HubResponse(MSG_TYPE.timeout))
                }
            }
        } catch (e: Exception) {
            Utils.logW(e.toString())
        }
    }

    fun dispose() {
        //SHOULD never be called.
        if (!retrySubscription.isDisposed) {
            retrySubscription.dispose()
        }

    }

    private fun connectHubInBackground(hubAddress: String, isDelay: Boolean = false) {
        Utils.debugHub("HubManager::connectHubInBackground::hubAddress=${hubAddress}::$isDelay")

        if (isDelay) {
            MyThreadManager.instance.hubManagerThread.schedule({
                connectHubIn(hubAddress)
            }, 3, TimeUnit.SECONDS)

        } else {
            MyThreadManager.instance.hubManagerThread.execute {
                connectHubIn(hubAddress)
            }
        }

    }

    private fun connectHubIn(hubAddress: String) {
        if (isHubActive(hubAddress)) {
        } else {
            val hubClient = HubClient(hubAddress)
            hubClient.connect()
            hubClients[hubAddress] = hubClient
        }
    }

    private fun isHubActive(hubAddress: String): Boolean {
        return hubClients.containsKey(hubAddress) && (hubClients[hubAddress]!!.isOpen)
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

        if (hubMsg is HubJustSaying && HubMsgFactory.CMD_NEW_VERSION_FROM_HUB == hubMsg.subject) {

            newVersionFound(hubMsg.bodyJson.toString())
            return

        }

        //Test code:
        //        if (hubMsg is HubJustSaying && HubMsgFactory.CMD_VERSION == hubMsg.subject) {
        //
        //            newVersionFound(TestData.newVersionInfo)
        //            return
        //
        //        }

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
                    updateNetworkStatus()
                }
    }

    private fun updateNetworkStatus() {
        val cm = TApp.context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
        isNetworkConnected = (activeNetwork?.isConnected == true)
        Utils.debugHub("HubManager::updateNetworkStatus::isNetworkConnected=$isNetworkConnected")

    }

    fun clear() {
        mRequestMap.clear()
        hubClients.clear()
    }

}

