package org.trustnote.wallet.network

import io.reactivex.disposables.CompositeDisposable
import java.net.URI
import java.nio.channels.NotYetConnectedException

import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import org.trustnote.wallet.network.pojo.*
import org.trustnote.wallet.util.Utils

class HubClient : WebSocketClient {

    private var isConnectCalled = false
    protected val disposables: CompositeDisposable = CompositeDisposable()
    var mHeartBeatTask: HeartBeatTask = HeartBeatTask(this)
    var mHubAddress: String
    var mChallenge: String = ""

    constructor(hubAddress: String) : super(URI(hubAddress)) {
        mHubAddress = hubAddress
    }

    @Throws(NotYetConnectedException::class)
    override fun send(text: String) {
        try{
            super.send(text)
            log("SENDING: $text")
        } catch (e: NotYetConnectedException) {
            log("SEND::NotYetConnectedException::${e.localizedMessage}")
        }
    }

    override fun connect() {

        val a = Utils.connectedEvent().subscribe { connectivity ->
            log("state: ${connectivity.state}, typeName: ${connectivity.typeName}")
            if (!isConnectCalled) {
                isConnectCalled = true
                // TODO: if already disposed, do nothing.
                //TODO: should dispose itself, otherwise memory leak(maybe?). find a one-time operator in rxjava for this case.
                super.connect()
            }
        }
        disposables.add(a)
    }

    fun sendHubMsg(hubMsg: HubMsg) {

        hubMsg.actualHubAddress = mHubAddress
        if (!isConnectCalled || isClosed || !isOpen) {
            log("try to send hub msg, but socket closed")
            hubMsg.networkErr()
        } else {

            send(hubMsg.toHubString())

        }
    }

    override fun onOpen(handshakedata: ServerHandshake) {

        log("ONOPEN: " + handshakedata.toString())

        mHeartBeatTask.start()

        sendHubMsg(HubMsgFactory.walletVersion())

        HubManager.instance.hubOpened(this)

    }

    override fun onMessage(message: String) {

        log("RECEIVED:onMessage: $message")

        HubManager.instance.onMessage(mHubAddress, message)

    }

    override fun onClose(code: Int, reason: String, remote: Boolean) {

        log("onClose:: " + "Connection closed by " + (if (remote) "remote peer" else "us") + " Code: " + code + " Reason: " + reason)
        mHeartBeatTask.stop()

        disposables.dispose()

        HubManager.instance.hubClosed(mHubAddress)

    }

    //From test result: onError sometimes be called after onClose.
    override fun onError(ex: Exception) {
        log("onError:: " + ex.message)
    }

    private fun log(msg: String) {

        Utils.debugHub("$msg @$mHubAddress")

    }

    fun dispose() {

        disposables.dispose()

    }

}