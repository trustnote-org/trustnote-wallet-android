package org.trustnote.wallet.biz.pwd

import android.view.View
import org.trustnote.wallet.R
import org.trustnote.wallet.TApp
import org.trustnote.wallet.biz.init.CreateWalletFragment
import org.trustnote.wallet.uiframework.BaseActivity
import org.trustnote.wallet.widget.InputPwdDialogFragment


open class FragmentInputPwd : CreateWalletFragment() {

    override fun getLayoutId(): Int {
        return R.layout.f_input_pwd
    }

    override fun initFragment(view: View) {
        view.findViewById<View>(R.id.pwd_exist_clickcontinue).setOnClickListener {
            InputPwdDialogFragment.showMe(activity, {
                TApp.userAlreadyInputPwd = true
                (activity as BaseActivity).iamDone()
            })
        }
    }

}
