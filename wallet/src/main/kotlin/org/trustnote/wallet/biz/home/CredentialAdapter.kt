package org.trustnote.wallet.biz.home

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import org.trustnote.wallet.R
import org.trustnote.wallet.biz.wallet.Credential
import org.trustnote.wallet.biz.wallet.WalletManager
import org.trustnote.wallet.widget.TMnAmount

class CredentialAdapter(val myDataset: Array<Credential>) :
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

        init {
            amount.setupStyle(true)
        }
    }


    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): RecyclerView.ViewHolder {
        if (viewType == VIEW_TYPE_HEADER) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.l_home_hearder, parent, false)
            return object : RecyclerView.ViewHolder(view) {
            }
        }

        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_credential, parent, false)
        // set the view's size, margins, paddings and layout parameters
        return ViewHolder(itemView)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {

        if (holder is ViewHolder && position > 0) {
            val credential = myDataset[position -1]
            holder.ic.setImageResource(R.drawable.credential_icon)
            holder.title.text = credential.walletName
            holder.amount.setMnAmount(credential.balance)

            holder.observerTag.visibility = if (credential.isObserveOnly) View.VISIBLE else View.INVISIBLE
            return
        } else {

            val totalBalanceView = holder!!.itemView.findViewById<TMnAmount>(R.id.wallet_summary)
            totalBalanceView.setMnAmount(WalletManager.model.mProfile.balance)

        }

    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = myDataset.size + 1

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) VIEW_TYPE_HEADER else VIEW_TYPE_ITEM
    }

    companion object {
        val VIEW_TYPE_ITEM = 1
        val VIEW_TYPE_HEADER = 0
    }

}