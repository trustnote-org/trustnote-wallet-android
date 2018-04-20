package org.trustnote.wallet.walletadmin

import android.webkit.ValueCallback
import org.trustnote.wallet.TTT
import org.trustnote.wallet.js.JSApi
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

    fun setJSMnemonic(s: String) {
        currentMnemonic = s.filterNot { it == '"' }.split(" ")
        currentJSMnemonic = s
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

    private fun createNextCredential(profile: TProfile, credentialName: String = TTT.firstWalletName): Credential {
        val api = JSApi()
        val walletPubKey = api.walletPubKeySync(profile.xPrivKey, 0)
        val walletId = api.walletIDSync(walletPubKey)
        val credential = Credential(account = findNextAccount(profile), walletId = walletId, xPubKey = walletPubKey, walletName = credentialName)
        return credential
    }

    private fun findNextAccount(profile: TProfile): Int {
        var max = -1
        for (one in profile.credentials) {
            if (one.account > max) {
                max = one.account
            }
        }
        return max + 1
    }

    fun createProfile(removeMnemonic: Boolean) {
        createProfileFromMnenonic(currentJSMnemonic, removeMnemonic)
    }

    fun newWallet(credentialName: String = TTT.firstWalletName) {
        val newCredential = createNextCredential(tProfile!!, credentialName)
        tProfile!!.credentials.add(newCredential)
        Prefs.getInstance().saveObject(tProfile)
    }

    fun createProfileFromMnenonic(mnemonic: String, removeMnemonic: Boolean) {
        //TODO: handle the removeMnenonic logic.
        val api = JSApi()
        val xPrivKey = api.xPrivKeySync(mnemonic)
        val ecdsaPubkey = api.ecdsaPubkeySync(xPrivKey, "\"m/1\"")
        val deviceAddress = api.deviceAddressSync(xPrivKey)
        val profile = TProfile(ecdsaPubkey = ecdsaPubkey, mnemonic = mnemonic, xPrivKey = xPrivKey, deviceAddress = deviceAddress)
        tProfile = profile
        newWallet()
    }

}

