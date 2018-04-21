package org.trustnote.wallet.network.hubapi

import java.net.URI
import java.nio.channels.NotYetConnectedException

import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import org.trustnote.wallet.util.Utils

import io.reactivex.subjects.Subject


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

    fun sendHubRequest(req: HubRequest) {
        mHubSocketModel.mRequestMap.put(req)
        send(req.toHubString())
    }

    override fun onOpen(handshakedata: ServerHandshake) {
        log("ONOPEN: " + handshakedata.toString())
        mHubSocketModel.mHeartBeatTask.start()
        sendHubRequest(HubMsgFactory.walletHeartBeat(mHubSocketModel))
        //        mSubject.onNext(HubResponse.createConnectedInstance())
//        send(HubRequest.reqVersion())
    }

    override fun onMessage(message: String) {
        log("RECEIVED:onMessage: $message")
        mHubSocketModel.mSubject.onNext(HubMsgFactory.parseMsg(message))
    }

    override fun onClose(code: Int, reason: String, remote: Boolean) {
        log("onClose:: " + "Connection closed by " + (if (remote) "remote peer" else "us") + " Code: " + code + " Reason: " + reason)
        mHubSocketModel.mHeartBeatTask.stop()
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
        Utils.d(HubClient::class.java, msg)
    }

}