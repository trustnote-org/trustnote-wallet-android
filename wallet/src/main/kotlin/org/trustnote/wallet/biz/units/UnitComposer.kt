package org.trustnote.wallet.biz.units

import android.os.Bundle
import com.google.gson.JsonObject
import org.trustnote.db.DbHelper
import org.trustnote.db.FundedAddress
import org.trustnote.db.Payload
import org.trustnote.db.entity.*
import org.trustnote.wallet.biz.ActivityMain
import org.trustnote.wallet.biz.TTT
import org.trustnote.wallet.biz.home.FragmentMainWalletTxDetail
import org.trustnote.wallet.biz.js.JSApi
import org.trustnote.wallet.biz.wallet.PaymentInfo
import org.trustnote.wallet.biz.wallet.WalletManager
import org.trustnote.wallet.network.HubModel
import org.trustnote.wallet.network.pojo.ReqGetParents
import org.trustnote.wallet.network.pojo.ReqPostJoint
import org.trustnote.wallet.util.MyThreadManager
import org.trustnote.wallet.util.Utils

class UnitComposer(val sendPaymentInfo: PaymentInfo) {
    private val units = Units()
    private val messages = Messages()
    private val payload = Payload()
    private val receiverOutput = Outputs()
    private val changeOutput = Outputs()
    private val authors = mutableListOf<Authentifiers>()

    lateinit var mGetParentRequest: ReqGetParents

    var hashToSign = ""
    var signFailed = false

    lateinit var changeAddress: String
    private val jsApi = JSApi()

    fun isOkToSendTx(): Boolean {

        if (WalletManager.model.mGetParentRequest == null) {
            return false
        } else {
            mGetParentRequest = WalletManager.model.mGetParentRequest!!
            if (mGetParentRequest.getResponse().hasError()) {
                return false
            }
            return true
        }
    }

    fun startSendTx(activity: ActivityMain, password: String = "") {

        if (authors.isEmpty()) {
            //TODO: this kind of fail
            showFail()
            //Maybe re-generate input.
            return
        }


        MyThreadManager.instance.runInBack {

            val credential = WalletManager.model.findWallet(sendPaymentInfo.walletId)
            if (password.isNotEmpty() && !credential.isObserveOnly) {
                signWithEveryAuthors(password)
            } else {

            }

            if (signFailed) {
                showFail()
                return@runInBack
            }

            units.unit = jsApi.getUnitHashSync(unitToGsonString(units))

            if (postNewUnitToHub()) {
                val unitJson = unitToGsonObject(units)
                val unit = UnitsManager().parseUnitFromJson(unitJson, listOf())

                WalletManager.model.newUnitAcceptedByHub(unit, sendPaymentInfo.walletId)

                if (sendPaymentInfo.amount < 10 && sendPaymentInfo.textMessage.isNotEmpty()) {
                    //Go to detail for text message
                    val bundle = Bundle()
                    bundle.putString(TTT.KEY_WALLET_ID, credential.walletId)
                    bundle.putInt(TTT.KEY_TX_INDEX, 0)
                    val f = FragmentMainWalletTxDetail()
                    f.arguments = bundle
                    activity.addL2Fragment(f)
                }


                units.unit

            } else {
                showFail()
            }
        }
    }

    private fun unitToGsonString(units: Units): String {
        return unitToGsonObject(units).toString()
    }

    private fun unitToGsonObject(units: Units): JsonObject {
        val unitJson = Utils.toGsonObject(units)
        addTextMessage(unitJson)
        return unitJson
    }

    private fun addTextMessage(unitJson: JsonObject) {
        //The text message always the second message.
        if (sendPaymentInfo.textMessage.isEmpty()) {
            return
        }

        val textMessage = Messages()
        textMessage.app = TTT.unitMsgTypeText
        textMessage.payloadLocation = TTT.unitPayloadLoationInline
        textMessage.payloadHash = JSApi().getBase64HashForStringSync(sendPaymentInfo.textMessage)

        val textMsgJson = Utils.toGsonObject(textMessage)
        textMsgJson.remove("payload")
        textMsgJson.addProperty("payload", sendPaymentInfo.textMessage)

        unitJson.getAsJsonArray("messages").add(textMsgJson)

    }

    private fun genPayloadInputs() {

        payload.inputs.clear()

        val fundedAddress = DbHelper.queryFundedAddressesByAmount(sendPaymentInfo.walletId, sendPaymentInfo.amount)
        val filterFundedAddress = filterMostFundedAddresses(fundedAddress, sendPaymentInfo.amount)
        val addresses = mutableListOf<String>()

        filterFundedAddress.forEach { addresses.add(it.address) }

        val outputs = DbHelper.queryUtxoByAddress(addresses, sendPaymentInfo.lastBallMCI)
        val res = mutableListOf<Inputs>()
        outputs.forEach {
            val inputs = Inputs()
            inputs.srcUnit = it.unit
            inputs.srcMessageIndex = it.messageIndex
            inputs.srcOutputIndex = it.outputIndex
            inputs.amount = it.amount
            inputs.address = it.address
            res.add(inputs)
        }

        payload.inputs = res
    }

    private fun filterMostFundedAddresses(rows: Array<FundedAddress>, estimatedAmount: Long): List<FundedAddress> {
        if (estimatedAmount <= 0) {
            return rows.asList()
        }
        val res = mutableListOf<FundedAddress>()
        var accumulatedAmount = 0L

        for (it in rows) {
            res.add(it)
            accumulatedAmount += it.total
            if (accumulatedAmount > estimatedAmount + TTT.MAX_FEE) {
                return res
            }
        }
        return res
    }

