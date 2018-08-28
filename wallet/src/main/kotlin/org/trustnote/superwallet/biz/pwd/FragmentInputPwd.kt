package org.trustnote.superwallet.biz.pwd

import android.view.View
import android.view.WindowManager
import org.trustnote.superwallet.R
import org.trustnote.superwallet.TApp
import org.trustnote.superwallet.biz.init.CreateWalletModel
import org.trustnote.superwallet.biz.startMainActivityWithMenuId
import org.trustnote.superwallet.uiframework.ActivityBase
import org.trustnote.superwallet.uiframework.FragmentBase
import org.trustnote.superwallet.widget.FragmentDialogInputPwd

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
