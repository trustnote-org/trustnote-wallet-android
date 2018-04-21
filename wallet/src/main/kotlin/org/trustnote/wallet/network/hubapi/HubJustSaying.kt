package org.trustnote.wallet.network.hubapi

import com.google.gson.JsonObject


class HubJustSaying : HubMsg {

    val subject: String
    val bodyJson: JsonObject
    constructor(textFromHub: String) : super(textFromHub) {
        subject = msgJson.getAsJsonPrimitive(HubMsgFactory.SUBJECT).asString
        bodyJson = msgJson.getAsJsonObject(HubMsgFactory.BODY)
    }

    constructor(subject:String, bodyJson: JsonObject) : super(MSG_TYPE.justsaying) {
        this.msgSource = MSG_SOURCE.wallet
        this.subject = msgJson.getAsJsonPrimitive(HubMsgFactory.SUBJECT).asString
        this.bodyJson = msgJson.getAsJsonObject(HubMsgFactory.BODY)
    }

}

