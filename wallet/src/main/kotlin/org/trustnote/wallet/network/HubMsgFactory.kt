package org.trustnote.wallet.network

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import org.trustnote.db.entity.MyAddresses
import org.trustnote.db.entity.MyWitnesses
import org.trustnote.wallet.util.Utils
import com.google.gson.JsonArray
import org.trustnote.wallet.network.pojo.*

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
    const val CMD_GET_PARENT_FOR_NEW_TX = "light/get_parents_and_last_ball_and_witness_list_unit"
    const val CMD_POST_JOINT = "post_joint"

    fun parseMsg(textFromHub: String): HubMsg {
        val index = textFromHub.indexOf(',')
        var msgType = if (index < 3) {
            MSG_TYPE.ERROR
        } else {
            MSG_TYPE.valueOf(textFromHub.substring(2, index - 1))
        }

        when (msgType) {
            MSG_TYPE.ERROR, MSG_TYPE.empty -> return HubMsg(msgType)
            MSG_TYPE.response -> return HubResponse(textFromHub)
            MSG_TYPE.request -> return HubRequest(textFromHub)
            else -> {

            }
        }
        return HubMsg()
    }

    fun walletVersion(): HubJustSaying {
        return HubJustSaying(CMD_VERSION, JsonParser().parse(Utils.getGson().toJson(WalletVersion())) as JsonObject)
    }

    //TODO: handle the err from Hub: "response",{"tag":"RANDOM:-1208010420","response":{"error":"your history is too large, consider switching to a full client"}}]

    fun getParentForNewTx(hubSocketModel: HubSocketModel, witnesses: Array<MyWitnesses>): HubRequest {

        val params = JsonObject()
        val wts = JsonArray()
        for (oneWts in witnesses) {
            wts.add(oneWts.address)
        }

        params.add("witnesses", wts)

        return HubRequest(CMD_GET_PARENT_FOR_NEW_TX, hubSocketModel.getRandomTag(), params)
    }

    fun getPostJointRequest(hubSocketModel: HubSocketModel, units: JsonObject): HubRequest {
        val params = JsonObject()
        params.add("unit", units)
        return HubRequest(CMD_POST_JOINT, hubSocketModel.getRandomTag(), params)
    }

}


