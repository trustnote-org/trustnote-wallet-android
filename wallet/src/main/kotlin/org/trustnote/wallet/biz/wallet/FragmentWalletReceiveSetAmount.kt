package org.trustnote.wallet.biz.wallet

import android.view.View
import android.widget.Button
import android.widget.EditText
import org.trustnote.wallet.R
import org.trustnote.wallet.biz.FragmentPageBase
import org.trustnote.wallet.util.Utils

class FragmentWalletReceiveSetAmount : FragmentPageBase() {

    lateinit var inputAmount: EditText
    lateinit var btnConfirm: Button

    override fun getLayoutId(): Int {
        return R.layout.l_dialog_wallet_receive_set_amount
    }

    override fun initFragment(view: View) {
        super.initFragment(view)
        inputAmount = mRootView.findViewById(R.id.receive_amount_input)
        btnConfirm = mRootView.findViewById(R.id.receive_set_amount_btn)

        btnConfirm.setOnClickListener {
            getMyActivity().receiveAmount = Utils.mnToNotes(inputAmount.text.toString())
            getMyActivity().onBackPressed()
        }
    }


}

