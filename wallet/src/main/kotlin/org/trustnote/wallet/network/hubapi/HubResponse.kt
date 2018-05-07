package org.trustnote.wallet.network.hubapi

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import org.trustnote.db.DbHelper
import org.trustnote.wallet.util.Utils
import org.trustnote.wallet.walletadmin.WalletModel


class HubResponse : HubMsg {

    val tag: String
    val responseJson: JsonElement

    constructor(textFromHub: String) : super(textFromHub) {
        tag = msgJson.getAsJsonPrimitive(HubMsgFactory.TAG).asString

        responseJson = msgJson.get(HubMsgFactory.RESPONSE) ?: Utils.emptyJsonObject
    }

    constructor(responseJson: JsonObject, tag: String) : super(MSG_TYPE.response) {
        this.msgSource = MSG_SOURCE.wallet
        this.responseJson = responseJson
        this.tag = tag
    }

    fun handResonse(hubSocketModel: HubSocketModel): Boolean {

        return hubSocketModel.responseArrived(this)
    }




}

