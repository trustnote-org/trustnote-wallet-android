package org.trustnote.wallet.network.pojo

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import org.trustnote.wallet.network.HubMsgFactory
import org.trustnote.wallet.util.Utils


class HubResponse : HubMsg {

    val tag: String
    val responseJson: JsonElement
    val hasErrorFromHub: Boolean

    constructor(textFromHub: String) : super(textFromHub) {
        tag = msgJson.getAsJsonPrimitive(HubMsgFactory.TAG).asString

        responseJson = msgJson.get(HubMsgFactory.RESPONSE) ?: Utils.emptyJsonObject
        hasErrorFromHub = responseJson is JsonObject && responseJson.has("error")
    }

    constructor(msgType: MSG_TYPE = MSG_TYPE.empty) : super(msgType) {
        tag = ""
        responseJson = Utils.emptyJsonObject
        hasErrorFromHub = responseJson is JsonObject && responseJson.has("error")
    }

    constructor(responseJson: JsonObject, tag: String) : super(MSG_TYPE.response) {
        this.responseJson = responseJson
        this.tag = tag
        hasErrorFromHub = responseJson is JsonObject && responseJson.has("error")
    }

    fun getError(): String {
        return if (hasErrorFromHub) (responseJson as JsonObject).get("error").asString else ""
    }

    //TODO: a sample error response.
    //    ["response",{"tag":"zI5Q6Hgr7wGm9+wmY3iPX826JOlmXdhrd0bgtFuZruU=",
    // "response":{"error":"I'm light, cannot subscribe you to updates"}}]

}

