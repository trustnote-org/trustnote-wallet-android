package org.trustnote.superwallet.biz.me

import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import org.trustnote.superwallet.R
import org.trustnote.superwallet.biz.TTT
import org.trustnote.superwallet.biz.FragmentDialogBase
import org.trustnote.superwallet.util.TTTUtils

class FragmentDialogAuthorizeSuccessful(val confirmLogic: (String) -> Unit = {}) : FragmentDialogBase(R.layout.l_dialog_create_wallet_observer_qr, confirmLogic) {

    var msg: String = "TTT Welcome"
    var qrStr: String = ""

    override fun initFragment(view: View) {

        qrStr = arguments.getString(TTT.KEY_QR_CODE) ?: ""

        val title = view.findViewById<TextView>(R.id.show_qr_title)
        val msg = view.findViewById<TextView>(R.id.show_qr_msg)
        val btn = view.findViewById<Button>(R.id.next_step)

        title.setText(R.string.me_wallet_cold_code_sucessful_title)
        msg.setText(R.string.me_wallet_cold_code_sucessful_msg)
        btn.setText(R.string.me_wallet_cold_code_sucessful_btn)

        view.findViewById<Button>(R.id.next_step).setOnClickListener {
            dismiss()
            confirmLogic.invoke("")
        }

        val qrImageView = view.findViewById<ImageView>(R.id.qr_code_imageview)

        TTTUtils.setupQRCode(qrStr, qrImageView)

    }

}