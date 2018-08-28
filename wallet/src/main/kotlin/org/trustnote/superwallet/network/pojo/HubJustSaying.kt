package org.trustnote.superwallet.network.pojo

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import org.trustnote.superwallet.network.HubMsgFactory
import org.trustnote.superwallet.util.Utils

open class HubJustSaying : HubMsg {

    var subject: String
    var bodyJson: JsonElement

    constructor(textFromHub: String) : super(textFromHub) {

        subject = msgJson.getAsJsonPrimitive(HubMsgFactory.SUBJECT).asString
        bodyJson = msgJson.get(HubMsgFactory.BODY)?: Utils.emptyJsonObject

    }

    constructor(subject:String, bodyJson: JsonElement) : super(MSG_TYPE.justsaying) {

        this.msgSource = MSG_SOURCE.wallet
        this.subject = subject
        this.bodyJson = bodyJson

        this.msgJson = JsonObject()
        this.msgJson.addProperty(HubMsgFactory.SUBJECT, subject)
        this.msgJson.add(HubMsgFactory.BODY, bodyJson)

    }

}

