package org.trustnote.wallet.biz.wallet

import com.google.gson.JsonObject
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import org.trustnote.db.DbHelper
import org.trustnote.db.entity.MyAddresses
import org.trustnote.wallet.TTT
import org.trustnote.wallet.js.JSApi
import org.trustnote.wallet.network.HubManager
import org.trustnote.wallet.network.HubMsgFactory
import org.trustnote.wallet.biz.tx.TxParser
import org.trustnote.wallet.network.pojo.HubMsg
import org.trustnote.wallet.util.Prefs
import org.trustnote.wallet.util.Utils
import java.util.concurrent.TimeUnit

class WalletModel() {

    lateinit var mProfile: TProfile
    val mSubject: Subject<Boolean> = PublishSubject.create()

    init {
        if (Prefs.profileExist()) {
            mProfile = Prefs.readProfile()
            profileUpdated()
            loadDataFromDb()
        }
    }

    constructor(mnemonic: String, shouldRemoveMnemonic: Boolean) : this() {
        mProfile = TProfile()
        if (!shouldRemoveMnemonic) {
            mProfile.mnemonic = mnemonic
        }
        //TODO: thread manager
        Thread {
            initFromMnemonic()
        }.start()
    }

    fun profileExist(): Boolean {
        return Prefs.profileExist()
    }

    private fun initFromMnemonic() {

        //TODO: At this moment, should clear all data in DB.
        val api = JSApi()
        val xPrivKey = api.xPrivKeySync(mProfile.mnemonic)
        mProfile.xPrivKey = xPrivKey
        mProfile.keyDb = xPrivKey.hashCode().toString()

        profileUpdated()
        //TODO: think more.
        HubManager.instance.reConnectHub()

        monitorWallet()
        createDefaultCredential()

    }

    private fun profileUpdated() {
        Prefs.writeProfile(mProfile)
        mSubject.onNext(true)
    }

    fun removeMnemonicFromProfile() {
        mProfile.mnemonic = ""
        profileUpdated()
    }

    private fun createDefaultCredential() {
        newWallet(TTT.firstWalletName)
    }

    fun loadDataFromDb() {
        //TODO: thread manager
        Thread {
            loadDataFromDbBg()
            monitorWallet()
        }.start()
    }

    private fun loadDataFromDbBg() {
        mProfile.credentials.forEach {
            loadMyAddress(it)
            updateBalance(it)
            updateTxs(it)
        }
    }

    private fun updateBalance(credential: Credential) {
        val balanceDetails = DbHelper.getBanlance(credential.walletId)
        credential.balanceDetails = balanceDetails
        credential.balance = 0
        credential.balanceDetails.forEach {
            credential.balance += it.amount
        }

        updateTotalBalance()
    }

    private fun updateTotalBalance() {
        mProfile.balance = 0
        mProfile.credentials.forEach {
            mProfile.balance += it.balance
        }
    }

    private fun updateTxs(credential: Credential) {
        credential.txDetails.clear()
        val txs = TxParser().getTxs(credential.walletId)

        val sortedRes = txs.sortedByDescending {
            it.ts
        }

        credential.txDetails.addAll(sortedRes)
    }

    private fun loadMyAddress(credential: Credential) {
        val addresses = DbHelper.queryAddressByWalletId(credential.walletId)

        credential.myAddresses.clear()
        credential.myAddresses.addAll(addresses)

        credential.myReceiveAddresses.clear()
        credential.myReceiveAddresses.addAll(addresses.filter { it.isChange == 0 })

        credential.myChangeAddresses.clear()
        credential.myChangeAddresses.addAll(addresses.filter { it.isChange == 1 })
    }

//

    fun monitorWallet() {
        DbHelper.monitorAddresses().debounce(3, TimeUnit.SECONDS).subscribeOn(Schedulers.io()).subscribe {
            Utils.debugLog("from monitorAddresses")
            hubRequestCurrentWalletTxHistory()
        }

        DbHelper.monitorUnits().debounce(3, TimeUnit.SECONDS).subscribeOn(Schedulers.io()).subscribe {
            Utils.debugLog("from monitorUnits")
            tryToReqMoreUnitsFromHub()
            DbHelper.fixIsSpentFlag()
        }

        DbHelper.monitorOutputs().delay(3L, TimeUnit.SECONDS).subscribeOn(Schedulers.io()).subscribe {
            Utils.debugLog("from monitorOutputs")
            if (mProfile != null) {
                loadDataFromDbBg()
            }
        }
    }

    private fun tryToReqMoreUnitsFromHub() {
        mProfile.credentials.forEachIndexed { index, oneWallet ->
            val isMore = DbHelper.shouldGenerateMoreAddress(oneWallet.walletId)
            if (isMore) {
                generateMoreAddressAndSave(oneWallet)
            }
        }

        var lastWallet = mProfile.credentials.last()

        if (lastWallet != null) {
            val isMore = DbHelper.shouldGenerateNextWallet(lastWallet.walletId)
            if (isMore) {
                newWallet()
            }
        }

    }

