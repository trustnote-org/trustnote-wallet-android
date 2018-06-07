package org.trustnote.wallet.biz.units

import com.google.gson.JsonObject
import org.trustnote.db.DbHelper
import org.trustnote.db.FundedAddress
import org.trustnote.db.Payload
import org.trustnote.db.entity.*
import org.trustnote.wallet.biz.ActivityMain
import org.trustnote.wallet.biz.TTT
import org.trustnote.wallet.biz.js.JSApi
import org.trustnote.wallet.biz.wallet.PaymentInfo
import org.trustnote.wallet.biz.wallet.WalletManager
import org.trustnote.wallet.biz.wallet.WitnessManager
import org.trustnote.wallet.network.HubManager
import org.trustnote.wallet.network.pojo.MSG_TYPE
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

    var hashToSign = ""

    lateinit var mGetParentRequest: ReqGetParents
    private val jsApi = JSApi()

    fun isOkToSendTx(): Boolean {

        val witnesses = WitnessManager.getMyWitnesses()
        if (witnesses.isEmpty()) {
            return false
        }

        val hubModel = HubManager.instance.getCurrentHub()
        val reqId = hubModel.getRandomTag()
        mGetParentRequest = ReqGetParents(reqId, witnesses)
        hubModel.mHubClient.sendHubMsg(mGetParentRequest)

        if (mGetParentRequest.getResponse().msgType == MSG_TYPE.empty) {
            return false
        }

        return true

    }



    fun startSendTx(activity: ActivityMain, password: String = "") {

        MyThreadManager.instance.runInBack {

            val credential = WalletManager.model.findWallet(sendPaymentInfo.walletId)
            if (password.isNotEmpty() && !credential.isObserveOnly) {
                signWithEveryAuthors(password)
            } else {

            }

            units.unit = jsApi.getUnitHashSync(Utils.toGsonString(units))

            if (postNewUnitToHub()) {
                val unitJson = Utils.toGsonObject(units)
                val unit = UnitsManager().parseUnitFromJson(unitJson)
                DbHelper.saveUnits(unit)
                WalletManager.model.refreshOneWallet(sendPaymentInfo.walletId)
            }
        }
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

        rows.forEach {
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

        changeOutput.address = queryOrIssueNotUsedChangeAddress()
        changeOutput.amount = TTT.PLACEHOLDER_AMOUNT

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
        genChange()
        genPayloadHash()

        hashToSign = jsApi.getUnitHashToSignSync(Utils.toGsonString(units))

        Utils.debugLog(Utils.toGsonString(units))
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
        units.headersCommission = jsApi.getHeadersSizeSync(Utils.toGsonString(units)).toLong()
        units.payloadCommission = jsApi.getTotalPayloadSizeSync(Utils.toGsonString(units)).toLong()
    }

    private fun genAuthors() {
        authors.clear()

        val addressList = mutableListOf<String>()
        payload.inputs.forEach { addressList.add(it.address) }

        val myAddressesArray = DbHelper.queryAddress(addressList.toList())
        myAddressesArray.forEach {
            val authentifiers = Authentifiers()
            authentifiers.address = it.address
            authentifiers.definition = Utils.parseJsonArray(it.definition)
            authentifiers.authentifiers = Utils.genJsonObject("r", TTT.PLACEHOLDER_SIG)
            authors.add(authentifiers)
        }
    }

    private fun queryOrIssueNotUsedChangeAddress(): String {
        //How about it cannot find unused change address
        return WalletManager.model.findNextUnusedChangeAddress(sendPaymentInfo.walletId).address
    }

    fun postNewUnitToHub(): Boolean {

        val hubModel = HubManager.instance.getCurrentHub()
        val reqId = hubModel.getRandomTag()
        val req = ReqPostJoint(reqId, Utils.toGsonObject(units))
        hubModel.mHubClient.sendHubMsg(req)

        val hubResponse = req.getResponse()

        if (hubResponse.msgType == MSG_TYPE.response) {
            return "accepted" == hubResponse.responseJson?.asString
        }

        return false
    }
}