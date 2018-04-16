package org.trustnote.wallet.debugui;


import android.view.inputmethod.InputMethodSession;

import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.socketio.Acknowledge;
import com.koushikdutta.async.http.socketio.ConnectCallback;
import com.koushikdutta.async.http.socketio.JSONCallback;
import com.koushikdutta.async.http.socketio.SocketIOClient;
import com.koushikdutta.async.http.socketio.StringCallback;

import org.json.JSONArray;
import org.json.JSONObject;
import org.trustnote.wallet.util.Utils;

public class TestSocket {

    static String hubAddress = "wss://raytest.trustnote.org:443";

    public static void test() {
        SocketIOClient.connect(AsyncHttpClient.getDefaultInstance(), hubAddress, new ConnectCallback() {
            @Override
            public void onConnectCompleted(Exception ex, SocketIOClient client) {
                if (ex != null) {
                    //Utils.debugToast("connect error!");
                    Utils.debugLog(ex.getMessage());
                    ex.printStackTrace();
                    return;
                } else {
                    Utils.debugToast("Connected!");
                }
                client.setStringCallback(new StringCallback() {
                    @Override
                    public void onString(String s, Acknowledge acknowledge) {
                        System.out.println(s);
                    }

                });
                client.setJSONCallback(new JSONCallback() {
                    @Override
                    public void onJSON(JSONObject json, Acknowledge acknowledge) {
                        System.out.println("json: " + json.toString());
                    }
                });
            }
        });
    }


}
