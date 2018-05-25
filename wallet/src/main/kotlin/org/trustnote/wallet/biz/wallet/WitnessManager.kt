package org.trustnote.wallet.biz.wallet

import com.google.gson.JsonArray
import org.trustnote.db.DbHelper
import org.trustnote.db.entity.MyWitnesses
import org.trustnote.wallet.network.HubManager
import org.trustnote.wallet.network.pojo.HubResponse
import org.trustnote.wallet.network.pojo.MSG_TYPE
import org.trustnote.wallet.network.pojo.ReqGetMyWitnesses

object WitnessManager {

    //TODO: bug: never have chance to update the witnesses.

    fun getMyWitnesses(): Array<MyWitnesses> {

        var res = DbHelper.getMyWitnesses()
        if (res.isEmpty()) {
            val hubModel = HubManager.instance.getCurrentHub()
            val req = ReqGetMyWitnesses(hubModel.getRandomTag())
            hubModel.mHubClient.sendHubMsg(req)
            saveMyWitnesses(req.getResponse())

            res = DbHelper.getMyWitnesses()
        }
        return res

    }

    private fun saveMyWitnesses(hubResponse: HubResponse): List<String> {

        if (hubResponse.msgType == MSG_TYPE.empty) {
            return listOf()
        }

        var myWitnesses = parseArray(hubResponse.responseJson as JsonArray)
        DbHelper.saveMyWitnesses(myWitnesses)
        return myWitnesses
    }

    private fun parseArray(origJson: JsonArray): List<String> {

        return List<String>(origJson.size()) { index: Int ->
            origJson[index].asString
        }
    }

}
