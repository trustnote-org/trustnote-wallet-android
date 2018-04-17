package org.trustnote.wallet.network.hubapi;

import org.json.JSONObject;

import java.util.HashMap;

public class HubPackageBase {

    public MSG_TYPE msgType;
    String command;
    String content;
    JSONObject body;

    HashMap<String, String> attrs = new HashMap();
    BODY_TYPE subjectType = BODY_TYPE.EMPTY;


    public static enum MSG_TYPE {
        request,
        response,
        justsaying,
        CONNECTED,
        CLOSED,
        ERROR,
    }

    public static enum BODY_TYPE {
        EMPTY,
        CONNECTED,
        RES_CHALLENGE,
    }
}
