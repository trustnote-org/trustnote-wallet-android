package org.trustnote.wallet.widget

import android.view.View
import android.view.WindowManager
import android.widget.Button
import org.trustnote.wallet.R
import org.trustnote.wallet.biz.FragmentPageBase
import org.trustnote.wallet.biz.init.CreateWalletModel
import org.trustnote.wallet.biz.wallet.TestData
import org.trustnote.wallet.util.AndroidUtils
import org.trustnote.wallet.util.Prefs
import org.trustnote.wallet.util.Utils

class FragmentDialogInputPwd() : FragmentPageBase() {


    var msg: String = "TTT Welcome"

    var cancelLogic: () -> Unit = {}
    var confirmLogic: (String) -> Unit = {}
    var dontRunOnBackPressed: Boolean = false
    var isTwoButtons = true
    lateinit var pwdView: ClearableEditText
    lateinit var pwdErrView: View

    constructor(confirmLogic: (String) -> Unit) : this() {
        this.confirmLogic = confirmLogic
        isTwoButtons = false
    }

    override fun getLayoutId(): Int {
        return R.layout.l_dialog_input_pwd
    }

    override fun initFragment(view: View) {
        super.initFragment(view)
        // Inflate the layout to use as dialog or embedded fragment

        view.findViewById<Button>(R.id.first_button).setOnClickListener {
            onBackPressed()
        }

        view.findViewById<Button>(R.id.second_button).setOnClickListener {
            checkPwd(pwdView.text.toString())
        }


        pwdView = view.findViewById(R.id.pwd)

        pwdView.addTextChangedListener(MyTextWatcher(this))
        //pwdView.transformationMethod = MyPasswordTransformationMethod()

        pwdErrView = view.findViewById(R.id.pwd_err)

        AndroidUtils.hideErrIfHasFocus(pwdView, pwdErrView)

        if (Utils.isDeveloperFeature()) {
            pwdView.setText(TestData.password)
        }

    }

    override fun onResume() {
        super.onResume()
        activity.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN or WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
    }

    override fun onPause() {
        super.onPause()
        activity.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
    }

    private fun checkPwd(password: String) {
        if (CreateWalletModel.verifyPwd(password)) {
            if (!dontRunOnBackPressed) {
                onBackPressed()
            }
            savePwdInRamOrNot(password)
            confirmLogic.invoke(password)
        } else {
            pwdErrView.visibility = View.VISIBLE
        }
    }

    private fun savePwdInRamOrNot(pwd: String) {
        if (Prefs.isUserInFullRestore()) {
            CreateWalletModel.savePassphraseInRam(pwd)
        }
    }

    override fun updateUI() {
        super.updateUI()
        mToolbar.visibility = View.INVISIBLE

        //AndroidUtils.enableBtn(findViewById<Button>(R.id.first_button), pwdView.text.isNotEmpty())
        AndroidUtils.enableBtn(findViewById<Button>(R.id.second_button), pwdView.text.isNotEmpty())
    }

}