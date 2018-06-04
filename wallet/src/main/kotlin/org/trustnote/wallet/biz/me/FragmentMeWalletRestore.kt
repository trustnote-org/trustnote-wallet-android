package org.trustnote.wallet.biz.me

import android.view.View
import org.trustnote.wallet.R
import org.trustnote.wallet.biz.init.CWFragmentRestore
import org.trustnote.wallet.biz.wallet.WalletManager
import org.trustnote.wallet.widget.MyDialogFragment

class FragmentMeWalletRestore : CWFragmentRestore() {

    override fun initFragment(view: View) {
        fromInitActivity = false
        super.initFragment(view)

        MyDialogFragment.showMsg(activity, R.string.me_restore_warning)

    }

    override fun startRestore(privKey: String, isRemove: Boolean, mnemonics: String) {
        WalletManager.initWithMnemonic(mnemonics, isRemove, privKey)
        onBackPressed()
    }

}

