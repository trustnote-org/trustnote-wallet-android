package org.trustnote.wallet.network.hubapi

import com.google.gson.JsonParser

import org.trustnote.wallet.network.ReqTagMapping


class HubResponse : HubPackageBase() {

    val challenge: String
        get() {
            if (subjectType != HubPackageBase.BODY_TYPE.RES_CHALLENGE) {
                throw IllegalStateException("Msg is not RES_CHALLENGE")
            }
            return body.get(KEY_Body).asString
        }

    companion object {

        val TYPE_Justsaying = "justsaying"
        val TYPE_Status_connected = "socketconnected"
        val TYPE_Status_closed = "socketclosed"
        val KEY_Subject = "mSubject"
        val KEY_CHALLENGE = "hub/challenge"
        val KEY_Body = "body"

        internal fun parseResponse(hubMsg: String, reqTagMapping: ReqTagMapping): HubResponse? {

            val index = hubMsg.indexOf(',')

            if (index < 3) {
                return null
            } else {
                val res = HubResponse()
                res.msgType = HubPackageBase.MSG_TYPE.valueOf(hubMsg.substring(2, index - 1))
                res.content = hubMsg.substring(index + 1, hubMsg.length - 1)
                res.body = JsonParser().parse(res.content).asJsonObject
                if (MSG_TYPE.justsaying == res.msgType) {
                    if (KEY_CHALLENGE == res.body.getAsJsonPrimitive(KEY_Subject).asString) {
                        res.subjectType = HubPackageBase.BODY_TYPE.RES_CHALLENGE
                    }
                }

                if (HubPackageBase.MSG_TYPE.response == res.msgType) {
                    val tag = res.body.get("tag").asString
                    res.subjectType = reqTagMapping.getExpectedResBodyType(tag)
                }

                return res
            }
        }


        fun createConnectedInstance(): HubResponse {
            val res = HubResponse()
            res.msgType = HubPackageBase.MSG_TYPE.CONNECTED
            return res
        }

        fun createCloseInstance(): HubResponse {
            val res = HubResponse()
            res.msgType = HubPackageBase.MSG_TYPE.CLOSED
            return res
        }
    }
}
