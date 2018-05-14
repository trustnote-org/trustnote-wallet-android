package org.trustnote.wallet.biz.wallet

import android.annotation.SuppressLint
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import org.trustnote.wallet.R
import org.trustnote.wallet.util.AndroidUtils
import org.trustnote.wallet.util.Utils

@SuppressLint("ValidFragment")
class CWFragmentPwd(layoutId: Int) : CreateWalletFragment(layoutId) {

    lateinit var pwdConfirm: Button
    lateinit var pwd: EditText
    lateinit var pwdVerify: EditText
    lateinit var pwdVerifyError: TextView
    lateinit var pwdError: TextView
    private val pwdTextWatcher: PwdTextWatcher = PwdTextWatcher()

    override fun initFragment(view: View) {

        pwdConfirm = mRootView.findViewById(R.id.pwd_confirm)
        pwd = mRootView.findViewById(R.id.pwd)
        pwdVerify = mRootView.findViewById(R.id.pwd_verify)
        pwdError = mRootView.findViewById(R.id.pwd_err)
        pwdVerifyError = mRootView.findViewById(R.id.pwd_verify_err)

        pwd.addTextChangedListener(pwdTextWatcher)
        pwdVerify.addTextChangedListener(pwdTextWatcher)


        pwdConfirm.setOnClickListener {
            savePwdAndForward()
        }

        updateUI()
    }


    private inner class PwdTextWatcher : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }

        override fun afterTextChanged(s: Editable?) {
            this@CWFragmentPwd.updateUI()
        }

    }

    private fun updateUI() {


        AndroidUtils.disableBtn(pwdConfirm)
        pwdError.visibility = View.INVISIBLE
        pwdVerifyError.visibility = View.INVISIBLE


        val pwdString = pwd.text.toString()

        val isPwdVerifyOk = (pwd.text.toString() == pwdVerify.text.toString())
        val isPwdLengthOk = isPwdLengthOk(pwdString)
        val isPwdStrengOk = isPwdStrengthOk(pwdString)

        if (isPwdVerifyOk && isPwdLengthOk) {
            AndroidUtils.enableBtn(pwdConfirm)
            return
        }

        if (!isPwdLengthOk) {
            pwdError.setText(R.string.pwd_length_error)
            pwdError.visibility = View.VISIBLE
        }

        if (isPwdLengthOk && !isPwdStrengOk) {
            pwdError.setText(R.string.pwd_strength_warning)
            pwdError.visibility = View.VISIBLE
        }

        if (!isPwdVerifyOk) {
            pwdVerifyError.visibility = View.VISIBLE
        }

    }

    private fun savePwdAndForward() {

        nextPage()
    }

    private fun isPwdStrengthOk(pwd: String): Boolean {
        return true
    }


    private fun isPwdLengthOk(pwd: String): Boolean {
        return pwd.isNotEmpty() && pwd.length >= 8
    }

    override fun onBackPressed() {
        nextPage(R.layout.f_new_seed_or_restore)
    }

}
