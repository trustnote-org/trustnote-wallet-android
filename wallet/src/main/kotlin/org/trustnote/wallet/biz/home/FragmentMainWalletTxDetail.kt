package org.trustnote.wallet.biz.home

import android.view.View
import android.widget.TextView
import org.trustnote.db.TxType
import org.trustnote.wallet.R
import org.trustnote.wallet.biz.TTT
import org.trustnote.wallet.biz.wallet.FragmentWalletBase
import org.trustnote.wallet.util.AndroidUtils
import org.trustnote.wallet.util.Utils
import org.trustnote.wallet.widget.FieldTextView
import org.trustnote.wallet.widget.TMnAmount

class FragmentMainWalletTxDetail : FragmentWalletBase() {

    override fun getLayoutId(): Int {
        return R.layout.f_main_wallet_tx_detail
    }

    override fun setupToolbar() {
        super.setupToolbar()
        mToolbar.setBackgroundResource(R.color.tx_list_bg)

    }

    override fun updateUI() {
        super.updateUI()
        val txIndex = arguments.getInt(TTT.KEY_TX_INDEX, 0)


        val listData = credential.txDetails.filter {
            TxType.moved != it.txType
        }

        val tx = listData[txIndex]

        val amountView = mRootView.findViewById<TMnAmount>(R.id.wallet_summary)
        amountView.setupStyle(tx.txType, isFromDetail = true)
        amountView.setMnAmount(tx.amount)
        amountView.setupForTxListHeader()

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
                    "$it"
                })
                receiver.setField(R.string.tx_receiver, tx.myAddress)
            }
            else -> {
                amountTitle.setText(R.string.tx_detial_sender_title)
                sender.setField(R.string.tx_receiver, tx.addressTo)
                receiver.setField(R.string.tx_fee, Utils.getFeeAsString(tx.fee))
            }
        }

        fDate.setField(R.string.tx_date, Utils.formatTxTimestampInTxDetail(tx.ts))
        fUnit.setUnitField(tx.unit)
        status.setStatus(R.string.tx_status, tx.confirmations > 0, tx.txType)


        fUnit.setOnClickListener{
            AndroidUtils.openDefaultBrowser(activity, "${TTT.TTT_EXPLORER_URL}${tx.unit}")
        }

        fUnit.fieldUnitValue.setOnClickListener{
            AndroidUtils.openDefaultBrowser(activity, "${TTT.TTT_EXPLORER_URL}${tx.unit}")
        }

        //TODO: status.
    }

}

