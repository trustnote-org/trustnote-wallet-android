package org.trustnote.wallet.network.hubapi

import java.net.URI
import java.nio.channels.NotYetConnectedException

import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import org.trustnote.wallet.util.Utils

import io.reactivex.subjects.Subject
import org.trustnote.wallet.network.HubManager


class HubClient : WebSocketClient {

    val mHubSocketModel: HubSocketModel

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
        val hugMsg = HubMsgFactory.parseMsg(message)
        mHubSocketModel.mSubject.onNext(hugMsg)

        //TODO: remove the req after every thing is OK.
        if (hugMsg.msgType == MSG_TYPE.response) {
            mHubSocketModel.mRequestMap.responseMsgArrived(hugMsg)
        }

    }

    override fun onClose(code: Int, reason: String, remote: Boolean) {
        log("onClose:: " + "Connection closed by " + (if (remote) "remote peer" else "us") + " Code: " + code + " Reason: " + reason)
        mHubSocketModel.mHeartBeatTask.stop()

        //Retry logic
        HubManager.instance.reConnectHubAfter10Sec()
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