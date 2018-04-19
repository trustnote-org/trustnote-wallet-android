package org.trustnote.wallet.network.hubapi;

import com.google.gson.JsonParser;


public class HubResponse extends HubPackageBase {

    public final static String TYPE_Justsaying = "justsaying";
    public final static String TYPE_Status_connected = "socketconnected";
    public final static String TYPE_Status_closed = "socketclosed";
    public final static String KEY_Subject = "mSubject";
    public final static String KEY_CHALLENGE = "hub/challenge";
    public final static String KEY_Body = "body";

    static HubResponse parseResponse(String hubMsg) {

        int index = hubMsg.indexOf(',');

        if (index < 3) {
            return null;
        } else {
            HubResponse res = new HubResponse();
            res.msgType = MSG_TYPE.valueOf(hubMsg.substring(2, index - 1));
            res.content = hubMsg.substring(index + 1, hubMsg.length() - 1);
            res.body = new JsonParser().parse(res.content).getAsJsonObject();
            if (TYPE_Justsaying.equals(res.msgType)) {
                if (KEY_CHALLENGE.equals(res.body.getAsJsonPrimitive(KEY_Subject).getAsString())) {
                    res.subjectType = BODY_TYPE.RES_CHALLENGE;
                }
            }
            return res;
        }
    }


    public static HubResponse createConnectedInstance() {
        HubResponse res = new HubResponse();
        res.msgType = MSG_TYPE.CONNECTED;
        return res;
    }

    public static HubResponse createCloseInstance() {
        HubResponse res = new HubResponse();
        res.msgType = MSG_TYPE.CLOSED;
        return res;
    }

    public String getChallenge() {
        if (subjectType != BODY_TYPE.RES_CHALLENGE) {
            throw new IllegalStateException("Msg is not RES_CHALLENGE");
        }
        return body.get(KEY_Body).getAsString();
    }
}
