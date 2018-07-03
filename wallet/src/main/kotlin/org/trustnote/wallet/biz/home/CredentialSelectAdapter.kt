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
import org.trustnote.wallet.util.TTTUtils
import org.trustnote.wallet.widget.TMnAmount

class CredentialSelectAdapter(val myDataset: List<Credential>, val layoutResId: Int = R.layout.item_credential_select) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    class ViewHolder(val holderView: View) : RecyclerView.ViewHolder(holderView) {

        val title: TextView = holderView.findViewById(R.id.credential_title)
        val walletId: TextView = holderView.findViewById(R.id.credential_address)

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
            holder.title.text = credential.walletName
            holder.walletId.text = TTTUtils.formatWalletId(credential.walletId)
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