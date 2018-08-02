package org.trustnote.wallet.biz.me

import android.view.View
import android.webkit.WebView
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import org.trustnote.wallet.R
import org.trustnote.wallet.biz.init.CWFragmentDisclaimer
import org.trustnote.wallet.biz.init.CreateWalletModel
import org.trustnote.wallet.biz.init.FragmentInitSetupPwd
import org.trustnote.wallet.biz.wallet.WalletManager
import org.trustnote.wallet.util.AndroidUtils
import org.trustnote.wallet.widget.MyDialogFragment
import org.trustnote.wallet.widget.MyTextWatcher
import org.trustnote.wallet.widget.PasswordStrength
import org.trustnote.wallet.widget.PwdStrength

class FragmentMeTou : CWFragmentDisclaimer() {


    lateinit var oldPwd: EditText

    override fun getLayoutId(): Int {
        return R.layout.f_me_tou
    }


}
