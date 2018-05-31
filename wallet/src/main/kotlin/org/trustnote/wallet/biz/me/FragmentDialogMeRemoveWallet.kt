package org.trustnote.wallet.biz.me

import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import org.trustnote.wallet.R
import org.trustnote.wallet.TTT
import org.trustnote.wallet.biz.FragmentDialogBase
import org.trustnote.wallet.util.TTTUtils

class FragmentDialogMeRemoveWallet(val confirmLogic: (String) -> Unit = {}) : FragmentDialogBase(R.layout.l_dialog_remove, confirmLogic) {


    override fun initFragment(view: View) {

        val btn1 = view.findViewById<Button>(R.id.first_button)
        val btn2 = view.findViewById<Button>(R.id.second_button)

        btn2.setOnClickListener {
            dismiss()
            confirmLogic.invoke("")
        }

        btn1.setOnClickListener {
            dismiss()
        }

    }

}