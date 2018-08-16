package org.trustnote.wallet.biz.pwd

import android.view.View
import android.view.WindowManager
import org.trustnote.wallet.R
import org.trustnote.wallet.TApp
import org.trustnote.wallet.biz.init.CreateWalletModel
import org.trustnote.wallet.biz.startMainActivityWithMenuId
import org.trustnote.wallet.uiframework.ActivityBase
import org.trustnote.wallet.uiframework.FragmentBase
import org.trustnote.wallet.widget.FragmentDialogInputPwd

open class FragmentInputPwd : FragmentBase() {

    override fun getLayoutId(): Int {
        return R.layout.f_input_pwd
    }

    override fun initFragment(view: View) {

        isBottomLayerUI = true

        super.initFragment(view)

        showPwdDialog()

        view.findViewById<View>(R.id.pwd_exist_clickcontinue).setOnClickListener {
            showPwdDialog()
        }
    }

    private fun showPwdDialog() {
        val f = FragmentDialogInputPwd()
        f.dontRunOnBackPressed = true
        f.confirmLogic = {
            TApp.userAlreadyInputPwd = true
            (activity as ActivityBase).iamDone()
        }
        addFragment(f, isUseAnimation = false)
    }

}
