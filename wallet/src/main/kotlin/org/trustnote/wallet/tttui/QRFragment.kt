package org.trustnote.wallet.tttui

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color.BLACK
import android.graphics.Color.WHITE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import com.google.zxing.integration.android.IntentIntegrator
import org.trustnote.wallet.R
import org.trustnote.wallet.uiframework.BaseFragment
import org.trustnote.wallet.util.Utils

class QRFragment : BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.f_show_qrcode, container, false)
    }

    lateinit var scanResultTextView: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val bitmap = encodeAsBitmap("guodaping")
        view!!.findViewById<ImageView>(R.id.qr_code_bitmap).setImageBitmap(bitmap)


        view.findViewById<Button>(R.id.qrcode_scan).setOnClickListener(View.OnClickListener {
            IntentIntegrator.forSupportFragment(this).initiateScan()        })

        scanResultTextView = view.findViewById(R.id.qrcode_scan_res)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {
                Utils.debugToast("Scan Cancelled")
            } else {
                scanResultTextView.text = result.contents
            }
        }
    }



    @Throws(WriterException::class)
    internal fun encodeAsBitmap(str: String): Bitmap? {
        val result: BitMatrix
        val width = 300
        try {
            result = MultiFormatWriter().encode(str,
                    BarcodeFormat.QR_CODE, width, width, null)
        } catch (iae: IllegalArgumentException) {
            // Unsupported format
            return null
        }

        val w = result.getWidth()
        val h = result.getHeight()
        val pixels = IntArray(w * h)
        for (y in 0 until h) {
            val offset = y * w
            for (x in 0 until w) {
                pixels[offset + x] = if (result.get(x, y)) BLACK else WHITE
            }
        }
        val bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        bitmap.setPixels(pixels, 0, width, 0, 0, w, h)
        return bitmap
    }


}