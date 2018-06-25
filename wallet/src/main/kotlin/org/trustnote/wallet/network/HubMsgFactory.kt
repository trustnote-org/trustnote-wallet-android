package org.trustnote.wallet.network

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import org.trustnote.wallet.biz.js.JSApi
import org.trustnote.wallet.util.Utils
import org.trustnote.wallet.network.pojo.*

object HubMsgFactory {

    const val TAG = "tag"
    const val RESPONSE = "response"
    const val COMMAND = "command"
    const val PARAMS = "params"
    const val BODY = "body"
    const val SUBJECT = "subject"

    const val CMD_HEARTBEAT = "heartbeat"
    const val CMD_VERSION = "version"
    const val CMD_GET_WITNESSES = "get_witnesses"
    const val CMD_GET_HISTORY = "light/get_history"
    const val CMD_GET_PARENT_FOR_NEW_TX = "light/get_parents_and_last_ball_and_witness_list_unit"
    const val CMD_POST_JOINT = "post_joint"

    const val SUBJECT_HUB_CHALLENGE = "hub/challenge"
    const val SUBJECT_HUB_LOGIN = "hub/login"

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

    //    ["justsaying",{"subject":"hub/login","body":{"challenge":"m0dxaegeZyT/GZI//j2cMK0CdK57iF6dLqEI51gk",
    // "pubkey":"Aki0PI8ouQau9uUATpGMwJVCFyZBw+tOkcfw34KioqTS",
    // "signature":"f3++Cx1+gv4KC2Hf8fWoDZ65nKdhSuOJ0CfACOYfM9p3biURsVWAD9xtUX537AwBRMwksSzuOZBWibCR+W3N6w=="}}]
    fun composeLoginBody(challenge: String, pubkey: String, privKey: String): JsonObject  {

        val api = JSApi()
        val jsonObject = JsonObject()
        jsonObject.addProperty("challenge", challenge)
        jsonObject.addProperty("pubkey", pubkey)
        val b64Hash = api.getDeviceMessageHashToSignSync(jsonObject.toString())
        val signature = api.signSync(b64Hash, privKey, "null")
        jsonObject.addProperty("signature", signature)

        return jsonObject

    }

}


