package org.trustnote.wallet.walletadmin

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import io.reactivex.schedulers.Schedulers
import org.trustnote.db.DbHelper
import org.trustnote.db.entity.MyAddresses
import org.trustnote.wallet.TTT
import org.trustnote.wallet.js.JSApi
import org.trustnote.wallet.network.HubManager
import org.trustnote.wallet.network.hubapi.HubMsgFactory
import org.trustnote.wallet.network.hubapi.HubResponse
import org.trustnote.wallet.pojo.*
import org.trustnote.wallet.util.Prefs
import org.trustnote.wallet.util.Utils
import org.trustnote.wallet.util.testTopLevel
import java.util.concurrent.TimeUnit

class WalletModel {

    companion object {
        lateinit var instance: WalletModel
            private set
    }

    constructor() {
        instance = this
    }

    var currentMnemonic: List<String> = listOf()
    var currentJSMnemonic = ""
    var tProfile: TProfile? = null

    var latestWitnesses: MutableList<String> = mutableListOf()

    fun getWitnesses(): List<String> {
        return latestWitnesses
    }

    //TODO: use hash function
    fun getMnemonicAsHash(): String {
        return currentMnemonic[0]
    }

    fun monitorWallet() {
        DbHelper.monitorAddresses().debounce(3, TimeUnit.SECONDS).subscribeOn(Schedulers.io()).subscribe {
            Utils.debugLog("from monitorAddresses")
            WalletModel.instance.hubRequestCurrentWalletTxHistory()
        }

        DbHelper.monitorUnits().debounce(3, TimeUnit.SECONDS).subscribeOn(Schedulers.io()).subscribe {
            Utils.debugLog("from monitorUnits")
            tryToReqMoreUnitsFromHub()
            DbHelper.fixIsSpentFlag()
        }

        DbHelper.monitorOutputs().delay(3L, TimeUnit.SECONDS).subscribeOn(Schedulers.io()).subscribe {
            Utils.debugLog("from monitorOutputs")
            if (tProfile != null) {
                //Too
                updateBalance()
                updateTxs()
                saveProfile()
            }
        }
    }

    @Synchronized
    fun updateTxs() {
        tProfile!!.credentials.forEach {
            val txs = DbHelper.getTxs(it.walletId)
            it.txDetails = txs
        }
    }

    @Synchronized
    private fun updateBalance() {
        getProfile()!!.credentials.forEachIndexed { index, oneWallet ->
            val balanceDetails = DbHelper.getBanlance(oneWallet.walletId)
            oneWallet.balanceDetails = balanceDetails
            oneWallet.balance = 0
            oneWallet.balanceDetails.forEach {
                oneWallet.balance += it.amount
            }
        }
    }

    private fun tryToReqMoreUnitsFromHub() {
        if (getProfile() == null) {
            return
        }

        getProfile()!!.credentials.forEachIndexed { index, oneWallet ->
            val isMore = DbHelper.shouldGenerateMoreAddress(oneWallet.walletId)
            if (isMore) {
                generateMoreAddressAndSave(oneWallet)
            }
        }

        var lastWallet = getProfile()!!.credentials.last()

        if (lastWallet != null) {
            val isMore = DbHelper.shouldGenerateNextWallet(lastWallet.walletId)
            if (isMore) {
                newWallet()
            }
        }

    }

    fun setWitnesses(l: List<String>) {
        if (l.isEmpty()) {
            Utils.debugLog("setWitness with l, but it is empty")
            return
        }
        latestWitnesses.clear()

        latestWitnesses.addAll(l)
    }

    fun setJSMnemonic(s: String) {
        currentMnemonic = toNormalStr(s).split(" ")

        currentJSMnemonic = "\"" + currentMnemonic.joinToString(" ") + "\""
    }

    fun getTxtMnemonic(): String {
        return currentJSMnemonic.filterNot { it == '"' }
    }

    fun getOrCreateMnemonic(): List<String> {
        if (currentJSMnemonic.isEmpty()) {
            setJSMnemonic(JSApi().mnemonicSync())
        }
        return currentMnemonic
    }

    var deviceName: String = android.os.Build.MODEL

    fun getProfile(): TProfile? {
        if (tProfile == null) {
            tProfile = Prefs.getInstance().readObject(TProfile::class.java)
        }
        return tProfile
    }

    fun getAllCredentials(): Iterable<Credential> {
        return getProfile()!!.credentials.asIterable()
    }

    private fun createNextCredential(profile: TProfile, credentialName: String = TTT.firstWalletName): Credential {
        val api = JSApi()
        val walletIndex = findNextAccount(profile)
        val walletPubKey = api.walletPubKeySync(profile.xPrivKey, walletIndex)
        val walletId = toNormalStr(api.walletIDSync(walletPubKey))
        val walletTitle = if (TTT.firstWalletName == credentialName) TTT.firstWalletName + ":" + walletIndex else credentialName
        return Credential(account = walletIndex, walletId = walletId, xPubKey = walletPubKey, walletName = walletTitle)
    }

    fun setCurrentWallet(accountIdx: Int) {
        getProfile()!!.currentWalletIndex = accountIdx
        saveProfile()
    }

