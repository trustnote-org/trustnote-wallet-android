package org.trustnote.wallet.network.pojo

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import org.trustnote.db.entity.MyWitnesses
import org.trustnote.wallet.network.HubMsgFactory.CMD_GET_PARENT_FOR_NEW_TX
import org.trustnote.wallet.util.Utils

class ReqGetParents : HubRequest {

    constructor(reqId: String, witnesses: Array<MyWitnesses>) : super(CMD_GET_PARENT_FOR_NEW_TX, reqId) {
        val params = JsonObject()
        val wts = JsonArray()
        for (oneWts in witnesses) {
            wts.add(oneWts.address)
        }

        params.add("witnesses", wts)

        setReqParams(params)
    }


    @Synchronized
    override fun setResponse(hubResponse: HubResponse) {
        if (latch.count == 0L) {
            return
        }
        Utils.debugLog("setResponse from::" + this.toString())
        this.hubResponse = hubResponse
        latch.countDown()
    }

    override fun handleResponse(): Boolean {
        return true
    }

}