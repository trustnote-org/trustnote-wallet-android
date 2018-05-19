package org.trustnote.wallet.biz.home

import android.view.View
import android.widget.TextView
import org.trustnote.db.TxType
import org.trustnote.wallet.R
import org.trustnote.wallet.TTT
import org.trustnote.wallet.biz.wallet.WalletManager
import org.trustnote.wallet.util.Utils
import org.trustnote.wallet.widget.FieldTextView
import org.trustnote.wallet.widget.TMnAmount

class FragmentMainWalletTxDetail : FragmentMainBase() {

    override fun getLayoutId(): Int {
        return R.layout.f_main_wallet_tx_detail
    }

    override fun initFragment(view: View) {
        super.initFragment(view)

        val credentialIndex = arguments.getInt(TTT.KEY_CREDENTIAL_INDEX, 0)
        val txIndex = arguments.getInt(TTT.KEY_TX_INDEX, 0)

        val tx = WalletManager.model.mProfile.credentials[credentialIndex].txDetails[txIndex]

        val amountView = mRootView.findViewById<TMnAmount>(R.id.wallet_summary)
        amountView.setupStyle(tx.txType)
        amountView.setMnAmount(tx.amount)

        val amountTitle = mRootView.findViewById<TextView>(R.id.wallet_summary_title)

        val sender = mRootView.findViewById<FieldTextView>(R.id.tx_sender)
        val receiver = mRootView.findViewById<FieldTextView>(R.id.tx_receiver)
        val fDate = mRootView.findViewById<FieldTextView>(R.id.tx_date)
        val fUnit = mRootView.findViewById<FieldTextView>(R.id.tx_unit)
        val status = mRootView.findViewById<FieldTextView>(R.id.tx_status)

        when (tx.txType) {
            TxType.received -> {
                amountTitle.setText(R.string.tx_detial_receive_title)
                sender.setField(R.string.tx_sender, tx.arrPayerAddresses.joinToString{
                    "$it\n"
                })
                receiver.setField(R.string.tx_receiver, tx.addressTo)
            }
            else -> {
                amountTitle.setText(R.string.tx_detial_sender_title)
                sender.setField(R.string.tx_receiver, tx.myAddress)
                receiver.setField(R.string.tx_receiver, tx.fee.toString())
            }
        }

        fDate.setField(R.string.tx_date, Utils.formatTxTimestampInTxDetail(tx.ts))
        fUnit.setUnitField(tx.unit)
        status.setField(R.string.tx_status, "TODO")

        //TODO: status.

    }

}

