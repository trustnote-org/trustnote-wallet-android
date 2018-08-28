package org.trustnote.superwallet.network.pojo

import com.google.gson.JsonObject
import org.trustnote.superwallet.network.HubModel
import org.trustnote.superwallet.network.HubMsgFactory.CMD_POST_JOINT

class ReqPostJoint : HubRequest {

    constructor(units: JsonObject) : super(CMD_POST_JOINT, tag = HubModel.instance.getRandomTag()) {

        val params = JsonObject()
        params.add("unit", units)

        setReqParams(params)
    }

    override fun handleResponse(): Boolean {

        return true
    }

}