package org.trustnote.wallet.biz.init

import org.trustnote.wallet.R
import org.trustnote.wallet.biz.wallet.CREATE_WALLET_STATUS
import org.trustnote.wallet.biz.wallet.WalletManager
import org.trustnote.wallet.util.Prefs

object CreateWalletModel {

    //TODO: should keep this in ram at most N minutes.
    var passphraseInRam: String = ""
    var tmpMnemonic: String = ""

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
        return Prefs.readPwdHash()
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
    fun getStartPageLayoutId(): Int {
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

    fun readDeviceName(): String {
        val deviceName = Prefs.readDeviceName()
        return if (deviceName.isBlank()) android.os.Build.MODEL else deviceName
    }

    fun finishedCreateOrRestore() {
        Prefs.writeFinisheCreateOrRestore()
    }

    fun isFinisheCreateOrRestore():Boolean {
        return Prefs.readIsFinisheCreateOrRestore()
    }

    fun iamDone(mnemonic: String, isRemove: Boolean) {
        finishedCreateOrRestore()
        WalletManager.initWithMnemonic(mnemonic, isRemove)
        tmpMnemonic = ""
    }

    fun iamDone() {
        finishedCreateOrRestore()
        WalletManager.initWithMnemonic(tmpMnemonic, false)
        tmpMnemonic = ""
    }

    fun verifyPwd(pwd: String): Boolean {
        return Prefs.readPwdHash() == hash(pwd)
    }
}

