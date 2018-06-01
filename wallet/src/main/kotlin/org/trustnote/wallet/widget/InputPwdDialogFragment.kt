package org.trustnote.wallet.widget

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

import org.trustnote.wallet.R
import org.trustnote.wallet.biz.init.CreateWalletModel


class InputPwdDialogFragment() : DialogFragment() {

    var msg: String = "TTT Welcome"
    var cancelLogic: () -> Unit = {}
    var confirmLogic: () -> Unit = {}
    var isTwoButtons = true
    lateinit var pwdView: EditText
    lateinit var pwdErrView: View

    constructor(confirmLogic: () -> Unit) : this() {
        this.confirmLogic = confirmLogic
        isTwoButtons = false
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout to use as dialog or embedded fragment
        val view = inflater!!.inflate(R.layout.l_dialog_input_pwd, container, false)

        view.findViewById<Button>(R.id.first_button).setOnClickListener {
            dismiss()
        }

        view.findViewById<Button>(R.id.second_button).setOnClickListener {
            checkPwd()
        }

        pwdView = view.findViewById(R.id.pwd)
        pwdErrView = view.findViewById(R.id.pwd_err)
        return view
    }

    private fun checkPwd() {
        if (CreateWalletModel.verifyPwd(pwdView.text.toString())) {
            dismiss()
            confirmLogic.invoke()
        } else {
            pwdErrView.visibility = View.VISIBLE
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

        fun showMe(activity: FragmentActivity, confirmLogic: () -> Unit) {

            val newFragment = InputPwdDialogFragment(confirmLogic)
            newFragment.show(getFragmentTransaction(activity), null)
        }


    }
}