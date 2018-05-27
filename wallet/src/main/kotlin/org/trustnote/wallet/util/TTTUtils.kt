package org.trustnote.wallet.util

import android.widget.ImageView
import org.trustnote.wallet.R

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

}