package org.trustnote.wallet.biz.me

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import org.trustnote.wallet.R
import org.trustnote.wallet.widget.TMnAmount
import org.trustnote.wallet.biz.wallet.Credential

class SettingItemAdapter(private val myDataset: Array<SettingItem>) :
        RecyclerView.Adapter<SettingItemAdapter.ViewHolder>() {

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder.
    // Each data item is just a string in this case that is shown in a TextView.
    class ViewHolder(val holderView: View) : RecyclerView.ViewHolder(holderView) {

        val ic: ImageView = holderView.findViewById(R.id.setting_ic)
        val title: TextView = holderView.findViewById(R.id.setting_title)

    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): SettingItemAdapter.ViewHolder {
        // create a new view
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_setting, parent, false)

        // set the view's size, margins, paddings and layout parameters
        return ViewHolder(itemView)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        //holder.ic.setImageResource(R.drawable.credential_icon)
        //holder.title.text = myDataset[position].walletName
        holder.title.setText(myDataset[position].titleResId)
        holder.holderView.setOnClickListener {
            myDataset[position].lambda.invoke()
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = myDataset.size
}