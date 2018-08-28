package org.trustnote.superwallet.network

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import org.trustnote.superwallet.biz.js.JSApi
import org.trustnote.superwallet.biz.wallet.WalletManager
import org.trustnote.superwallet.util.Utils
import org.trustnote.superwallet.network.pojo.*

object HubMsgFactory {

    const val TAG = "tag"
    const val RESPONSE = "response"
    const val COMMAND = "command"
    const val PARAMS = "params"
    const val BODY = "body"
    const val SUBJECT = "subject"

    const val CMD_HEARTBEAT = "heartbeat"
    const val CMD_VERSION = "version"

    const val CMD_NEW_VERSION_FROM_HUB = "new_version"

    const val CMD_GET_WITNESSES = "get_witnesses"
    const val CMD_GET_HISTORY = "light/get_history"
    const val CMD_GET_PARENT_FOR_NEW_TX = "light/get_parents_and_last_ball_and_witness_list_unit"
    const val CMD_POST_JOINT = "post_joint"
    const val CMD_GET_TEMP_PUBKEY = "hub/get_temp_pubkey"
    const val CMD_UPDATE_MY_TEMP_PUBKEY = "hub/temp_pubkey"
    const val CMD_DELIVER = "hub/deliver"

    const val SUBJECT_HUB_CHALLENGE = "hub/challenge"
    const val SUBJECT_HUB_LOGIN = "hub/login"
    const val SUBJECT_HUB_MESSAGE = "hub/message"
    const val SUBJECT_LIGHT_HAVE_UPDATES = "light/have_updates"
    const val SUBJECT_JOINT = "joint"
    const val SUBJECT_HUB_DELETE = "hub/delete"

    fun parseHubMsg(hubAddress: String, textFromHub: String): HubMsg {

        val res: HubMsg = when (parseMsgType(textFromHub)) {
            MSG_TYPE.ERROR, MSG_TYPE.empty -> HubMsg(MSG_TYPE.ERROR)
            MSG_TYPE.response -> HubResponse(textFromHub)
            MSG_TYPE.request -> HubRequest(textFromHub)
            MSG_TYPE.justsaying -> HubJustSaying(textFromHub)

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

    fun signOneString(key: String, value: String): JsonObject {
        val api = JSApi()
        val jsonObject = JsonObject()
        jsonObject.addProperty(key, value)
        jsonObject.addProperty("pubkey", WalletManager.model.mProfile.pubKeyForPairId)
        val b64Hash = api.getDeviceMessageHashToSignSync(jsonObject.toString())
        val signature = api.signSync(b64Hash, WalletManager.model.mProfile.privKeyForPairId, "null")
        jsonObject.addProperty("signature", signature)

        return jsonObject

    }


}


