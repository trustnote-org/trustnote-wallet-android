package org.trustnote.wallet.biz.wallet

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
        Prefs.savePassphrase(hash(passphrase))
    }

    fun readPassphrase(): String {
        if (passphraseInRam.isNotEmpty()) {
            return passphraseInRam
        }
        TODO("Implemention Unfinished!!!")
    }

    fun hash(passphrase: String): String {
        //TODO
        return passphrase
    }

}

