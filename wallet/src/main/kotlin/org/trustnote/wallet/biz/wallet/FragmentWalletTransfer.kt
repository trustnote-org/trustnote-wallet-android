package org.trustnote.wallet.biz.wallet

import android.view.View
import android.widget.EditText
import android.widget.TextView
import org.trustnote.wallet.R
import org.trustnote.wallet.util.TTTUtils
import org.trustnote.wallet.widget.TMnAmount

class FragmentWalletTransfer : FragmentWalletBase() {

    override fun getLayoutId(): Int {
        return R.layout.f_wallet_transfer
    }

    lateinit var title: TextView
    lateinit var balance: TMnAmount
    lateinit var receiverText: EditText
    lateinit var scanIcon: View
    lateinit var receiveErr: TextView

    lateinit var amountText: EditText
    lateinit var amountErr: TextView
    lateinit var transferAll: TextView

    override fun initFragment(view: View) {
        super.initFragment(view)

        title = findViewById(R.id.transfer_title)

        balance = findViewById(R.id.transfer_balance)
        receiverText = findViewById(R.id.transfer_receiver_hint)
        scanIcon = findViewById(R.id.transfer_receiver_scan)
        receiveErr = findViewById(R.id.transfer_receiver_err)

        amountText = findViewById(R.id.transfer_amount)
        amountErr = findViewById(R.id.transfer_receiver_err_overflow)

        transferAll = findViewById(R.id.transfer_transfer_all)

        transferAll.setOnClickListener { setTransferAmount(credential.balance) }

        setupScan(scanIcon) { handleScanRes(it) }

    }

    private fun setTransferAmount(transferAmount: Long) {
        //TODO: how about fee?
        if (transferAmount <= credential.balance) {
            //TODO:
            amountText.setText(transferAmount.toString())
            amountErr.visibility = View.INVISIBLE
        } else {
            amountErr.visibility = View.VISIBLE
        }

    }

    fun handleScanRes(res: String) {
        val address = TTTUtils.parseAddressFromQRCode(res)
        if (address.isEmpty()) {
            receiveErr.visibility = View.VISIBLE
        } else {
            receiverText.setText(address)
            receiveErr.visibility = View.INVISIBLE
        }
    }

    override fun updateUI() {
        balance.setMnAmount(credential.balance)
        title.text = credential.walletName
    }

}

