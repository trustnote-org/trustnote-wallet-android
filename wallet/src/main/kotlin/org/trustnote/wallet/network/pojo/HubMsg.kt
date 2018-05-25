package org.trustnote.wallet.network.pojo


import com.google.gson.JsonObject
import com.google.gson.JsonParser
import org.trustnote.wallet.util.Utils

open class HubMsg {

    var msgSource = MSG_SOURCE.hub
    var msgType: MSG_TYPE = MSG_TYPE.empty
    var msgString: String = ""
    var msgJson: JsonObject = Utils.emptyJsonObject
    var textFromHub: String = ""
    var lastSentTime = 0L
    var shouldRetry = false

    constructor(msgType: MSG_TYPE = MSG_TYPE.empty) {
        this.msgType = msgType
    }

    constructor(textFromHub: String) {
        this.textFromHub = textFromHub

        val index = textFromHub.indexOf(',')

        if (index < 3) {
            msgType = MSG_TYPE.ERROR
            return
        } else {
            msgType = MSG_TYPE.valueOf(textFromHub.substring(2, index - 1))
            msgString = textFromHub.substring(index + 1, textFromHub.length - 1)
            msgJson = JsonParser().parse(msgString).asJsonObject
        }
    }

    fun toHubString(): String {
        return """["${msgType}",${msgJson}]"""
    }

}


enum class MSG_TYPE {
    empty,
    request,
    response,
    justsaying,
    CONNECTED,
    CLOSED,
    ERROR
}


enum class MSG_SOURCE {
    wallet,
    hub
}
