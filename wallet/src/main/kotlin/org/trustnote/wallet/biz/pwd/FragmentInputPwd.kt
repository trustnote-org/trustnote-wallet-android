package org.trustnote.wallet.biz.pwd

import android.view.View
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

        FragmentDialogInputPwd.showMe(activity, {
            TApp.userAlreadyInputPwd = true
            (activity as ActivityBase).iamDone()
        })

        view.findViewById<View>(R.id.pwd_exist_clickcontinue).setOnClickListener {
            FragmentDialogInputPwd.showMe(activity, {
                TApp.userAlreadyInputPwd = true

                (activity as ActivityBase).iamDone()
            })
        }
    }

}
