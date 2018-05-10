package org.trustnote.wallet.biz.wallet

import org.trustnote.wallet.pojo.CREATE_WALLET_STATUS
import org.trustnote.wallet.util.Prefs

object CreateWalletModel {
    fun getCreationProgress(): CREATE_WALLET_STATUS {
        if (Prefs.profileExist()) {
            return CREATE_WALLET_STATUS.FINISHED
        }
        if (Prefs.pwdExist()) {
            return CREATE_WALLET_STATUS.PASSWORD_READY
        }

        return CREATE_WALLET_STATUS.GENESIS
    }
}

