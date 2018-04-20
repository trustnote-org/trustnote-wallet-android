package org.trustnote.wallet.network.hubapi

import java.net.URI
import java.nio.channels.NotYetConnectedException
import java.util.Timer

import org.java_websocket.client.WebSocketClient
import org.java_websocket.drafts.Draft
import org.java_websocket.handshake.ServerHandshake
import org.trustnote.wallet.util.Utils

import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import org.trustnote.wallet.network.ReqTagMapping


class HubClient : WebSocketClient {

    internal lateinit var mHeartBeatTimer: Timer
    internal var mSubject: Subject<HubResponse> = PublishSubject.create()
    private val reqTagMapping = ReqTagMapping()

    constructor(serverUri: URI, draft: Draft) : super(serverUri, draft) {}

    constructor(serverURI: URI) : super(serverURI) {}

    constructor(serverUri: URI, httpHeaders: Map<String, String>) : super(serverUri, httpHeaders) {}


    @Throws(NotYetConnectedException::class)
    override fun send(text: String) {
        super.send(text)
        log("SENDING: $text")
    }

    fun sendHubRequest(req: HubRequest) {
        reqTagMapping.put(req.tag, req.expectBodyType)
        super.send(req.toHubString())
    }

    override fun onOpen(handshakedata: ServerHandshake) {
        log("ONOPEN: " + handshakedata.toString())
        mSubject.onNext(HubResponse.createConnectedInstance())
        send(HubRequest.reqVersion())
        sendHubRequest(HubRequest.reqGetWitnesses())
    }

    override fun onMessage(message: String) {
        log("RECEIVED:onMessage: $message")
        val hubResponse = HubResponse.parseResponse(message, reqTagMapping)
        mSubject.onNext(hubResponse)
    }

    override fun onClose(code: Int, reason: String, remote: Boolean) {
        log("onClose:: " + "Connection closed by " + (if (remote) "remote peer" else "us") + " Code: " + code + " Reason: " + reason)
        mSubject.onNext(HubResponse.createCloseInstance())
        mHeartBeatTimer.cancel()
    }

    override fun onError(ex: Exception) {
        ex.printStackTrace()
        log("onError:: " + ex.message)
    }

    fun init(subject: Subject<HubResponse>) {
        //TODO: this.mHeartBeatTimer = mHeartBeatTimer;
        this.mSubject = subject
        mHeartBeatTimer = Timer(true)
        mHeartBeatTimer.scheduleAtFixedRate(HeartBeatTask(this), (3 * 1000).toLong(), (5 * 1000).toLong())
    }

    private fun log(msg: String) {
        Utils.d(HubClient::class.java, msg)
    }

}