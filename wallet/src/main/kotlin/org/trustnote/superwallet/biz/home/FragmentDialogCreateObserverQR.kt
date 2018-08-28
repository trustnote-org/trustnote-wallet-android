package org.trustnote.superwallet.biz.home

import android.view.View
import android.widget.Button
import android.widget.ImageView
import org.trustnote.superwallet.R
import org.trustnote.superwallet.biz.TTT
import org.trustnote.superwallet.biz.FragmentDialogBase
import org.trustnote.superwallet.util.TTTUtils

class FragmentDialogCreateObserverQR(val confirmLogic: (String) -> Unit = {}) : FragmentDialogBase(R.layout.l_dialog_create_wallet_observer_qr, confirmLogic) {

    var msg: String = "TTT Welcome"
    var qrStr: String = ""

    override fun initFragment(view: View) {

        qrStr = arguments.getString(TTT.KEY_QR_CODE) ?: ""

        view.findViewById<Button>(R.id.next_step).setOnClickListener {
            dismiss()
            confirmLogic.invoke("")
        }

        val qrImageView = view.findViewById<ImageView>(R.id.qr_code_imageview)

        TTTUtils.setupQRCode(qrStr, qrImageView)

    }

}