package org.trustnote.wallet.network.hubapi


object HubMsgFactory {

    const val TAG = "tag"
    const val RESPONSE = "response"
    const val COMMAND = "command"
    const val PARAMS = "params"
    const val BODY = "msgJson"
    const val SUBJECT = "subject"
    const val CMD_HEARTBEAT = "heartbeat"

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



}


//companion object {
//
//    val TYPE_Justsaying = "justsaying"
//    val TYPE_Status_connected = "socketconnected"
//    val TYPE_Status_closed = "socketclosed"
//    val KEY_Subject = "mSubject"
//    val KEY_CHALLENGE = "hub/challenge"
//    val KEY_Body = "msgJson"
//
//    internal fun parseResponse(hubMsg: String, reqTagMapping: RequestMap): HubResponse? {
//
//        val index = hubMsg.indexOf(',')
//
//        if (index < 3) {
//            return null
//        } else {
//            val res = HubResponse()
//            res.msgType = HubMsg.MSG_TYPE.valueOf(hubMsg.substring(2, index - 1))
//            res.content = hubMsg.substring(index + 1, hubMsg.length - 1)
//            res.msgJson = JsonParser().parse(res.content).asJsonObject
//            if (MSG_TYPE.justsaying == res.msgType) {
//                if (KEY_CHALLENGE == res.msgJson.getAsJsonPrimitive(KEY_Subject).asString) {
//                    res.subjectType = HubMsg.BODY_TYPE.RES_CHALLENGE
//                }
//            }
//
//            if (HubMsg.MSG_TYPE.response == res.msgType) {
//                val tag = res.msgJson.get("tag").asString
//                res.subjectType = reqTagMapping.getExpectedResBodyType(tag)
//            }
//
//            return res
//        }
//    }
//
//    fun createConnectedInstance(): HubResponse {
//        val res = HubResponse()
//        res.msgType = HubMsg.MSG_TYPE.CONNECTED
//        return res
//    }
//
//    fun createCloseInstance(): HubResponse {
//        val res = HubResponse()
//        res.msgType = HubMsg.MSG_TYPE.CLOSED
//        return res
//    }
//}
//}
