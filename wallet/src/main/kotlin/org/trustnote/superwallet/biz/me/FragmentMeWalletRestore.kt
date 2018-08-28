package org.trustnote.superwallet.biz.me

import android.view.View
import android.view.WindowManager
import android.widget.TextView
import org.trustnote.superwallet.R
import org.trustnote.superwallet.biz.init.CWFragmentRestore
import org.trustnote.superwallet.biz.init.CreateWalletModel
import org.trustnote.superwallet.biz.wallet.WalletManager
import org.trustnote.superwallet.util.Prefs
import org.trustnote.superwallet.widget.FragmentDialogInputPwd
import org.trustnote.superwallet.widget.MyDialogFragment

class FragmentMeWalletRestore : CWFragmentRestore() {

    override fun initFragment(view: View) {
        fromInitActivity = false
        super.initFragment(view)

        MyDialogFragment.showMsg(activity, R.string.me_restore_warning, isTextAlignLeft = true)

        val title = findViewById<TextView>(R.id.mnemonic_restore_title)
        title.text = activity.getString(R.string.mnemonic_restore_me_title)

    }

    override fun onResume() {
        super.onResume()
        activity.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING or WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
    }

    override fun onPause() {
        super.onPause()
        activity.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

    }

    override fun startRestore(isRemove: Boolean, mnemonics: String) {

        val inputPwdDialog = FragmentDialogInputPwd()
        inputPwdDialog.confirmLogic = {

            Prefs.saveUserInFullRestore(true)
            CreateWalletModel.savePassphraseInRam(it)

            WalletManager.initWithMnemonic(it, mnemonics, isRemove)

            showWaitingUI {
                onBackPressed()
            }

        }

        addL2Fragment(inputPwdDialog)

    }

}

