package org.trustnote.wallet.widget

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentTransaction
import android.text.Layout
import android.view.*
import android.widget.Button
import android.widget.TextView

import org.trustnote.wallet.R
import android.view.WindowManager
import org.trustnote.wallet.util.AndroidUtils

class MyDialogFragment() : DialogFragment() {

    var msg: String = "TTT Welcome"
    var cancelLogic: () -> Unit = {}
    var confirmLogic: () -> Unit = {}
    var isTwoButtons = true
    var isTextAlignLeft = false
    var msgView: TextView? = null
    var forUpgradeInfoUI = false

    constructor(msg: String, confirmLogic: () -> Unit) : this(msg, confirmLogic, {}) {
        isTwoButtons = false
    }

    constructor(msg: String, confirmLogic: () -> Unit, cancelLogic: () -> Unit) : this() {
        this.msg = msg
        this.confirmLogic = confirmLogic
        this.cancelLogic = cancelLogic
    }

    override fun onResume() {
        super.onResume()
        dialog.window!!.setLayout(AndroidUtils.getScreenWidth(activity) * 270 / 375, ViewGroup.LayoutParams.WRAP_CONTENT)

        if (isTextAlignLeft) {
            msgView!!.gravity = Gravity.LEFT
        }

    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout to use as dialog or embedded fragment
        val view = inflater!!.inflate(if (forUpgradeInfoUI) R.layout.l_dialog_upgradeinfo else R.layout.l_dialog_twobutton, container, false)

        msgView = view.findViewById<TextView>(R.id.msg)
        msgView!!.text = msg

        if (isTextAlignLeft) {
            view.findViewById<TextView>(R.id.msg).gravity = Gravity.LEFT
        }

        view.findViewById<Button>(R.id.first_button).setOnClickListener {
            dismiss()
            cancelLogic.invoke()
        }

        view.findViewById<Button>(R.id.first_button).visibility = if (isTwoButtons) View.VISIBLE else View.GONE
        view.findViewById<View>(R.id.line_between_btns).visibility = if (isTwoButtons) View.VISIBLE else View.GONE

        view.findViewById<Button>(R.id.second_button).setOnClickListener {
            if (isCancelable) {
                dismiss()
            }
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
            val prev = activity.supportFragmentManager.findFragmentByTag(null)
            if (prev != null) {
                ft.remove(prev)
            }
            ft.addToBackStack(null)
            return ft
        }

        fun showDialog1Btn(activity: FragmentActivity, msg: String, isCancelable: Boolean = true, isTextAlignLeft: Boolean = false, forUpgradeInfoUI: Boolean = false, confirmLogic: () -> Unit) {

            val newFragment = MyDialogFragment.newInstance(msg, confirmLogic)
            newFragment.isCancelable = isCancelable
            newFragment.isTextAlignLeft = isTextAlignLeft
            newFragment.forUpgradeInfoUI = forUpgradeInfoUI

            newFragment.show(getFragmentTransaction(activity), null)
        }

        fun showMsg(activity: FragmentActivity, strResId: Int, isTextAlignLeft: Boolean = false) {
            val msg = activity.getString(strResId)!!
            showDialog1Btn(activity, msg, isTextAlignLeft = isTextAlignLeft) {}
        }

        fun showDialog2Btns(activity: FragmentActivity, msg: String, isTextAlignLeft: Boolean = false, forUpgradeInfoUI: Boolean = false, confirmLogic: () -> Unit = {}) {
            val newFragment = MyDialogFragment.newInstance(msg, confirmLogic, {})
            newFragment.isTextAlignLeft = isTextAlignLeft
            newFragment.show(getFragmentTransaction(activity), null)
            newFragment.forUpgradeInfoUI = forUpgradeInfoUI

            //            val adb = AlertDialog.Builder(this)
            //            val d = adb.setView(View(this)).create()
            //            // (That new View is just there to have something inside the dialog that can grow big enough to cover the whole screen.)
            //
            //            val lp = WindowManager.LayoutParams()
            //            lp.copyFrom(d.getWindow()!!.getAttributes())
            //            lp.width = WindowManager.LayoutParams.MATCH_PARENT
            //            lp.height = WindowManager.LayoutParams.MATCH_PARENT
            //            d.show()
            //            d.getWindow()!!.setAttributes(lp)
        }

    }
}