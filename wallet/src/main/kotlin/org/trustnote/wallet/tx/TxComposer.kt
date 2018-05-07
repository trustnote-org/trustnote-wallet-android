package org.trustnote.wallet.tx

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import org.trustnote.db.DbHelper
import org.trustnote.wallet.TTT
import org.trustnote.wallet.js.JSApi
import org.trustnote.wallet.network.HubManager
import org.trustnote.wallet.network.HubMsgFactory
import org.trustnote.wallet.network.pojo.HubRequest
import org.trustnote.wallet.network.pojo.HubResponse
import org.trustnote.wallet.network.pojo.MSG_TYPE
import org.trustnote.wallet.pojo.Author
import org.trustnote.wallet.pojo.InputOfPayment
import org.trustnote.wallet.pojo.SendPaymentInfo
import org.trustnote.wallet.util.Utils
import org.trustnote.wallet.walletadmin.WalletModel

class TxComposer(
        val sendPaymentInfo: SendPaymentInfo
) {

    lateinit var mGetParentRequest: HubRequest
    //TODO: need refactor code.
    fun composeNewTx(hubResponse: HubResponse) {
//        val sendPaymentInfo = SendPaymentInfo("LyzbDDiDedJh+fUHMFAXpWSiIw/Z1Tgve0J1+KOfT3w=", "CDZUOZARLIXSQDSUQEZKM4Z7X6AXTVS4", 3000000L)
        val responseJson = hubResponse.responseJson as JsonObject

        sendPaymentInfo.lastBallMCI = responseJson.get("last_stable_mc_ball_mci").asInt

        val unit = JsonObject()
        unit.addProperty("version", TTT.version)
        unit.addProperty("alt", TTT.alt)
        val messages = JsonArray()

        val paymentMessage = JsonObject()
        paymentMessage.addProperty("app", "payment")
        paymentMessage.addProperty("payload_location", "inline")

        val payload = genPayload(sendPaymentInfo)
        val payloadHash = JSApi().getBase64HashSync(payload.toString())
        paymentMessage.addProperty("payload_hash", payloadHash)

        paymentMessage.add("payload", payload)

        messages.add(paymentMessage)

        unit.add("messages", messages)

        //TODO: DUP code and error prone if DB changed.
        val inputs = DbHelper.findInputsForPayment(sendPaymentInfo)
        val addressList = mutableListOf<String>()
        inputs.forEach { addressList.add(it.address) }

        val myAddressesArray = DbHelper.queryAddress(addressList.toList())
        val authors = JsonArray()
        myAddressesArray.forEach {
            authors.add(Author(it.address,
                    TTT.PLACEHOLDER_SIG, it.definition).toJsonObject())
        }

        unit.add("authors", authors)

        unit.add("parent_units", responseJson.getAsJsonArray("parent_units"))

        unit.addProperty("last_ball", responseJson.get("last_stable_mc_ball").asString)

        unit.addProperty("last_ball_unit", responseJson.get("last_stable_mc_ball_unit").asString)

        unit.addProperty("witness_list_unit", responseJson.get("witness_list_unit").asString)

        unit.addProperty("headers_commission", computeHeadersCommission())

        unit.addProperty("payload_commission", computePayloadCommission())


        val api = JSApi()
        val unitHash = api.getUnitHashSync(unit.toString())

        //TODO:
        unit.addProperty("unit", Utils.jsStr2NormalStr(unitHash))

        unit.addProperty("timestamp", System.currentTimeMillis() / 1000L)

        //TODO: sign and send reqeust.

        val unitHashForSign = api.getUnitHashToSignSync(unit.toString())

        //TODO: read below value from MyAddress, where is the wallet index come from?


        val authorsWithSign = JsonArray()
        myAddressesArray.forEach {
            val path = Utils.genJsBip44Path(it.account, it.isChange, it.addressIndex)
            val sign = api.signSync(unitHashForSign,
                    WalletModel.instance.getProfile()!!.xPrivKey, path)
            authorsWithSign.add(Author(it.address,
                    Utils.jsStr2NormalStr(sign), it.definition).toJsonObject())
        }

        unit.remove("authors")
        unit.add("authors", authorsWithSign)

        Utils.debugLog(unit.toString())


    }

    private fun genPayload(sendPaymentInfo: SendPaymentInfo): JsonObject {
        val payload = JsonObject()
        val outputs = JsonArray()
        val inputs = JsonArray()

        val inputsOfPayment = findInputsForPayment(sendPaymentInfo)
        inputsOfPayment.forEach { inputs.add(it.toJsonObject()) }

        val changeAddress = queryOrIssueNotUsedChangeAddress()
        val changeAmount = computeChangeAmount(sendPaymentInfo, inputsOfPayment)
        val changeOutput = createOutput(changeAddress, changeAmount)
        val receiverOutput = createOutput(sendPaymentInfo.receiverAddress, sendPaymentInfo.amount)

        outputs.add(receiverOutput)
        outputs.add(changeOutput)

        payload.add("outputs", outputs)
        payload.add("inputs", inputs)

        return payload
    }

    private fun computeChangeAmount(sendPaymentInfo: SendPaymentInfo, inputsOfPayment: List<InputOfPayment>): Long {
        var totalInput = 0L
        inputsOfPayment.forEach { totalInput += it.amount }

        return totalInput - sendPaymentInfo.amount
        -computeHeadersCommission() - computePayloadCommission()
    }

    private fun computeHeadersCommission(): Long {
        return 391L
    }

    private fun computePayloadCommission(): Long {
        return 197L
    }

    private fun findInputsForPayment(sendPaymentInfo: SendPaymentInfo): List<InputOfPayment> {
        return DbHelper.findInputsForPayment(sendPaymentInfo)
    }

    private fun createOutput(address: String, amount: Number): JsonObject {
        val output = JsonObject()
        output.addProperty("address", address)
        output.addProperty("amount", amount)
        return output

    }

    private fun queryOrIssueNotUsedChangeAddress(): String {
        //TODO
        return "FJDDWP4AJ6I44HSKHPXXIX6RSQMH674G"
    }

    fun startSending() {
        //        TxComposer(sendPaymentInfo).composeNewTx(hubResponse)
        val witnesses = DbHelper.getMyWitnesses()

        if (witnesses.isEmpty()) {
            return
        }

        mGetParentRequest = HubMsgFactory.getParentForNewTx(HubManager.instance.getCurrentHub(), witnesses)

        val subscribe = HubManager.instance.getCurrentHub().mSubject.subscribe() {
            if (it.msgType == MSG_TYPE.request && it == mGetParentRequest) {
                gotParentFromHub(it as HubRequest)
            }
        }
        HubManager.instance.getCurrentHub().mHubClient.sendHubMsg(mGetParentRequest)
    }

    private fun gotParentFromHub(hubRequest: HubRequest) {
        composeNewTx(hubRequest.hubResponse)
    }

}