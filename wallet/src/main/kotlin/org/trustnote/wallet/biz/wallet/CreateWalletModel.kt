package org.trustnote.wallet.biz.wallet

import org.trustnote.wallet.util.Prefs

object CreateWalletModel {
    fun getCreationProgress(): CREATE_WALLET_STATUS {
        if (Prefs.profileExist()) {
            return CREATE_WALLET_STATUS.FINISHED
        }
        if (Prefs.pwdExist()) {
            return CREATE_WALLET_STATUS.PASSWORD_READY
        }

        return CREATE_WALLET_STATUS.SELECT_CREATE_OR_RESTORE
    }
}

