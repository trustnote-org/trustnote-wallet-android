package org.trustnote.wallet.biz.home

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentTransaction
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.EditText
import com.google.zxing.integration.android.IntentIntegrator
import org.trustnote.wallet.R
import org.trustnote.wallet.biz.wallet.WalletManager
import org.trustnote.wallet.util.AndroidUtils
import org.trustnote.wallet.util.Utils

class FragmentDialogCreateObserverFinish() : DialogFragment() {

    var msg: String = "TTT Welcome"
    var confirmLogic: (String) -> Unit = {}
    lateinit var btn: Button
    lateinit var editText: EditText

    constructor(confirmLogic: (String) -> Unit) : this() {
        this.confirmLogic = confirmLogic
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout to use as dialog or embedded fragment
        val view = inflater!!.inflate(R.layout.l_dialog_create_wallet_observer_finish, container, false)

        btn = view.findViewById(R.id.create_wallet_observer_qr_finish_btn)

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

        editText = view.findViewById(R.id.create_wallet_observer_qr_finish_scan_res)

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

        return view
    }

    fun showScanResult(scanResultStr: String) {
        val addr: String = WalletManager.model.parseObserverAdd(scanResultStr)
        editText.setText(addr)
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

    private fun updateUI() {
        val scanCode = editText.text.toString()
        if (scanCode.isNotEmpty()) {
            AndroidUtils.enableBtn(btn)
        } else {
            AndroidUtils.disableBtn(btn)
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

    companion object {

        private fun getFragmentTransaction(activity: FragmentActivity): FragmentTransaction {

            val ft = activity.supportFragmentManager.beginTransaction()
            val prev = activity.supportFragmentManager.findFragmentByTag("dialog")
            if (prev != null) {
                ft.remove(prev)
            }

            ft.addToBackStack(null)
            return ft
        }

        fun showMe(activity: FragmentActivity, confirmLogic: (String) -> Unit) {
            val newFragment = FragmentDialogCreateObserverFinish(confirmLogic)
            newFragment.show(getFragmentTransaction(activity), "dialog")
        }

    }
}