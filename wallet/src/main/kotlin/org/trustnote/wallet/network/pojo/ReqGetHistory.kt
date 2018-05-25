package org.trustnote.wallet.network.pojo

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import org.trustnote.db.entity.MyAddresses
import org.trustnote.db.entity.MyWitnesses
import org.trustnote.wallet.network.HubMsgFactory.CMD_GET_HISTORY

class ReqGetHistory : HubRequest {

    constructor(reqId: String, witnesses: Array<MyWitnesses>, myAddresses: Array<MyAddresses>) : super(CMD_GET_HISTORY, reqId) {
        val params = JsonObject()
        val wts = JsonArray()
        for (oneWts in witnesses) {
            wts.add(oneWts.address)
        }

        val ads = JsonArray()
        for (oneAds in myAddresses) {
            ads.add(oneAds.address)
        }

        params.add("addresses", ads)
        params.add("witnesses", wts)

        setReqParams(params)
    }

    override fun handleResponse(): Boolean {

        return true
    }

}