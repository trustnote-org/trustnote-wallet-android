package org.trustnote.superwallet.biz.wallet

import android.webkit.ValueCallback
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import org.trustnote.superwallet.biz.TTT
import org.trustnote.superwallet.biz.js.JSApi
import org.trustnote.superwallet.biz.js.TWebView
import org.trustnote.superwallet.network.HubManager
import org.trustnote.superwallet.util.Prefs

object WalletManager {

    //TODO: when create new model, should close all hub listener.

    //TODO: debounce the events.
    val mWalletEventCenter: Subject<Boolean> = PublishSubject.create()
    val mUpgradeEventCenter: Subject<Boolean> = PublishSubject.create()

    lateinit var model: WalletModel
    private var currentDbTag = ""

    init {
        if (Prefs.profileExist()) {
            model = WalletModel()
            model.refreshExistWallet()
        }
    }

    @Synchronized
    fun getCurrentWalletDbTag(): String {
        return currentDbTag
    }

    @Synchronized
    fun setCurrentWalletDbTag(tag: String) {
        currentDbTag = tag
    }

    fun getProfile(): TProfile {
        return model.mProfile
    }

    fun initWithMnemonic(password: String, mnemonic: String, removeMnemonic: Boolean) {
        if (Prefs.profileExist()) {
            model.destruct()
        }
        model = WalletModel(password, mnemonic, removeMnemonic)
    }

    fun getOrCreateMnemonic(): String {
        return JSApi().mnemonicSync()
    }

    fun isExist(): Boolean {
        return Prefs.profileExist()
    }

}