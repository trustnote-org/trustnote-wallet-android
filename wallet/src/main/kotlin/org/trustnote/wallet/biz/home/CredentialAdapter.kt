package org.trustnote.wallet.biz.home

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import org.trustnote.wallet.R
import org.trustnote.wallet.widget.TMnAmount
import org.trustnote.wallet.biz.wallet.Credential

class CredentialAdapter(private val myDataset: Array<Credential>) :
        RecyclerView.Adapter<CredentialAdapter.ViewHolder>() {

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
                                    viewType: Int): CredentialAdapter.ViewHolder {
        // create a new view
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_credential, parent, false)
        // set the view's size, margins, paddings and layout parameters
        return ViewHolder(itemView)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.ic.setImageResource(R.drawable.credential_icon)
        holder.title.text = myDataset[position].walletName
        holder.amount.setMnAmount(myDataset[position].balance)

        holder.observerTag.visibility = if (myDataset[position].isObserveOnly) View.VISIBLE else View.INVISIBLE
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = myDataset.size
}