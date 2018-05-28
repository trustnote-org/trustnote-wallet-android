package org.trustnote.wallet.biz.me

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import org.trustnote.wallet.R
import org.trustnote.wallet.biz.home.CredentialAdapter
import org.trustnote.wallet.widget.TMnAmount
import org.trustnote.wallet.biz.wallet.Credential

class SettingItemAdapter(private val myDataset: Array<SettingItem>) :
        RecyclerView.Adapter<SettingItemAdapter.ViewHolder>() {

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder.
    // Each data item is just a string in this case that is shown in a TextView.
    class ViewHolder(val holderView: View) : RecyclerView.ViewHolder(holderView) {

    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): SettingItemAdapter.ViewHolder {
        // create a new view
        val itemView = LayoutInflater.from(parent.context)
                .inflate(if (viewType == VIEW_TYPE_ITEM) R.layout.item_setting else R.layout.item_setting_sub, parent, false)

        // set the view's size, margins, paddings and layout parameters
        return ViewHolder(itemView)
    }

    override fun getItemViewType(position: Int): Int {
        return if (myDataset[position].isSubItem) VIEW_TYPE_SUBITEM else VIEW_TYPE_ITEM
    }


    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.holderView.findViewById<TextView>(R.id.setting_title).setText(myDataset[position].titleResId)

        if (getItemViewType(position) == VIEW_TYPE_SUBITEM) {
            holder.holderView.findViewById<TextView>(R.id.setting_value).setText(myDataset[position].value)
        }
        holder.holderView.setOnClickListener {
            myDataset[position].lambda.invoke()
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = myDataset.size

    companion object {
        val VIEW_TYPE_ITEM = 1
        val VIEW_TYPE_SUBITEM = 0
    }

}