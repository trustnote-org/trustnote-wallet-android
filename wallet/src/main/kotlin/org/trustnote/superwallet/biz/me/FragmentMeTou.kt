package org.trustnote.superwallet.biz.me

import android.view.View
import android.webkit.WebView
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import org.trustnote.superwallet.R
import org.trustnote.superwallet.biz.init.CWFragmentDisclaimer
import org.trustnote.superwallet.biz.init.CreateWalletModel
import org.trustnote.superwallet.biz.init.FragmentInitSetupPwd
import org.trustnote.superwallet.biz.wallet.WalletManager
import org.trustnote.superwallet.util.AndroidUtils
import org.trustnote.superwallet.widget.MyDialogFragment
import org.trustnote.superwallet.widget.MyTextWatcher
import org.trustnote.superwallet.widget.PasswordStrength
import org.trustnote.superwallet.widget.PwdStrength

class FragmentMeTou : CWFragmentDisclaimer() {


    lateinit var oldPwd: EditText

    override fun getLayoutId(): Int {
        return R.layout.f_me_tou
    }


}
