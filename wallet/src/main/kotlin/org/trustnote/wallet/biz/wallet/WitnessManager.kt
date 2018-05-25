package org.trustnote.wallet.biz.wallet

import org.trustnote.db.DbHelper
import org.trustnote.db.entity.MyWitnesses
import org.trustnote.wallet.network.HubManager
import org.trustnote.wallet.network.pojo.ReqGetMyWitnesses

object WitnessManager {

    fun getMyWitnesses(): Array<MyWitnesses>{

        val res = DbHelper.getMyWitnesses()
        if (res.isEmpty()) {
            val hubModel = HubManager.instance.getCurrentHub()
            val req = ReqGetMyWitnesses(hubModel.mGetWitnessTag)
            hubModel.mHubClient.sendHubMsg(req)
            req.getResponse()
        }
        return res

    }
}
