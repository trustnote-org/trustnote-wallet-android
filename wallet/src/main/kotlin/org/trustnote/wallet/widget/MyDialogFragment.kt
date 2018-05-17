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
import android.widget.TextView

import org.trustnote.wallet.R


class MyDialogFragment() : DialogFragment() {

    var msg: String = "TTT Welcome"
    var cancelLogic: () -> Unit = {}
    var confirmLogic: () -> Unit = {}
    var isTwoButtons = true

    constructor(msg: String, confirmLogic: () -> Unit) : this(msg, confirmLogic, {}) {
        isTwoButtons = false
    }

    constructor(msg: String, confirmLogic: () -> Unit, cancelLogic: () -> Unit) : this() {
        this.msg = msg
        this.confirmLogic = confirmLogic
        this.cancelLogic = cancelLogic
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout to use as dialog or embedded fragment
        val view = inflater!!.inflate(R.layout.l_dialog_twobutton, container, false)

        view.findViewById<TextView>(R.id.msg).text = msg

        view.findViewById<Button>(R.id.first_button).setOnClickListener {
            dismiss()
            cancelLogic.invoke()
        }

        view.findViewById<Button>(R.id.first_button).visibility = if (isTwoButtons) View.VISIBLE else View.GONE
        view.findViewById<View>(R.id.line_between_btns).visibility = if (isTwoButtons) View.VISIBLE else View.GONE

        view.findViewById<Button>(R.id.second_button).setOnClickListener {
            dismiss()
            confirmLogic.invoke()
        }

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

        private fun newInstance(msg: String, confirmLogic: () -> Unit): MyDialogFragment {
            return MyDialogFragment(msg, confirmLogic)
        }

        private fun newInstance(msg: String, confirmLogic: () -> Unit, cancelLogic: () -> Unit): MyDialogFragment {
            return MyDialogFragment(msg, confirmLogic, cancelLogic)
        }

        private fun getFragmentTransaction(activity: FragmentActivity): FragmentTransaction {

            val ft = activity.supportFragmentManager.beginTransaction()
            val prev = activity.supportFragmentManager.findFragmentByTag("dialog")
            if (prev != null) {
                ft.remove(prev)
            }
            ft.addToBackStack(null)
            return ft
        }

        private fun showDialog1Btn(activity: FragmentActivity, msg: String, confirmLogic: () -> Unit) {

            val newFragment = MyDialogFragment.newInstance(msg, confirmLogic)
            newFragment.show(getFragmentTransaction(activity), "dialog")
        }

        fun showMsg(activity: FragmentActivity, strResId: Int) {
            val msg = activity.getString(strResId)!!
            showDialog1Btn(activity, msg, {})
        }

        fun showDialog2Btns(activity: FragmentActivity, strResId: Int, confirmLogic: () -> Unit) {
            val msg = activity.getString(strResId)!!
            val newFragment = MyDialogFragment.newInstance(msg, confirmLogic, {})
            newFragment.show(getFragmentTransaction(activity), "dialog")
        }

    }
}