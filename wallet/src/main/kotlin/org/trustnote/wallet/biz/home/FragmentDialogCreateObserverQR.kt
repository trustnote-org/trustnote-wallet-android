package org.trustnote.wallet.biz.home

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentTransaction
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView

import org.trustnote.wallet.R
import org.trustnote.wallet.biz.init.CreateWalletModel
import org.trustnote.wallet.util.AndroidUtils

class FragmentDialogCreateObserverQR() : DialogFragment() {

    var msg: String = "TTT Welcome"
    var confirmLogic: () -> Unit = {}
    var qrStr = ""

    constructor(qrStr: String, confirmLogic: () -> Unit) : this() {
        this.confirmLogic = confirmLogic
        this.qrStr = qrStr
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout to use as dialog or embedded fragment
        val view = inflater!!.inflate(R.layout.l_dialog_create_wallet_observer_qr, container, false)

        view.findViewById<Button>(R.id.next_step).setOnClickListener {
            dismiss()
            confirmLogic.invoke()
        }

        val qrImageView = view.findViewById<ImageView>(R.id.qr_code)
        val qrWidth = org.trustnote.wallet.TApp.context.resources.getDimension(R.dimen.qr_width).toInt()
        val qrBitmap = AndroidUtils.encodeStrAsQrBitmap(qrStr, qrWidth)

        qrImageView.setImageBitmap(qrBitmap)

        return view
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
            val prev = activity.supportFragmentManager.findFragmentByTag("dialog_QR")
            if (prev != null) {
                ft.remove(prev)
            }

            //ft.addToBackStack(null)
            return ft
        }

        fun showMe(qrStr: String, activity: FragmentActivity, confirmLogic: () -> Unit) {
            val newFragment = FragmentDialogCreateObserverQR(qrStr, confirmLogic)
            newFragment.show(getFragmentTransaction(activity), "dialog_QR")
        }


    }
}