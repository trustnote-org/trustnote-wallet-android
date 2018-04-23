package org.trustnote.wallet.network.hubapi

import android.net.NetworkInfo
import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.net.URI
import java.nio.channels.NotYetConnectedException

import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import org.trustnote.wallet.util.Utils

import io.reactivex.subjects.Subject
import org.trustnote.wallet.TApp
import org.trustnote.wallet.network.HubManager


class HubClient : WebSocketClient {

    val mHubSocketModel: HubSocketModel
    private var isConnectCalled = false

    constructor(hubSocketModel: HubSocketModel) : super(URI(hubSocketModel.mHubAddress)) {
        mHubSocketModel = hubSocketModel
        mHubSocketModel.mHubClient = this
        mHubSocketModel.mHeartBeatTask = HeartBeatTask(this)
    }

    @Throws(NotYetConnectedException::class)
    override fun send(text: String) {
        super.send(text)
        log("SENDING: $text")
    }

    override fun connect() {

        ReactiveNetwork.observeNetworkConnectivity(TApp.context)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { connectivity ->
                    log("state: ${connectivity.state}, typeName: ${connectivity.typeName}")
                    if (connectivity.state == NetworkInfo.State.CONNECTED) {
                        if (!isConnectCalled) {
                            isConnectCalled = true
                            // TODO: if already disposed, do nothing.
                            //TODO: should dispose itself, otherwise memory leak(maybe?). find a one-time operator in rxjava for this case.
                            super.connect()
                        }
                    }
                }
    }

    fun sendHubMsg(hubMsg: HubMsg) {
        hubMsg.lastSentTime = System.currentTimeMillis()
        mHubSocketModel.mRequestMap.put(hubMsg)
        send(hubMsg.toHubString())
    }

    override fun onOpen(handshakedata: ServerHandshake) {
        log("ONOPEN: " + handshakedata.toString())
        mHubSocketModel.mHeartBeatTask.start()
        sendHubMsg(HubMsgFactory.walletVersion())
        sendHubMsg(HubMsgFactory.getWitnesses(mHubSocketModel))

        //        mSubject.onNext(HubResponse.createConnectedInstance())
//        send(HubRequest.reqVersion())
    }

    override fun onMessage(message: String) {
        log("RECEIVED:onMessage: $message")
        val hubMsg = HubMsgFactory.parseMsg(message)
        mHubSocketModel.mSubject.onNext(hubMsg)

        //TODO: remove the req after every thing is OK.
        if (hubMsg.msgType == MSG_TYPE.response) {
            (hubMsg as HubResponse).handResonse(mHubSocketModel)
        }
    }

    override fun onClose(code: Int, reason: String, remote: Boolean) {
        log("onClose:: " + "Connection closed by " + (if (remote) "remote peer" else "us") + " Code: " + code + " Reason: " + reason)
        mHubSocketModel.mHeartBeatTask.stop()

        HubManager.instance.reConnectHub(mHubSocketModel)
    }

    //From test result: onError sometimes be called after onClose.
    override fun onError(ex: Exception) {
        ex.printStackTrace()
        log("onError:: " + ex.message)
    }

    fun init(subject: Subject<HubResponse>) {
        //TODO: mHeartBeatTimer = mHeartBeatTimer;
        //mSubject = subject
    }

    private fun log(msg: String) {
        Utils.debugHub(msg)
    }

}