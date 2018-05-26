package org.trustnote.wallet.biz.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import org.trustnote.wallet.R
import org.trustnote.wallet.TTT
import org.trustnote.wallet.biz.FragmentDialogBase
import org.trustnote.wallet.util.AndroidUtils

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
        val qrWidth = org.trustnote.wallet.TApp.context.resources.getDimension(R.dimen.qr_width).toInt()

        val qrBitmap = AndroidUtils.encodeStrAsQrBitmap(qrStr, qrWidth)

        qrImageView.setImageBitmap(qrBitmap)

    }

}