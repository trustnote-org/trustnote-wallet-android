package org.trustnote.wallet.biz.init

import android.view.View
import android.webkit.WebView
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import org.trustnote.wallet.R
import org.trustnote.wallet.TApp
import org.trustnote.wallet.biz.wallet.TestData
import org.trustnote.wallet.util.AndroidUtils
import org.trustnote.wallet.util.Utils
import org.trustnote.wallet.widget.MyTextWatcher
import org.trustnote.wallet.widget.PasswordStrength
import org.trustnote.wallet.widget.PwdStrength

open class FragmentInitSetupPwd : FragmentInit() {

    lateinit var pwdConfirm: Button
    lateinit var pwd: EditText
    lateinit var pwdVerify: EditText
    lateinit var pwdVerifyError: TextView
    lateinit var pwdError: TextView
    lateinit var pwdPrompt: TextView
    lateinit var pwdStrength: PasswordStrength
    private val pwdTextWatcher = MyTextWatcher(this)

    override fun getLayoutId(): Int {
        return R.layout.f_init_pwd
    }

    override fun initFragment(view: View) {
        super.initFragment(view)

        pwdConfirm = mRootView.findViewById(R.id.pwd_confirm)
        pwd = mRootView.findViewById(R.id.pwd)
        pwdVerify = mRootView.findViewById(R.id.pwd_verify)
        pwdError = mRootView.findViewById(R.id.pwd_err)
        pwdVerifyError = mRootView.findViewById(R.id.pwd_verify_err)
        pwdStrength = mRootView.findViewById(R.id.pwd_strength)
        pwdPrompt = mRootView.findViewById(R.id.pwd_prompt)

        pwd.addTextChangedListener(pwdTextWatcher)
        pwdVerify.addTextChangedListener(pwdTextWatcher)

        pwdConfirm.setOnClickListener {
            savePwdAndForward()
        }

        if (Utils.isDeveloperFeature()) {
            pwd.setText(TestData.password)
            pwdVerify.setText(TestData.password)
        }

        val webView: WebView = view.findViewById(R.id.pwd_warning)

        AndroidUtils.setupWarningWebView(webView, R.string.PWD_WARNING1,
                R.string.PWD_WARNING2)

        //        if (Utils.isUseDebugOption()) {
        //            pwd.setText("qwer1234")
        //            pwdVerify.setText("qwer1234")
        //        }

        updateUI()
    }

    override fun updateUI() {

        if (pwd.text.toString().isNotBlank() && pwdVerify.text.toString().isNotBlank()) {
            AndroidUtils.enableBtn(pwdConfirm)
        } else {
            AndroidUtils.disableBtn(pwdConfirm)
        }


        AndroidUtils.disableBtn(pwdConfirm)
        pwdError.visibility = View.INVISIBLE
        pwdVerifyError.visibility = View.INVISIBLE
        pwdStrength.computPwdStrength("")

        val pwdString = pwd.text.toString()

        val pwdStrengthValue = pwdStrength.computPwdStrength(pwdString)

        //val isPwdVerifyOk = (pwd.text.toString() == pwdVerify.text.toString())
        val isPwdLengthOk = isPwdLengthOk(pwdString)
        val isPwdStrengOk = (pwdStrengthValue == PwdStrength.NORMAL || pwdStrengthValue == PwdStrength.STRONG)


        if (pwd.text.toString().isBlank() && pwdVerify.text.toString().isBlank()) {
            AndroidUtils.disableBtn(pwdConfirm)
            pwdPrompt.visibility = View.VISIBLE
            pwdStrength.visibility = View.INVISIBLE
            return
        } else {
            pwdPrompt.visibility = View.INVISIBLE
            pwdStrength.visibility = View.VISIBLE
        }


        if (pwd.text.toString().isNotBlank() && pwdVerify.text.toString().isNotBlank()) {
            AndroidUtils.enableBtn(pwdConfirm)
        } else {
            AndroidUtils.disableBtn(pwdConfirm)
        }


        if (pwdString.isNotBlank() && !isPwdStrengOk) {
            pwdError.setText(R.string.pwd_strength_warning)
            pwdError.visibility = View.VISIBLE
        }

        pwdVerifyError.visibility = View.INVISIBLE

    }

    open fun savePwdAndForward() {

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

        CreateWalletModel.savePassphraseInDisk(pwdString)
        CreateWalletModel.savePassphraseInRam(pwdString)

        if (fromInitActivity) {
            TApp.userAlreadyInputPwd = true
        }

        removeMeFromBackStack()
        nextPage(AndroidUtils.getTagForNextPage(arguments))
    }

    private fun isPwdStrengthOk(pwd: String): Boolean {
        return true
    }

    fun isPwdLengthOk(pwd: String): Boolean {
        return pwd.isNotEmpty() && pwd.length >= 8
    }

    override fun onBackPressed() {
        if (fromInitActivity) {
            nextPage(R.layout.f_init_create_or_restore)
        } else {
            super.onBackPressed()
        }
    }

}
