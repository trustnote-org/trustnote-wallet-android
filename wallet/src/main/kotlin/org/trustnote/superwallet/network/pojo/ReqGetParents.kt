package org.trustnote.superwallet.network.pojo

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import org.trustnote.db.entity.MyWitnesses
import org.trustnote.superwallet.network.HubModel
import org.trustnote.superwallet.network.HubMsgFactory.CMD_GET_PARENT_FOR_NEW_TX
import org.trustnote.superwallet.util.Utils

class ReqGetParents : HubRequest {

    constructor(witnesses: Array<MyWitnesses>) : super(CMD_GET_PARENT_FOR_NEW_TX, tag = HubModel.instance.getRandomTag()) {
        val params = JsonObject()
        val wts = JsonArray()
        for (oneWts in witnesses) {
            wts.add(oneWts.address)
        }

        params.add("witnesses", wts)

        setReqParams(params)
    }


}