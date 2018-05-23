package org.trustnote.wallet.biz

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

class FragmentDialogBase(private val layoutId: Int, private val confirmLogic: () -> Unit = {}) : DialogFragment() {

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater!!.inflate(layoutId, container, false)
        initFragment(view)
        return view
    }

    open fun initFragment(view: View) {
//        view.findViewById<Button>(R.id.next_step).setOnClickListener {
//            dismiss()
//            confirmLogic.invoke()
//        }
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