package org.trustnote.wallet.network.hubapi

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import org.trustnote.db.DbHelper
import org.trustnote.wallet.network.RequestMap
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

        var handleResult = true
        val originRequset = hubSocketModel.mRequestMap.getHubRequest(tag)

        when (originRequset.msgType) {
            MSG_TYPE.ERROR -> return false
            MSG_TYPE.request -> handleResonseInternally(originRequset, hubSocketModel)
        }

        if (handleResult) {
            hubSocketModel.mRequestMap.remove(this)
        }

        return handleResult
    }

    private fun handleResonseInternally(originRequset: HubRequest, hubSocketModel: HubSocketModel): Boolean {

        var handleResult = true
        when (originRequset.command) {
            HubMsgFactory.CMD_GET_WITNESSES -> handleResult = handleMyWitnesses()
            HubMsgFactory.CMD_GET_HISTORY -> handleResult = handleGetHistory()
            HubMsgFactory.CMD_GET_PARENT_FOR_NEW_TX -> handleResult = handleGetParentForNewTx()
        }

        return handleResult
    }

    private fun handleMyWitnesses(): Boolean {
        DbHelper.saveMyWitnesses(this)
        return true
    }

    private fun handleGetHistory(): Boolean {
        DbHelper.saveUnit(this)
        return true
    }

    private fun handleGetParentForNewTx(): Boolean {
        WalletModel.instance.composeNewTx(this)
        return true
    }



}