    //TODO: get the next accout from DB. regarding use can remove wallet.
    private fun findNextAccount(profile: TProfile): Int {
        var max = -1
        for (one in profile.credentials) {
            if (one.account > max) {
                max = one.account
            }
        }
        return max + 1
    }

    private fun generateMyAddresses(credential: Credential, isChange: Int) {
        val api = JSApi()
        val currentMaxAddress = DbHelper.getMaxAddressIndex(credential.walletId, isChange)
        val newAddressSize = if (currentMaxAddress == 0) TTT.walletAddressInitSize else TTT.walletAddressIncSteps
        val res = List(newAddressSize, {
            val myAddress = MyAddresses()
            myAddress.wallet = credential.walletId
            myAddress.isChange = isChange
            myAddress.addressIndex = it + currentMaxAddress
            myAddress.address = toNormalStr(api.walletAddressSync(credential.xPubKey, isChange, myAddress.addressIndex))
            val addressPubkey = toNormalStr(api.walletAddressPubkeySync(credential.xPubKey, isChange, myAddress.addressIndex))
            myAddress.definition = """["sig",{"pubkey":$addressPubkey}]"""
            //TODO: check above logic from JS code.
            myAddress
        })

        credential.myAddresses.addAll(res)

    }

    fun createProfile(removeMnemonic: Boolean) {
        createProfileFromMnenonic(currentJSMnemonic, removeMnemonic)
    }

    @Synchronized
    fun newWallet(credentialName: String = TTT.firstWalletName) {
        val newCredential = createNextCredential(tProfile!!, credentialName)
        //TODO: how about DB/Prefs failed.
        tProfile!!.credentials.add(newCredential)
        generateMoreAddressAndSave(newCredential)
    }

    private fun generateMoreAddressAndSave(newCredential: Credential) {
        generateMyAddresses(newCredential, TTT.addressReceiveType)
        generateMyAddresses(newCredential, TTT.addressChangeType)

        DbHelper.saveWalletMyAddress(newCredential)
        saveProfile()
    }

    fun createProfileFromMnenonic(mnemonic: String, removeMnemonic: Boolean = false) {
        //TODO: handle the removeMnenonic logic.
        setJSMnemonic(mnemonic)

        val api = JSApi()
        val xPrivKey = api.xPrivKeySync(currentJSMnemonic)

        val ecdsaPubkey = api.ecdsaPubkeySync(xPrivKey, "\"m/1\"")
        val deviceAddress = api.deviceAddressSync(xPrivKey)
        val profile = TProfile(ecdsaPubkey = ecdsaPubkey, mnemonic = mnemonic, xPrivKey = xPrivKey, deviceAddress = deviceAddress)
        tProfile = profile
        newWallet()

        monitorWallet()

    }

    private fun saveProfile() {
        Prefs.getInstance().saveObject(tProfile)
    }

    private fun toNormalStr(jsString: String): String {
        return jsString.filterNot { it == '"' }
    }

    fun hubRequestCurrentWalletTxHistory() {
        if (getProfile() == null || tProfile!!.credentials.isEmpty()) {
            return
        }

        val witnesses = DbHelper.getMyWitnesses()
        val addresses = DbHelper.getAllWalletAddress()

        if (witnesses.isEmpty() || addresses.isEmpty()) {
            return
        }

        val req = HubMsgFactory.getHistory(HubManager.instance.getCurrentHub(), witnesses, addresses)
        HubManager.instance.getCurrentHub().mHubClient.sendHubMsg(req)

    }

    //TODO: how about the result.
    fun startSendPayment(walletId: String = "", amount: Long = TTT.MAX_FEE * 2, toAddress: String = "CDZUOZARLIXSQDSUQEZKM4Z7X6AXTVS4") {
        val witnesses = DbHelper.getMyWitnesses()

        if (witnesses.isEmpty()) {
            return
        }

        val req = HubMsgFactory.getParentForNewTx(HubManager.instance.getCurrentHub(), witnesses)
        HubManager.instance.getCurrentHub().mHubClient.sendHubMsg(req)
    }


//    [
//    "response",
//    {
//        "tag": "bFJ6xkLnwXvliUo3oq8B8hz49fLVbUktpaVzwSBR5iU=",
//        "response": {
//        "parent_units": [
//        "UyVkdidA6xVp0DzRUb9k9cEUO+c2a8TCVDFahMvBJT4="
//        ],
//        "last_stable_mc_ball": "Lf/YhFAVxQC0tRqu6HGLRSKs96OHIIdzvfq8XPM7Xio=",
//        "last_stable_mc_ball_unit": "i8ua65frsm/rjDVRZphUfNbb1QyzZtaIEyONw1M52xQ=",
//        "last_stable_mc_ball_mci": 12299,
//        "witness_list_unit": "MtzrZeOHHjqVZheuLylf0DX7zhp10nBsQX5e/+cA3PQ="
//    }
//    }
//    ]

    //38338236 = 17000000 + 21337648 + 391 + 197

    //TODO: need refactor code.
    fun composeNewTx(hubResponse: HubResponse, sendPaymentInfo: SendPaymentInfo) {
        val responseJson = hubResponse.responseJson as JsonObject
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


        //TODO:
//        unit.addProperty("unit", responseJson.get("witness_list_unit").asString)

        unit.addProperty("timestamp", System.currentTimeMillis() / 1000L)


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

}


