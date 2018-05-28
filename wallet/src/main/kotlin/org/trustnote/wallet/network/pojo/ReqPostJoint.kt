package org.trustnote.wallet.network.pojo

import com.google.gson.JsonObject
import org.trustnote.wallet.network.HubMsgFactory.CMD_POST_JOINT

class ReqPostJoint : HubRequest {

    constructor(reqId: String, units: JsonObject) : super(CMD_POST_JOINT, reqId) {

        val params = JsonObject()
        params.add("unit", units)

        setReqParams(params)
    }

    override fun handleResponse(): Boolean {

        return true
    }

}