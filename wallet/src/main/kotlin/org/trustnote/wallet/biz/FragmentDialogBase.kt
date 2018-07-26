package org.trustnote.wallet.biz

import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.DialogFragment
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import org.trustnote.wallet.util.AndroidUtils
import org.trustnote.wallet.util.Utils
import org.trustnote.wallet.widget.CustomViewFinderScannerActivity

open class FragmentDialogBase(private val layoutId: Int, private val confirmLogic: (String) -> Unit = {}) : DialogFragment() {

    var scanResHandler: (String) -> Unit = {}

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater!!.inflate(layoutId, container, false)
        initFragment(view)
        return view
    }

    open fun initFragment(view: View) {
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)

        if (dialog.window != null) {
            dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.window.requestFeature(Window.FEATURE_NO_TITLE)
        }

        return dialog
    }

    fun startScan(scanResHandler: (String) -> Unit = {}) {
        this.scanResHandler = scanResHandler
        AndroidUtils.initiateScan(this)
    }

    fun setupScan(scanIcon: View, scanResHandler: (String) -> Unit = {}) {

        scanIcon.setOnClickListener {
            startScan(scanResHandler)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Utils.logW("$requestCode ___  $resultCode")
        AndroidUtils.handleScanResult(data, scanResHandler)
    }

    private val REQ_CODE_ZXING_CAMERA_PERMISSION = 1900
    private val REQ_CODE_ZXING_SCAN_RESULT = 1901

    fun launchScanActivity() {

        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity,
                    arrayOf(Manifest.permission.CAMERA), REQ_CODE_ZXING_CAMERA_PERMISSION)
        } else {
            val intent = Intent(activity, CustomViewFinderScannerActivity::class.java)
            startActivityForResult(intent, REQ_CODE_ZXING_SCAN_RESULT)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            REQ_CODE_ZXING_CAMERA_PERMISSION -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    val intent = Intent(activity, CustomViewFinderScannerActivity::class.java)
                    startActivityForResult(intent, REQ_CODE_ZXING_SCAN_RESULT)
                }
            }
        }
        return
    }


}