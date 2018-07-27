package org.trustnote.wallet.biz.me

import android.os.Bundle
import android.view.View
import org.trustnote.wallet.R
import org.trustnote.wallet.biz.FragmentProgressBlocking
import org.trustnote.wallet.biz.init.CWFragmentRestore
import org.trustnote.wallet.biz.init.CreateWalletModel
import org.trustnote.wallet.biz.wallet.WalletManager
import org.trustnote.wallet.util.AndroidUtils
import org.trustnote.wallet.util.Prefs
import org.trustnote.wallet.widget.FragmentDialogInputPwd
import org.trustnote.wallet.widget.MyDialogFragment

class FragmentMeWalletRestore : CWFragmentRestore() {

    override fun initFragment(view: View) {
        fromInitActivity = false
        super.initFragment(view)

        MyDialogFragment.showMsg(activity, R.string.me_restore_warning)

    }

    override fun startRestore(isRemove: Boolean, mnemonics: String) {

        val f = FragmentDialogMeRemoveWallet {
            val removeResult = WalletManager.model.removeWallet(credential)
            if (removeResult) {
                activity.onBackPressed()
            } else {
                MyDialogFragment.showMsg(getMyActivity(), R.string.me_wallet_remove_wallet_deny)
            }
        }

        val inputPwdDialog = FragmentDialogInputPwd()
        inputPwdDialog.confirmLogic = {

            Prefs.saveUserInFullRestore(true)
            CreateWalletModel.savePassphraseInRam(it)

            WalletManager.initWithMnemonic(it, mnemonics, isRemove)
            onBackPressed()

            showWaitingUI()

        }
        addL2Fragment(inputPwdDialog)
    }

}

