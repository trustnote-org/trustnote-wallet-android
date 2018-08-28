package org.trustnote.superwallet.biz.home

import android.widget.ImageView
import android.widget.TextView
import org.trustnote.db.Tx
import org.trustnote.db.TxType
import org.trustnote.superwallet.R
import org.trustnote.superwallet.util.TTTUtils
import org.trustnote.superwallet.util.Utils
import org.trustnote.superwallet.widget.EmptyAdapter
import org.trustnote.superwallet.widget.MyViewHolder
import org.trustnote.superwallet.widget.TMnAmount

class TxAdapter(private val myDataset: List<Tx>) :
        EmptyAdapter<Tx>(R.layout.item_tx, R.layout.item_tx_empty, myDataset) {

    override fun handleItemView(holder: MyViewHolder, tx: Tx) {

        val ic: ImageView = holder.itemView.findViewById(R.id.tx_ic)
        val addrress: TextView = holder.itemView.findViewById(R.id.tx_address)
        val txTime: TextView = holder.itemView.findViewById(R.id.tx_time)
        val amount: TMnAmount = holder.itemView.findViewById(R.id.tx_amount)

        val statusDrawableResId = TTTUtils.getTxStatusDrawable(tx.txType, tx.confirmations > 0)
        ic.setImageResource(statusDrawableResId)

        if (tx.txType == TxType.received && tx.arrPayerAddresses.isNotEmpty()) {
            addrress.text = Utils.formatAddressWithEllipse(tx.arrPayerAddresses[0])
        } else {
            addrress.text = Utils.formatAddressWithEllipse(tx.addressTo)
        }
        txTime.text = Utils.formatTxTimestamp(tx.ts)
        amount.setupStyle(true)
        amount.setupStyle(tx.txType)
        amount.setMnAmount(tx.amount)

    }


}