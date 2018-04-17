package org.trustnote.wallet.network.hubapi;

import java.net.URI;
import java.nio.channels.NotYetConnectedException;
import java.util.Map;
import java.util.Timer;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.handshake.ServerHandshake;
import org.trustnote.wallet.util.Utils;

import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;


public class HubClient extends WebSocketClient {


    public HubClient(URI serverUri, Draft draft) {
        super(serverUri, draft);
    }

    public HubClient(URI serverURI) {
        super(serverURI);
    }

    public HubClient(URI serverUri, Map<String, String> httpHeaders) {
        super(serverUri, httpHeaders);
    }


    @Override
    public void send(String text) throws NotYetConnectedException {
        super.send(text);
        log("SENDING: " + text);
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        log("ONOPEN: " + handshakedata.toString());
        send(HubRequest.reqVersion());
        mSubject.onNext(HubResponse.createConnectedInstance());
    }

    @Override
    public void onMessage(String message) {
        log("RECEIVED:onMessage: " + message);
        HubResponse hubResponse = HubResponse.parseResponse(message);
        mSubject.onNext(hubResponse);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        log("onClose:: " + "Connection closed by " + (remote ? "remote peer" : "us") + " Code: " + code + " Reason: " + reason);
        mSubject.onNext(HubResponse.createCloseInstance());
        mHeartBeatTimer.cancel();
    }

    @Override
    public void onError(Exception ex) {
        //TODO: log, when Error?
        ex.printStackTrace();
    }

    Timer mHeartBeatTimer;
    Subject<HubResponse> mSubject = PublishSubject.create();

    public void init(Subject<HubResponse> subject) {
        //TODO: this.mHeartBeatTimer = mHeartBeatTimer;
        this.mSubject = subject;
        mHeartBeatTimer = new Timer(true);
        mHeartBeatTimer.scheduleAtFixedRate(new HeartBeatTask(this), 3 * 1000, 13 * 1000);
    }

    private static void log(String msg) {
        Utils.d(HubClient.class, msg);
    }

}