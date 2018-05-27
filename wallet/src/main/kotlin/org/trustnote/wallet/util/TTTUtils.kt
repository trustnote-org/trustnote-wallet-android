package org.trustnote.wallet.util

import android.widget.ImageView
import android.widget.TextView
import org.trustnote.db.TxType
import org.trustnote.wallet.R
import org.trustnote.wallet.TTT

object TTTUtils {

    fun toAddressQRText(address: String): String {
        return "TTT.KEY_TTT_QR_TAG:$address"
    }

    fun setupAddressQRCode(address: String, qrImageView: ImageView) {
        val qrWidth = org.trustnote.wallet.TApp.context.resources.getDimension(R.dimen.qr_width).toInt()
        val qrForAddress = toAddressQRText(address)
        val qrBitmap = AndroidUtils.encodeStrAsQrBitmap(qrForAddress, qrWidth)
        qrImageView.setImageBitmap(qrBitmap)
    }

    fun parseAddressFromQRCode(qrCode: String): String {
        //TODO: Address RULE
        return if (qrCode.isNotEmpty() && qrCode.length > 4) {
            qrCode.substring(4)
        } else {
            ""
        }
    }

    fun formatMN(view: TextView, amount: Long) {
        view.setText(formatMN(amount))
    }

    fun formatMN(amount: Long): String {
        return "${amount / TTT.w_coinunitValue}.${(amount % TTT.w_coinunitValue).toString().padStart(6, '0').substring(0, 4)}"
    }

    fun isValidAddress(address: String): Boolean {
        //TODO:
        return address.isNotEmpty()
    }

    fun isValidAmount(amount: String, balance: Long): Boolean {
        return amount.isNotEmpty() && amount.toFloat() * TTT.w_coinunitValue <= balance
    }

}