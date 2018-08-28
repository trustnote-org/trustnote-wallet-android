package org.trustnote.superwallet.biz.home

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import org.trustnote.superwallet.R
import org.trustnote.superwallet.biz.wallet.Credential
import org.trustnote.superwallet.util.TTTUtils
import org.trustnote.superwallet.widget.TMnAmount

class CredentialAdapter(val myDataset: List<Credential>, val layoutResId: Int = R.layout.item_credential) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder.
    // Each data item is just a string in this case that is shown in a TextView.
    class ViewHolder(val holderView: View) : RecyclerView.ViewHolder(holderView) {

        val ic: ImageView = holderView.findViewById(R.id.credential_ic)
        val title: TextView = holderView.findViewById(R.id.credential_title)
        val amount: TMnAmount = holderView.findViewById(R.id.credential_amount)
        val observerTag: TextView = holderView.findViewById(R.id.credential_observer_tag)
        val walletid: TextView? = holderView.findViewById(R.id.credential_walletid)

        init {
            amount.setupStyle(true)
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): RecyclerView.ViewHolder {

        val itemView = LayoutInflater.from(parent.context)
                .inflate(layoutResId, parent, false)
        // set the view's size, margins, paddings and layout parameters
        return ViewHolder(itemView)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {

        if (holder is ViewHolder) {
            val credential = myDataset[position]
            holder.ic.setImageResource(R.drawable.credential_icon)
            holder.title.text = credential.walletName
            holder.amount.setMnAmount(credential.balance)

            holder.observerTag.visibility = if (credential.isObserveOnly) View.VISIBLE else View.INVISIBLE

            holder.walletid?.text = TTTUtils.formatWalletId(credential.walletId)
        }
        return
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = myDataset.size

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) VIEW_TYPE_ITEM else VIEW_TYPE_ITEM
    }

    companion object {
        val VIEW_TYPE_ITEM = 1
    }

}