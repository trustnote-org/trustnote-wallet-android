package org.trustnote.wallet.biz.wallet

import org.trustnote.wallet.R
import org.trustnote.wallet.util.Prefs

object CreateWalletModel {

    //TODO: should keep this in ram at most N minutes.
    var passphraseInRam: String = ""

    fun getCreationProgress(): CREATE_WALLET_STATUS {
        if (Prefs.profileExist()) {
            return CREATE_WALLET_STATUS.FINISHED
        }

        if (Prefs.pwdExist()) {
            return CREATE_WALLET_STATUS.PASSWORD_READY
        }

        return CREATE_WALLET_STATUS.SELECT_CREATE_OR_RESTORE
    }

    fun savePassphrase(passphrase: String) {
        passphraseInRam = passphrase
        Prefs.writePwdHash(hash(passphrase))
    }

    fun readPwd(): String {
        if (passphraseInRam.isNotEmpty()) {
            return passphraseInRam
        }
        return ""
    }

    fun readPwdHash(): String {
        return Prefs.readPwdHash()
    }

    fun hash(passphrase: String): String {
        //TODO
        return passphrase
    }

    fun userAgree() {
        Prefs.saveUserAgree()
    }

    fun isUserAgree(): Boolean {
        return Prefs.isUserAgree()
    }

    //TODO: R is not model logic.
    fun getStartPageLayoutId():Int {
        if (!Prefs.isUserAgree()) {
            return R.layout.f_new_seed_disclaimer
        }

        if (Prefs.readDeviceName().isEmpty()) {
            return R.layout.f_new_seed_devicename
        }

        return R.layout.f_new_seed_or_restore

    }

    fun saveDeviceName(deviceName: String) {
        Prefs.writeDeviceName(deviceName)
    }


}

