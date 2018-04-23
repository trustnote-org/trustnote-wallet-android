package org.trustnote.wallet.network.hubapi

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import org.trustnote.db.entity.MyAddresses
import org.trustnote.db.entity.MyWitnesses
import org.trustnote.wallet.util.Utils
import com.google.gson.JsonArray

object HubMsgFactory {

    const val TAG = "tag"
    const val RESPONSE = "response"
    const val COMMAND = "command"
    const val PARAMS = "params"
    const val BODY = "msgJson"
    const val SUBJECT = "subject"
    const val CMD_HEARTBEAT = "heartbeat"
    const val CMD_VERSION = "version"
    const val CMD_GET_WITNESSES = "get_witnesses"
    const val CMD_GET_HISTORY = "light/get_history"

    fun parseMsg(textFromHub: String): HubMsg {
        val index = textFromHub.indexOf(',')
        var msgType = MSG_TYPE.empty
        if (index < 3) {
            msgType = MSG_TYPE.ERROR
        } else {
            msgType = MSG_TYPE.valueOf(textFromHub.substring(2, index - 1))
        }

        when (msgType) {
            MSG_TYPE.ERROR, MSG_TYPE.empty -> return HubMsg(msgType)
            MSG_TYPE.response -> return HubResponse(textFromHub)
            MSG_TYPE.request -> return HubRequest(textFromHub)
        }
        return HubMsg()
    }

    fun walletHeartBeat(hubSocketModel: HubSocketModel): HubRequest {
        return HubRequest(CMD_HEARTBEAT, hubSocketModel.mHeartbeatTag)
    }

    fun walletVersion(): HubJustSaying {
        return HubJustSaying(CMD_VERSION, JsonParser().parse(Utils.getGson().toJson(WalletVersion())) as JsonObject)
    }

    fun getWitnesses(hubSocketModel: HubSocketModel): HubRequest {
        return HubRequest(CMD_GET_WITNESSES, hubSocketModel.mGetWitnessTag)
    }


    //TODO: handle the err from Hub: "response",{"tag":"RANDOM:-1208010420","response":{"error":"your history is too large, consider switching to a full client"}}]
    fun getHistory(hubSocketModel: HubSocketModel, witnesses: Array<MyWitnesses>, myAddresses: Array<MyAddresses>): HubRequest {

        val params = JsonObject()
        val wts = JsonArray()
        for(oneWts in witnesses) {
            wts.add(oneWts.address)
        }

        val ads = JsonArray()
        for(oneAds in myAddresses) {
            ads.add(oneAds.address)
        }

        params.add("addresses", ads)
        params.add("witnesses", wts)

        return HubRequest(CMD_GET_HISTORY, hubSocketModel.mGetHistoryTag, params)
    }


}


