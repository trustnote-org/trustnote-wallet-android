package org.trustnote.wallet.biz.wallet

import org.trustnote.wallet.js.JSApi
import org.trustnote.wallet.pojo.TProfile
import org.trustnote.wallet.util.Prefs


object WalletManager {

    //TODO: when create new model, should close all hub listener.

    lateinit var model: WalletModel
    init {
        if (Prefs.profileExist()) {
            model = WalletModel()
        }
    }

    fun getDbKey(): String {
        return model.mProfile.keyDb
    }

    fun getProfile(): TProfile {
        return model.mProfile
    }

    fun initWithMnemonic(removeMnemonic: Boolean) {
        model = WalletModel(Prefs.getTmpMnemonic(), removeMnemonic)
    }

    fun initWithMnemonic(mnemonic: String, removeMnemonic: Boolean) {
        model = WalletModel(mnemonic, removeMnemonic)
    }

    fun getTmpMnemonic(): String {
        return Prefs.getTmpMnemonic()
    }

    fun getOrCreateMnemonic(): String {
        return JSApi().mnemonicSync()
    }

    fun isExist(): Boolean {
        return Prefs.profileExist()
    }

}