package org.trustnote.wallet.network

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import org.trustnote.wallet.util.Utils
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

    fun parseMsg(hubAddress: String, textFromHub: String): HubMsg {

        val res: HubMsg = when (parseMsgType(textFromHub)) {
            MSG_TYPE.ERROR, MSG_TYPE.empty -> HubMsg(MSG_TYPE.ERROR)
            MSG_TYPE.response -> HubResponse(textFromHub)
            MSG_TYPE.request -> HubRequest(textFromHub)

            else -> {
                HubMsg(MSG_TYPE.unknown)
            }
        }

        res.actualHubAddress = hubAddress

        return res

    }

    private fun parseMsgType(textFromHub: String): MSG_TYPE {
        var msgType = MSG_TYPE.ERROR
        val index = textFromHub.indexOf(',')
        try {

            msgType = MSG_TYPE.valueOf(textFromHub.substring(2, index - 1))

        } catch (e: RuntimeException) {

            Utils.debugHub(e.localizedMessage)

        }
        return msgType
    }

    fun walletVersion(): HubJustSaying {
        return HubJustSaying(CMD_VERSION, JsonParser().parse(Utils.getGson().toJson(WalletVersion())) as JsonObject)
    }

}


