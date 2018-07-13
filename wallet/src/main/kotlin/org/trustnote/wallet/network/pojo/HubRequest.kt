package org.trustnote.wallet.network.pojo

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import org.trustnote.wallet.network.HubModel
import org.trustnote.wallet.network.HubMsgFactory
import org.trustnote.wallet.util.Utils
import java.util.concurrent.CountDownLatch

open class HubRequest : HubMsg {

    //TODO: for some request, we need a timeout logic to let caller has a chance to handle the err case.
    //TODO: for other request, we need continue retry until success.

    val command: String
    var params: JsonElement = Utils.emptyJsonObject
    val tag: String
    var attachedInfo: Object = Object()
    var canUseBackupHub = true

    @Volatile
    lateinit var hubResponse: HubResponse
    val latch = CountDownLatch(1)

    constructor(textFromHub: String) : super(textFromHub) {
        tag = msgJson.getAsJsonPrimitive(HubMsgFactory.TAG).asString
        command = msgJson.getAsJsonPrimitive(HubMsgFactory.COMMAND).asString
        params = msgJson.getAsJsonObject(HubMsgFactory.PARAMS)
    }

    constructor(command: String, tag: String, params: JsonObject = Utils.emptyJsonObject) : super(MSG_TYPE.request) {
        this.msgSource = MSG_SOURCE.wallet
        this.command = command
        this.tag = tag
        setReqParams(params)
    }

    constructor(msgType: MSG_TYPE) : super(msgType) {
        this.msgSource = MSG_SOURCE.wallet
        this.command = ""
        this.tag = ""
    }

    fun setReqParams(params: JsonElement) {

        this.params = params
        this.msgJson = JsonObject()
        this.msgJson.addProperty(HubMsgFactory.COMMAND, command)
        this.msgJson.add(HubMsgFactory.PARAMS, params)
        this.msgJson.addProperty(HubMsgFactory.TAG, tag)

    }

    @Synchronized
    open fun setResponse(hubResponse: HubResponse) {
        if (latch.count == 0L) {
            return
        }
        Utils.debugLog("setResponse from::" + this.toString())
        this.hubResponse = hubResponse
        latch.countDown()
    }

    fun getResponse(): HubResponse {
        latch.await()
        Utils.debugLog("getResponse after await from::" + this.toString())
        return hubResponse
    }

    open fun handleResponse(): Boolean {
        return true
    }

    fun isAccepted(): Boolean {

        getResponse()

        if (hubResponse.msgType == MSG_TYPE.response && hubResponse.responseJson is JsonPrimitive) {
            return "accepted" == hubResponse.responseJson.asString
        }
        return false
    }

}
