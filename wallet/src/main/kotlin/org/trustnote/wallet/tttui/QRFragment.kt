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
import org.trustnote.wallet.R
import org.trustnote.wallet.uiframework.FragmentBase
import org.trustnote.wallet.util.AndroidUtils
import org.trustnote.wallet.util.Utils

class QRFragment : FragmentBase() {

    override fun getLayoutId(): Int {
        return R.layout.f_show_qrcode
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.f_show_qrcode, container, false)
    }

    lateinit var scanResultTextView: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bitmap = AndroidUtils.encodeStrAsQrBitmap("guodaping", 300)
        view!!.findViewById<ImageView>(R.id.qr_code_bitmap).setImageBitmap(bitmap)

//        view.findViewById<Button>(R.id.qrcode_scan).setOnClickListener(View.OnClickListener {
//            IntentIntegrator.forSupportFragment(this).initiateScan()        })

        scanResultTextView = view.findViewById(R.id.qrcode_scan_res)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
//        if (result != null) {
//            if (result.contents == null) {
//                Utils.debugToast("Scan Cancelled")
//            } else {
//                scanResultTextView.text = result.contents
//            }
//        }
//
    }

}