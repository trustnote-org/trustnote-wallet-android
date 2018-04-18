package org.trustnote.wallet.network.hubapi;


import com.google.gson.JsonObject;

import java.util.HashMap;

public class HubPackageBase {

    public MSG_TYPE msgType;
    public String command;
    public String content;
    public JsonObject body;

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
