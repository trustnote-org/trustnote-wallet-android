package org.trustnote.wallet.walletadmin

import io.reactivex.schedulers.Schedulers
import org.trustnote.db.DbHelper
import org.trustnote.db.entity.MyAddresses
import org.trustnote.wallet.TTT
import org.trustnote.wallet.js.JSApi
import org.trustnote.wallet.network.HubManager
import org.trustnote.wallet.network.HubMsgFactory
import org.trustnote.wallet.pojo.Credential
import org.trustnote.wallet.pojo.TProfile
import org.trustnote.wallet.tx.TxParser
import org.trustnote.wallet.util.Prefs
import org.trustnote.wallet.util.Utils
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
        return tProfile!!.mnemonic.substring(1..4)
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
            val txs = TxParser().getTxs(it.walletId)
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
        currentMnemonic = Utils.jsStr2NormalStr(s).split(" ")

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

            //TODO: load tx, address from db etc.
            if (tProfile != null) {
                for (oneCredential in tProfile!!.credentials) {
                    val addresses = DbHelper.queryAddressByWalletId(oneCredential.walletId)
                    addresses.forEach { it.jsBip44Path = Utils.genJsBip44Path(oneCredential.account, it.isChange, it.addressIndex) }
                    oneCredential.myAddresses.clear()
                    oneCredential.myAddresses.addAll(addresses)
                }
            }


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
        val walletId = Utils.jsStr2NormalStr(api.walletIDSync(walletPubKey))
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
            myAddress.account = credential.account
            myAddress.wallet = credential.walletId

            myAddress.isChange = isChange
            myAddress.addressIndex = it + currentMaxAddress
            myAddress.address = Utils.jsStr2NormalStr(api.walletAddressSync(credential.xPubKey, isChange, myAddress.addressIndex))
            val addressPubkey = Utils.jsStr2NormalStr(api.walletAddressPubkeySync(credential.xPubKey, isChange, myAddress.addressIndex))
            myAddress.definition = """["sig",{"pubkey":"$addressPubkey"}]"""
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

}


