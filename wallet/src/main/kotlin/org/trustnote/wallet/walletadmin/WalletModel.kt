package org.trustnote.wallet.walletadmin

import io.reactivex.schedulers.Schedulers
import org.trustnote.db.DbHelper
import org.trustnote.db.entity.MyAddresses
import org.trustnote.wallet.TTT
import org.trustnote.wallet.js.JSApi
import org.trustnote.wallet.network.HubManager
import org.trustnote.wallet.network.hubapi.HubMsgFactory
import org.trustnote.wallet.pojo.Credential
import org.trustnote.wallet.pojo.TProfile
import org.trustnote.wallet.util.Prefs
import org.trustnote.wallet.util.Utils
import java.math.BigInteger

class WalletModel {

    companion object {
        lateinit var instance: WalletModel
            private set
    }

    constructor() {
        instance = this
        monitorWallet()
    }

    var currentMnemonic: List<String> = listOf()
    var currentJSMnemonic = ""
    var tProfile: TProfile? = null

    var latestWitnesses: MutableList<String> = mutableListOf()

    fun getWitnesses(): List<String> {
        return latestWitnesses
    }

    fun monitorWallet() {
        DbHelper.monitorAddresses().subscribeOn(Schedulers.io()).subscribe {
            Utils.debugLog("from monitorAddresses")
            WalletModel.instance.hubRequestCurrentWalletTxHistory()
        }

        DbHelper.monitorUnits().subscribeOn(Schedulers.io()).subscribe {
            Utils.debugLog("from monitorUnits")
            tryToReqMoreUnitsFromHub()
            DbHelper.fixIsSpentFlag()
        }

        DbHelper.monitorOutputs().subscribeOn(Schedulers.io()).subscribe {
            Utils.debugLog("from monitorOutputs")
            updateBalance()
        }
    }

    @Synchronized private fun updateBalance() {
        getProfile()!!.credentials.forEachIndexed { index, oneWallet ->
            val balanceDetails = DbHelper.getBanlance(oneWallet.walletId)
            oneWallet.balanceDetails = balanceDetails
            oneWallet.balance = 0
            oneWallet.balanceDetails.forEach{
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
        val res = List(TTT.walletAddressInitSize, {
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

    @Synchronized fun newWallet(credentialName: String = TTT.firstWalletName) {
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

}

