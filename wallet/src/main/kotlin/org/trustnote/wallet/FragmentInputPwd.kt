package org.trustnote.wallet

import android.annotation.SuppressLint
import android.view.View
import org.trustnote.wallet.biz.wallet.CreateWalletFragment
import org.trustnote.wallet.uiframework.BaseActivity
import org.trustnote.wallet.widget.InputPwdDialogFragment


@SuppressLint("ValidFragment")  //TODO: the fragment cannot re-create from tomb.
open class FragmentInputPwd(layoutId: Int) : CreateWalletFragment(layoutId) {
    
    override fun initFragment(view: View) {
        view.findViewById<View>(R.id.pwd_exist_clickcontinue).setOnClickListener {
            InputPwdDialogFragment.showMe(activity, {
                TApp.userAlreadyInputPwd = true
                (activity as BaseActivity).iamDone()
            })
        }
    }

}
