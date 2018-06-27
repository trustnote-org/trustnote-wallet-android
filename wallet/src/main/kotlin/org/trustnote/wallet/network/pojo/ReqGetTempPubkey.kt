package org.trustnote.wallet.network.pojo

import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import org.trustnote.wallet.network.HubModel
import org.trustnote.wallet.network.HubMsgFactory.CMD_GET_TEMP_PUBKEY

class ReqGetTempPubkey : HubRequest {


    //    ["request",{"command":"hub/get_temp_pubkey",
    // "params":"A4WANK+q3yKjY1zrFEHDnAePNte1JazA3dppW3XZB7+9",
    // "tag":"l8lMro64mYriosRR9Upp6gCFj1Tgm/7jlz5dGQu15Ek="}]
    constructor(pubkey: String) : super(CMD_GET_TEMP_PUBKEY, tag = HubModel.instance.getRandomTag()) {
        val params = JsonPrimitive(pubkey)
        setReqParams(params)
    }

    fun getTempPubkey(): String {
        return (getResponse().responseJson as JsonObject).get("temp_pubkey").asString
    }

    //["response",{"tag":"l8lMro64mYriosRR9Upp6gCFj1Tgm/7jlz5dGQu15Ek=",
    // "response":{"temp_pubkey":"Atc1msG8z4HTTJyT61WKHtL6A4xQLrSmSMaclvZtJdYJ",
    // "pubkey":"A4WANK+q3yKjY1zrFEHDnAePNte1JazA3dppW3XZB7+9",
    // "signature":"1jAOY9NEh26LwfAvfJIF7r80qwa/3sjcdOyH7cWBclMHzuffjlHb7IwPXWQ5o7HI+e/UmoqhxYT9BBZgyTEqLQ=="}}]


}