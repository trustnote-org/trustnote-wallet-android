package org.trustnote.wallet.biz.home

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.EditText
import com.google.zxing.integration.android.IntentIntegrator
import org.trustnote.wallet.R
import org.trustnote.wallet.biz.FragmentDialogBase
import org.trustnote.wallet.util.AndroidUtils
import org.trustnote.wallet.util.TTTUtils
import org.trustnote.wallet.util.Utils

class FragmentDialogCreateObserverFinish(val confirmLogic: (String) -> Unit = {}) : FragmentDialogBase(R.layout.l_dialog_create_wallet_observer_finish, confirmLogic) {

    var msg: String = "TTT Welcome"
    lateinit var btn: Button
    lateinit var editText: EditText

    override fun initFragment(view: View) {
        btn = view.findViewById(R.id.create_wallet_observer_qr_finish_btn)
        editText = view.findViewById(R.id.create_wallet_observer_qr_finish_scan_res)

        btn.setOnClickListener {
            dismiss()
            confirmLogic.invoke(editText.text.toString())
        }

        view.findViewById<View>(R.id.create_wallet_observer_qr_finish_scan).setOnClickListener {

            val integrator = IntentIntegrator.forSupportFragment(this)
            integrator.setOrientationLocked(true)
            integrator.setBeepEnabled(true)
            integrator.initiateScan()
        }


        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                updateUI()
            }
        })

        updateUI()

    }

    private fun updateUI() {
        val scanCode = editText.text.toString()
        if (scanCode.isNotEmpty()) {
            AndroidUtils.enableBtn(btn)
        } else {
            AndroidUtils.disableBtn(btn)
        }
    }

    //    TTT: {
    //        "type": "c2",
    //        "addr": "0NEYV3ZCRAJYGJDS5UNN4EOZGNVZJXOLI",
    //        "v": 1234
    //    }

    //    TTT: {
    //        "type": "c1",
    //        "name": "TTT",
    //        "pub": "xpub6CiT96vM5krNhwFA4ro5nKJ6nq9WykFmAsP18jC1Aa3URb69rvUHw6uvU51MQPkMZQ6BLiC5C1E3Zbsm7Xob3FFhNHJkN3v9xuxfqFFKPP5",
    //        "n": 0,
    //        "v": 1234
    //    }

    fun showScanResult(scanResultStr: String) {
        val addr: String? = TTTUtils.scanStringToJsonObject(scanResultStr).get("addr")?.asString
        editText.setText(addr ?: "")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Utils.logW("$requestCode ___  $resultCode")
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {
                Utils.debugToast("Cancelled")
            } else {
                showScanResult(result.contents)
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)

        if (dialog.window != null) {
            dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.window.requestFeature(Window.FEATURE_NO_TITLE)
        }

        return dialog
    }
}