package org.trustnote.wallet.walletadmin

import android.webkit.ValueCallback
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

    //var profile: TProfile = TProfile()
    var currentMnemonic: List<String> = listOf()
    var ecdsaPubkey: String = ""

    fun setMnemonic(s: String) {
        currentMnemonic = s.filterNot { it == '"' }.split(" ");
    }

    fun getFullMnemonic(): String {
        return currentMnemonic.joinToString(" ")
    }

//    set(value) {
//        currentMnemonic = value.filterNot { it == '"' }
//    }

    var deviceName: String = android.os.Build.MODEL

    fun createWithMnemonic() {

    }

    fun restoreWithMnemonic() {

    }

    fun getProfile(): TProfile? {
        return Prefs.getInstance().readObject(TProfile::class.java)
    }

    fun testCreateWallet() {
        setMnemonic("theme wall plunge fluid circle organ gloom expire coach patient neck clip")
        createWallet(false, Runnable {
            //TODO: verify it works.
            var profile = Prefs.getInstance().readObject(TProfile::class.java)
            if (!"\"LyzbDDiDedJh+fUHMFAXpWSiIw/Z1Tgve0J1+KOfT3w=\"".equals(profile.credentials[0].walletId)) {
                Utils.debugToast("Something error. walletId is: " + profile.credentials[0].walletId)
            } else {
                Utils.debugToast("Verify works good. walletId is " + profile.credentials[0].walletId)
            }
        })
    }

    fun addWallet(walletName: String, runnable: Runnable) {
        val profile = getProfile()

        var walletPubKey: String
        var walletId: String
        var account: Int = findNextAccount(profile!!)

        //TODO: reuse code from createWallet.
        JSApi().walletPubKey(profile.xPrivKey, account, ValueCallback {
            walletPubKey = it
            JSApi().walletID(walletPubKey, ValueCallback {
                walletId = it
                //Utils.debugLog(it)
                profile.credentials.add(Credential(walletId = walletId, xPubKey = walletPubKey, walletName = walletName, account = account))
                Prefs.getInstance().saveObject(profile)
                runnable.run()
            })
        })

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

    fun createWallet(removeMnemonic: Boolean, runnable: Runnable) {
        var mnemonic = getFullMnemonic()
        var xPrivKey: String
        var my_device_address: String

        var walletPubKey: String
        var walletId: String

        val credentials: ArrayList<Credential> = ArrayList(1)


        JSApi().xPrivKey(mnemonic, ValueCallback {
            xPrivKey = it


            //TODO:
            JSApi().ecdsaPubkey(it, "\"m/1\"", ValueCallback {
                WalletModel.instance.ecdsaPubkey = it
                //Utils.debugLog(it)
                JSApi().deviceAddress(xPrivKey, ValueCallback {
                    my_device_address = it
                    //Utils.debugLog(it)
                    JSApi().walletPubKey(xPrivKey, 0, ValueCallback {
                        walletPubKey = it
                        //Utils.debugLog(it)
                        JSApi().walletID(walletPubKey, ValueCallback {
                            walletId = it
                            //Utils.debugLog(it)
                            credentials.add(Credential(walletId = walletId, xPubKey = walletPubKey, walletName = "TTT钱包"))
                            val profile = TProfile(ecdsaPubkey = ecdsaPubkey, mnemonic = mnemonic, xPrivKey = xPrivKey, my_device_address = my_device_address, credentials = credentials)
                            Prefs.getInstance().saveObject(profile)
                            runnable.run()
                        })
                    })
                })

            })
        })
    }

    fun newMnemonic(runnable: Runnable) {
        JSApi().mnemonic(ValueCallback {
            WalletModel.instance.setMnemonic(it)
            runnable.run()
        })
    }

}

