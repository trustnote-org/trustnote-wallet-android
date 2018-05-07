package org.trustnote.wallet.network.hubapi

import com.google.gson.JsonObject
import org.trustnote.wallet.util.Utils

class HubRequest : HubMsg {

    //TODO: for some request, we need a timeout logic to let caller has a chance to handle the err case.
    //TODO: for other request, we need continue retry until success.

    val command: String
    val params: JsonObject
    val tag: String
    var attachedInfo: Object = Object()

    constructor(textFromHub: String) : super(textFromHub) {
        tag = msgJson.getAsJsonPrimitive(HubMsgFactory.TAG).asString
        command = msgJson.getAsJsonPrimitive(HubMsgFactory.COMMAND).asString
        params = msgJson.getAsJsonObject(HubMsgFactory.PARAMS)
    }

    constructor(command: String, tag: String, params: JsonObject = Utils.emptyJsonObject) : super(MSG_TYPE.request) {
        this.msgSource = MSG_SOURCE.wallet
        this.command = command
        this.tag = tag
        this.params = params
        this.msgJson = JsonObject()
        this.msgJson.addProperty(HubMsgFactory.COMMAND, command)
        this.msgJson.add(HubMsgFactory.PARAMS, params)
        this.msgJson.addProperty(HubMsgFactory.TAG, tag)
    }

    constructor(msgType: MSG_TYPE) : super(msgType) {
        this.msgSource = MSG_SOURCE.wallet
        this.params = Utils.emptyJsonObject
        this.command = ""
        this.tag = ""
    }
}
