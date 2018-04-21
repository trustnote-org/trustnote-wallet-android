package org.trustnote.wallet.network.hubapi

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import org.trustnote.wallet.util.Utils


class HubResponse : HubMsg {

    val tag: String
    val responseJson: JsonElement

    constructor(textFromHub: String) : super(textFromHub) {
        tag = msgJson.getAsJsonPrimitive(HubMsgFactory.TAG).asString

        responseJson = msgJson.get(HubMsgFactory.RESPONSE) ?: Utils.emptyJsonObject
    }

    constructor(responseJson: JsonObject, tag: String) : super(MSG_TYPE.response) {
        this.msgSource = MSG_SOURCE.wallet
        this.responseJson = responseJson
        this.tag = tag
    }

}

