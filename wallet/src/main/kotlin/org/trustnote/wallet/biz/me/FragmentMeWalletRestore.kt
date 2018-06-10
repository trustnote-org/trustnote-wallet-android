package org.trustnote.wallet.biz.me

import android.view.View
import org.trustnote.wallet.R
import org.trustnote.wallet.biz.init.CWFragmentRestore
import org.trustnote.wallet.biz.init.CreateWalletModel
import org.trustnote.wallet.biz.wallet.TestData
import org.trustnote.wallet.biz.wallet.WalletManager
import org.trustnote.wallet.util.Prefs
import org.trustnote.wallet.util.Utils
import org.trustnote.wallet.widget.FragmentDialogInputPwd
import org.trustnote.wallet.widget.MyDialogFragment

class FragmentMeWalletRestore : CWFragmentRestore() {

    override fun initFragment(view: View) {
        fromInitActivity = false
        super.initFragment(view)

        MyDialogFragment.showMsg(activity, R.string.me_restore_warning)

    }

    override fun startRestore(isRemove: Boolean, mnemonics: String) {

        FragmentDialogInputPwd.showMe(activity) {

            Prefs.saveUserInFullRestore(true)
            CreateWalletModel.savePassphraseInRam(it)

            WalletManager.initWithMnemonic(it, mnemonics, isRemove)
            onBackPressed()
        }
    }

}

