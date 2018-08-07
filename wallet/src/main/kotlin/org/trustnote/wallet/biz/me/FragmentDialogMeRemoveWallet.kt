package org.trustnote.wallet.biz.me

import android.view.View
import android.widget.Button
import org.trustnote.wallet.R
import org.trustnote.wallet.biz.FragmentDialogBase
import org.trustnote.wallet.biz.FragmentPageBase

class FragmentDialogMeRemoveWallet(val confirmLogic: (String) -> Unit = {}) : FragmentPageBase() {


    override fun getLayoutId(): Int {
        return R.layout.l_dialog_remove
    }

    override fun initFragment(view: View) {
        super.initFragment(view)

        val btn1 = view.findViewById<Button>(R.id.first_button)
        val btn2 = view.findViewById<Button>(R.id.second_button)

        btn2.setOnClickListener {
            onBackPressed()
            confirmLogic.invoke("")
        }

        btn1.setOnClickListener {
            onBackPressed()
        }

    }

    override fun updateUI() {
        super.updateUI()
        mToolbar.visibility = View.INVISIBLE
    }


}