package org.trustnote.wallet.biz.wallet

import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import org.trustnote.wallet.R
import org.trustnote.wallet.biz.FragmentPageBase
import org.trustnote.wallet.biz.MainActivity
import org.trustnote.wallet.widget.TMnAmount

class FragmentWalletReceive : FragmentPageBase() {

    lateinit var addressText: TextView
    lateinit var addressQR: ImageView
    lateinit var receiveAmount: TMnAmount
    lateinit var clearAmount: TextView
    lateinit var setupAmount: TextView
    lateinit var copyBtn: Button

    override fun getLayoutId(): Int {
        return R.layout.l_dialog_wallet_receive
    }

    override fun initFragment(view: View) {
        super.initFragment(view)
        addressText = mRootView.findViewById(R.id.receive_address_text)
        addressQR = mRootView.findViewById(R.id.qr_code_imageview)
        receiveAmount = mRootView.findViewById(R.id.receive_amount)
        clearAmount = mRootView.findViewById(R.id.receive_clear_amount)
        setupAmount = mRootView.findViewById(R.id.receive_setup_amount)
        copyBtn = mRootView.findViewById(R.id.receive_btn_copy)

        setupAmount.setOnClickListener {
            (activity as MainActivity).openLevel2Fragment(FragmentWalletReceiveSetAmount())
        }

        clearAmount.setOnClickListener {
            getMyActivity().receiveAmount = 0L
            updateUI()
        }

    }

    override fun updateUI() {
        if (getMyActivity().receiveAmount == 0L) {
            receiveAmount.visibility = View.GONE
            clearAmount.visibility = View.GONE
            setupAmount.visibility = View.VISIBLE
        } else {
            receiveAmount.visibility = View.VISIBLE
            clearAmount.visibility = View.VISIBLE
            setupAmount.visibility = View.GONE
            receiveAmount.setMnAmount(getMyActivity().receiveAmount)
        }

    }

}

