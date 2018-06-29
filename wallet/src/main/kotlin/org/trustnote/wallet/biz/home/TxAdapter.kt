package org.trustnote.wallet.biz.home

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import org.trustnote.db.Tx
import org.trustnote.db.TxType
import org.trustnote.wallet.R
import org.trustnote.wallet.util.TTTUtils
import org.trustnote.wallet.widget.TMnAmount
import org.trustnote.wallet.util.Utils

class TxAdapter(private val myDataset: Array<Tx>) :
        RecyclerView.Adapter<TxAdapter.ViewHolder>() {

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder.
    // Each data item is just a string in this case that is shown in a TextView.
    class ViewHolder(val holderView: View) : RecyclerView.ViewHolder(holderView) {

        val ic: ImageView = holderView.findViewById(R.id.tx_ic)
        val addrress: TextView = holderView.findViewById(R.id.tx_address)
        val txTime: TextView = holderView.findViewById(R.id.tx_time)
        val amount: TMnAmount = holderView.findViewById(R.id.tx_amount)

        init {
            amount.setupStyle(true)
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): TxAdapter.ViewHolder {
        // create a new view
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_tx, parent, false)
        // set the view's size, margins, paddings and layout parameters
        return ViewHolder(itemView)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        val tx = myDataset[position]
        val statusDrawableResId = TTTUtils.getTxStatusDrawable(tx.txType, tx.confirmations > 0)
        holder.ic.setImageResource(statusDrawableResId)

        if (tx.txType == TxType.received && tx.arrPayerAddresses.isNotEmpty()) {
            holder.addrress.text = Utils.formatAddressWithEllipse(tx.arrPayerAddresses[0])
        } else {
            holder.addrress.text = Utils.formatAddressWithEllipse(tx.addressTo)
        }
        holder.txTime.text = Utils.formatTxTimestamp(tx.ts)
        holder.amount.setupStyle(tx.txType)
        holder.amount.setMnAmount(tx.amount)
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = myDataset.size
}