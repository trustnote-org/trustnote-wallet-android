package org.trustnote.wallet.biz.me

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import org.trustnote.db.entity.TransferAddresses
import org.trustnote.wallet.R
import org.trustnote.wallet.biz.wallet.Credential
import org.trustnote.wallet.biz.wallet.WalletManager
import org.trustnote.wallet.util.TTTUtils
import org.trustnote.wallet.widget.TMnAmount

class AddressBookAdapter(val myDataset: List<TransferAddresses>, val layoutResId: Int = R.layout.item_addressbook) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    class ViewHolder(val holderView: View) : RecyclerView.ViewHolder(holderView) {

        val name: TextView = holderView.findViewById(R.id.name)
        val address: TextView = holderView.findViewById(R.id.address)

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
            val addressBook = myDataset[position]
            holder.name.text = addressBook.name
            holder.address.text = addressBook.address
        }
        return
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = myDataset.size

}