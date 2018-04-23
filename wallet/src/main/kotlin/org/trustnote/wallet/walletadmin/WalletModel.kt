package org.trustnote.wallet.walletadmin

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

class WalletModel {

    companion object {
        lateinit @JvmStatic
        var instance: WalletModel
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
        val walletId = api.walletIDSync(walletPubKey)
        return Credential(account = walletIndex, walletId = walletId, xPubKey = walletPubKey, walletName = credentialName)
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

    private fun generateMyAddresses(credential: Credential) {
        val api = JSApi()
        val res = List(TTT.walletAddressInitSize, {
            val myAddress = MyAddresses()
            myAddress.address = toNormalStr(api.walletAddressSync(credential.xPubKey, TTT.addressReceiveType, it))
            myAddress.wallet = credential.walletId
            myAddress.isChange = TTT.addressReceiveType
            myAddress.addressIndex = it
            val addressPubkey = api.walletAddressPubkeySync(credential.xPubKey, TTT.addressReceiveType, it)
            myAddress.definition = """["sig",{"pubkey":$addressPubkey}]"""
            //TODO: check above logic from JS code.
            myAddress
        })

        credential.myAddresses.addAll(res)

    }

    fun createProfile(removeMnemonic: Boolean) {
        createProfileFromMnenonic(currentJSMnemonic, removeMnemonic)
    }

    fun newWallet(credentialName: String = TTT.firstWalletName) {
        val newCredential = createNextCredential(tProfile!!, credentialName)
        //TODO: how about DB/Prefs failed.
        generateMyAddresses(newCredential)
        DbHelper.saveWalletMyAddress(newCredential)
        tProfile!!.credentials.add(newCredential)

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
        val addresses = DbHelper.getAllWalletAddress(tProfile!!.credentials[0].walletId)

        if (witnesses.isEmpty() || addresses.isEmpty()) {
            return
        }

        val req = HubMsgFactory.getHistory(HubManager.instance.getCurrentHub(), witnesses, addresses)
        HubManager.instance.getCurrentHub().mHubClient.sendHubMsg(req)

    }

}

