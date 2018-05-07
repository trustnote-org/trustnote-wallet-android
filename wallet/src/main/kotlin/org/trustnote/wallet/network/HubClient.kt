package org.trustnote.wallet.network

import io.reactivex.disposables.Disposable
import java.net.URI
import java.nio.channels.NotYetConnectedException

import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import org.trustnote.wallet.util.Utils

import org.trustnote.wallet.network.pojo.HubMsg
import org.trustnote.wallet.network.pojo.HubResponse
import org.trustnote.wallet.network.pojo.MSG_TYPE


class HubClient : WebSocketClient {

    val mHubSocketModel: HubSocketModel
    private var isConnectCalled = false
    private var networkMonitor : Disposable? = null

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

        Utils.connectedEvent().subscribe { connectivity ->
            log("state: ${connectivity.state}, typeName: ${connectivity.typeName}")
            if (!isConnectCalled) {
                isConnectCalled = true
                // TODO: if already disposed, do nothing.
                //TODO: should dispose itself, otherwise memory leak(maybe?). find a one-time operator in rxjava for this case.
                super.connect()
            }
        }
    }

    fun sendHubMsg(hubMsg: HubMsg) {
        if (!isConnectCalled || isClosed || !isOpen) {
            log("try to send hub msg, but socket closed")
            return
        }
        hubMsg.lastSentTime = System.currentTimeMillis()
        mHubSocketModel.mRequestMap.put(hubMsg)
        send(hubMsg.toHubString())
    }

    override fun onOpen(handshakedata: ServerHandshake) {
        log("ONOPEN: " + handshakedata.toString())
        mHubSocketModel.mHeartBeatTask.start()
        sendHubMsg(HubMsgFactory.walletVersion())
        sendHubMsg(HubMsgFactory.getWitnesses(mHubSocketModel))

    }

    override fun onMessage(message: String) {
        log("RECEIVED:onMessage: $message")
        val hubMsg = HubMsgFactory.parseMsg(message)
        mHubSocketModel.mSubject.onNext(hubMsg)

        //TODO: remove the req after every thing is OK.
        if (hubMsg.msgType == MSG_TYPE.response) {
            mHubSocketModel.responseArrived(hubMsg as HubResponse)
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

    private fun log(msg: String) {
        Utils.debugHub(msg)
    }

    fun dispose() {
        if (networkMonitor != null && !networkMonitor!!.isDisposed)
            networkMonitor!!.dispose()
    }

}