    private fun createNextCredential(profile: TProfile, credentialName: String = TTT.firstWalletName): Credential {
        val api = JSApi()
        val walletIndex = findNextAccount(profile)
        val walletPubKey = api.walletPubKeySync(profile.xPrivKey, walletIndex)
        val walletId = Utils.decodeJsStr(api.walletIDSync(walletPubKey))
        val walletTitle = if (TTT.firstWalletName == credentialName) TTT.firstWalletName + ":" + walletIndex else credentialName

        val res = Credential()
        res.account = walletIndex
        res.walletId = walletId
        res.walletName = walletTitle
        res.xPubKey = walletPubKey

        return res
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
            myAddress.account = credential.account
            myAddress.wallet = credential.walletId

            myAddress.isChange = isChange
            myAddress.addressIndex = it + currentMaxAddress
            myAddress.address = Utils.decodeJsStr(api.walletAddressSync(credential.xPubKey, isChange, myAddress.addressIndex))
            val addressPubkey = Utils.decodeJsStr(api.walletAddressPubkeySync(credential.xPubKey, isChange, myAddress.addressIndex))
            myAddress.definition = """["sig",{"pubkey":"$addressPubkey"}]"""
            //TODO: check above logic from JS code.
            myAddress
        })

        credential.myAddresses.addAll(res)

    }

    @Synchronized
    fun newWallet(credentialName: String = TTT.firstWalletName) {
        val newCredential = createNextCredential(mProfile, credentialName)
        mProfile.credentials.add(newCredential)
        generateMoreAddressAndSave(newCredential)
    }

    private fun generateMoreAddressAndSave(newCredential: Credential) {
        generateMyAddresses(newCredential, TTT.addressReceiveType)
        generateMyAddresses(newCredential, TTT.addressChangeType)

        DbHelper.saveWalletMyAddress(newCredential)

        profileUpdated()
    }

    fun hubRequestCurrentWalletTxHistory() {

        val witnesses = DbHelper.getMyWitnesses()
        val addresses = DbHelper.getAllWalletAddress()

        if (witnesses.isEmpty() || addresses.isEmpty()) {
            return
        }

        val req = HubMsgFactory.getHistory(HubManager.instance.getCurrentHub(), witnesses, addresses)
        HubManager.instance.getCurrentHub().mHubClient.sendHubMsg(req)

    }

    fun findNextUnusedChangeAddress(walletId: String): MyAddresses {
        var res = MyAddresses()
        DbHelper.queryUnusedChangeAddress(walletId).subscribe(
                Consumer {
                    res = it
                }, Consumer {
            //Issue and save new address.

        })
        return res
    }

    //    TTT: {
    //        "type": "h1",
    //        "id": "LYnW1wl8qHyHyWjoV2CYOlYhUvE3Gj1jh5tUEFzoMn0=",
    //        "v": 1234
    //    }
    fun genColdScancodeFromWalletId(observeScanRes: String): String {
        if (observeScanRes.isBlank() || observeScanRes.length < 4) {
            return ""
        }

        try {
            val jsonStr = observeScanRes.substring(4)

            val jsonObj = Utils.getGson().fromJson(jsonStr, JsonObject::class.java)

            val walletPubKey = jsonObj.getAsJsonPrimitive("pub").asString

            val checkCode = jsonObj.getAsJsonPrimitive("v").asString

            val walletId = JSApi().walletIDSync(walletPubKey)

            return """TTT:{"type":"h1","id": "$walletId","v":$checkCode}"""

        } catch (ex: Exception) {
            Utils.logW(ex.localizedMessage)
        }
        return ""
    }

    //    TTT: {
    //        "type": "c1",
    //        "name": "TTT",
    //        "pub": "xpub6CiT96vM5krNhwFA4ro5nKJ6nq9WykFmAsP18jC1Aa3URb69rvUHw6uvU51MQPkMZQ6BLiC5C1E3Zbsm7Xob3FFhNHJkN3v9xuxfqFFKPP5",
    //        "n": 0,
    //        "v": 1234
    //    }
    fun parseObserverScanResult(scanStr: String): String {
        if (scanStr.isBlank() || scanStr.length < 4) {
            return ""
        }

        val jsonStr = scanStr.substring(4)

        try {
            val jsonObj = Utils.getGson().fromJson(jsonStr, JsonObject::class.java)
            return jsonObj.getAsJsonPrimitive("pub").asString
        } catch (ex: Exception) {
            Utils.logW(ex.localizedMessage)
        }
        return ""

    }

    //    TTT: {
    //        "type": "c2",
    //        "addr": "0NEYV3ZCRAJYGJDS5UNN4EOZGNVZJXOLI",
    //        "v": 1234
    //    }
    fun parseObserverAdd(scanStr: String): String {
        if (scanStr.isBlank() || scanStr.length < 4) {
            return ""
        }

        val jsonStr = scanStr.substring(4)

        try {
            val jsonObj = Utils.getGson().fromJson(jsonStr, JsonObject::class.java)
            return jsonObj.getAsJsonPrimitive("addr").asString
        } catch (ex: Exception) {
            Utils.logW(ex.localizedMessage)
        }
        return ""
    }


    fun isValidObserverAdd(scanStr: String): Boolean {
        if (scanStr.isBlank() || scanStr.length < 4) {
            return false
        }

        val jsonStr = scanStr.substring(4)

        try {
            val jsonObj = Utils.getGson().fromJson(jsonStr, JsonObject::class.java)
            return jsonObj.getAsJsonPrimitive("addr").asString.isNotEmpty()
        } catch (ex: Exception) {
            Utils.logW(ex.localizedMessage)
        }
        return false
    }

}


