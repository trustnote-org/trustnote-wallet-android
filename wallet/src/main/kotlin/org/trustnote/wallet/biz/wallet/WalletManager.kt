package org.trustnote.wallet.biz.wallet

import android.webkit.ValueCallback
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import org.trustnote.wallet.TTT
import org.trustnote.wallet.js.JSApi
import org.trustnote.wallet.js.TWebView
import org.trustnote.wallet.network.HubManager
import org.trustnote.wallet.util.Prefs


object WalletManager {

    //TODO: when create new model, should close all hub listener.

    //TODO: debounce the events.
    val mWalletEventCenter: Subject<Boolean> = PublishSubject.create()

    lateinit var model: WalletModel
    private var currentDbTag = ""

    init {
        if (Prefs.profileExist()) {
            model = WalletModel()
            model.fullRefreshing()
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

    fun initWithMnemonic(mnemonic: String, removeMnemonic: Boolean, privKey: String = "") {
        if (Prefs.profileExist()) {
            model.destruct()
        }
        model = WalletModel(mnemonic, removeMnemonic, privKey)
    }

    fun getOrCreateMnemonic(): String {
        return JSApi().mnemonicSync()
    }

    fun isExist(): Boolean {
        return Prefs.profileExist()
    }

    //TODO: move to msg module.
    // Data sample: TTT:A1woEiM/LdDHLvTYUvlTZpsTI+82AphGZAvHalie5Nbw@shawtest.trustnote.org#xSpGdRdQTv16
    fun generateMyPairIdForFutureUse() {

        val res = Prefs.readMyPairId()
        if (res.isNotEmpty()) {
            return
        }

        val api = JSApi()

        api.randomBytes(9, ValueCallback {
            val randomString = it

            fun ecdsaPubkey(xPrivKey: String, path: String, cb: ValueCallback<String>) {
                TWebView.sInstance.callJS("""window.Client.ecdsaPubkey("$xPrivKey", "$path");""", cb)
            }

            api.ecdsaPubkey(model.mProfile.xPrivKey, "m/1'", ValueCallback {
                val m1Pubkey = it
                //TODO: since the value is pre-generated, should replace hub address
                val res = """TTT:$m1Pubkey@${TTT.hubAddress}#$randomString"""

                Prefs.writeMyPairId(res)
            })

        })
    }

    fun readAndConsumeMyPairId(): String {
        val res = Prefs.readMyPairId()

        if (res != null) {
            Prefs.writeMyPairId("")
        }

        generateMyPairIdForFutureUse()
        return res
    }

}