package org.trustnote.superwallet.biz.me

import android.view.View
import android.webkit.WebView
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import org.trustnote.superwallet.R
import org.trustnote.superwallet.biz.init.CreateWalletModel
import org.trustnote.superwallet.biz.init.FragmentInitSetupPwd
import org.trustnote.superwallet.biz.wallet.WalletManager
import org.trustnote.superwallet.util.AndroidUtils
import org.trustnote.superwallet.widget.MyDialogFragment
import org.trustnote.superwallet.widget.MyTextWatcher
import org.trustnote.superwallet.widget.PasswordStrength
import org.trustnote.superwallet.widget.PwdStrength

class FragmentMeChangePwd : FragmentInitSetupPwd() {


    lateinit var oldPwd: EditText

    override fun getLayoutId(): Int {
        return R.layout.f_me_change_pwd
    }

    override fun initFragment(view: View) {

        fromInitActivity = false
        super.initFragment(view)

        oldPwd = findViewById(R.id.pwd_old)
        oldPwd.visibility = View.VISIBLE

    }

    override fun savePwdAndForward() {

        if (!CreateWalletModel.verifyPwd(oldPwd.text.toString())) {
            MyDialogFragment.showMsg(activity, R.string.me_change_pwd_old_pwd_err)
            return
        }

        val pwdString = pwd.text.toString()

        val pwdStrength = pwdStrength.computPwdStrength(pwdString)

        val isPwdVerifyOk = (pwd.text.toString() == pwdVerify.text.toString())
        val isPwdLengthOk = isPwdLengthOk(pwdString)
        val isPwdStrengOk = (pwdStrength == PwdStrength.NORMAL || pwdStrength == PwdStrength.STRONG)

        if (!isPwdLengthOk) {
            pwdError.setText(R.string.pwd_length_error)
            pwdError.visibility = View.VISIBLE
            return
        }

        if (!isPwdVerifyOk) {
            pwdVerifyError.visibility = View.VISIBLE
            return
        }

        WalletManager.model.updatePassword(oldPwd.text.toString(), pwd.text.toString())
        CreateWalletModel.savePassphraseInDisk(pwdString)
        CreateWalletModel.clearPassphraseInRam()
        onBackPressed()
    }

}