    private fun initUnits() {

        receiverOutput.address = sendPaymentInfo.receiverAddress
        receiverOutput.amount = sendPaymentInfo.amount

        changeAddress = queryOrIssueNotUsedChangeAddress()
        changeOutput.address = changeAddress
        changeOutput.amount = TTT.PLACEHOLDER_AMOUNT

        payload.outputs.clear()
        payload.outputs.clear()

        payload.outputs.add(receiverOutput)
        payload.outputs.add(changeOutput)

        payload.outputs = payload.outputs.sortedBy { it.address }

        messages.payload = payload
        messages.payloadHash = TTT.PLACEHOLDER_HASH
        messages.app = TTT.unitMsgTypePayment
        messages.payloadLocation = TTT.unitPayloadLoationInline

        units.messages = mutableListOf<Messages>()
        units.messages.add(messages)

        units.authenfiers = authors

    }

    fun composeUnits() {

        val responseJson = mGetParentRequest.getResponse().responseJson as JsonObject

        sendPaymentInfo.lastBallMCI = responseJson.get("last_stable_mc_ball_mci").asInt

        initUnits()

        units.parentUnits = responseJson.getAsJsonArray("parent_units")
        units.lastBall = responseJson.get("last_stable_mc_ball").asString
        units.lastBallUnit = responseJson.get("last_stable_mc_ball_unit").asString
        units.witnessListUnit = responseJson.get("witness_list_unit").asString
        units.headersCommission = TTT.PLACEHOLDER_AMOUNT
        units.payloadCommission = TTT.PLACEHOLDER_AMOUNT
        units.unit = TTT.PLACEHOLDER_HASH
        units.creationDate = System.currentTimeMillis() / 1000L

        genPayloadInputs()
        genAuthors()

        genCommission()

        updateTransferValueIfNotEnoughCommission()

        genChange()
        genPayloadHash()

        hashToSign = jsApi.getUnitHashToSignSync(unitToGsonString(units))

        Utils.debugLog(unitToGsonString(units))
    }



    private var isAlreadyUpdateTransferValue: Boolean = false

    private fun updateTransferValueIfNotEnoughCommission() {

        if (isAlreadyUpdateTransferValue) {

            return

        } else {

            val credential = WalletManager.model.findWallet(sendPaymentInfo.walletId)
            if (credential.balance >= sendPaymentInfo.amount + units.headersCommission + units.payloadCommission) {
                return
            } else {
                sendPaymentInfo.amount = credential.balance - (units.headersCommission + units.payloadCommission) -1

                receiverOutput.amount = sendPaymentInfo.amount
                changeOutput.amount = 1
            }
            isAlreadyUpdateTransferValue = true
        }

    }

    fun getOneUnSignedAuthentifier(): Authentifiers? {
        var res: Authentifiers? = null
        authors.forEach {
            val currentSign = it.authentifiers.get("r")?.asString
            if (currentSign.isNullOrBlank() || currentSign == TTT.PLACEHOLDER_SIG) {
                res = it
            }
        }
        return res
    }

    private fun genChange() {
        var totalInput = 0L
        payload.inputs.forEach { totalInput += it.amount }

        changeOutput.amount = (totalInput - sendPaymentInfo.amount - units.payloadCommission - units.headersCommission)

        Utils.debugLog("after genChange")
        Utils.debugLog(Utils.toGsonString(payload))

    }

    private fun signWithEveryAuthors(password: String) {
        authors.forEach {
            val myAddresses = DbHelper.queryAddressByAddresdId(it.address)
            val sign = jsApi.signSync(hashToSign, WalletManager.model.getPrivKey(password), Utils.genBip44Path(myAddresses))

            if (sign.isEmpty() || "0" == sign) {
                signFailed = true
            }

            it.authentifiers.remove("r")
            it.authentifiers.addProperty("r", sign)
        }

    }

    private fun genPayloadHash() {

        Utils.debugLog("befroe genPayloadHash")
        Utils.debugLog(Utils.toGsonString(payload))

        messages.payloadHash = JSApi().getBase64HashSync(Utils.toGsonString(payload))
    }

    private fun genCommission() {
        units.headersCommission = jsApi.getHeadersSizeSync(unitToGsonString(units)).toLong()
        units.payloadCommission = jsApi.getTotalPayloadSizeSync(unitToGsonString(units)).toLong()
    }

    private fun genAuthors() {
        authors.clear()

        val addressList = mutableListOf<String>()
        payload.inputs.forEach { addressList.add(it.address) }

        val myAddressesArray = DbHelper.queryAddress(addressList.toList())
        myAddressesArray.forEach {
            val authentifiers = Authentifiers()
            authentifiers.address = it.address
            if (!DbHelper.hasDefinitions(authentifiers.address)) {
                authentifiers.definition = Utils.parseJsonArray(it.definition)
            }
            authentifiers.authentifiers = Utils.genJsonObject("r", TTT.PLACEHOLDER_SIG)
            authors.add(authentifiers)
        }

        if (payload.inputs.size > 1) {
            genCommissionReceipts()
        }
    }

    private fun genCommissionReceipts() {
        val commissionRecipient = CommissionRecipients()
        commissionRecipient.address = changeAddress
        units.commissionRecipients = listOf(commissionRecipient)
    }

    private fun queryOrIssueNotUsedChangeAddress(): String {
        //How about it cannot find unused change address
        return WalletManager.model.findNextUnusedChangeAddress(sendPaymentInfo.walletId).address
    }

    private fun postNewUnitToHub(): Boolean {

        val req = ReqPostJoint(unitToGsonObject(units))
        HubModel.instance.sendHubMsg(req)

        val hubResponse = req.getResponse()

        return req.isAccepted()

    }

    //TODO: how to notify UI.
    fun showFail() {
        Utils.toastMsg("发送失败")
    }

}