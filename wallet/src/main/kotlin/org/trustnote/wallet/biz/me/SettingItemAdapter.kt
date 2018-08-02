package org.trustnote.wallet.biz.me

import android.support.v7.widget.RecyclerView
import android.util.TypedValue
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
        var resId = 0
        when (SettingItemType.values()[viewType]) {
            SettingItemType.ITEM_SETTING -> {
                resId = R.layout.item_setting
            }

            SettingItemType.ITEM_SETTING_SUB -> {
                resId = R.layout.item_setting_sub
            }

            SettingItemType.ITEM_CHECKED -> {
                resId = R.layout.item_setting_checked
            }

            SettingItemType.ITEM_LINE -> {
                resId = R.layout.item_setting_line
            }

            SettingItemType.ITEM_LINE_SUB -> {
                resId = R.layout.item_setting_line_sub
            }

            SettingItemType.ITEM_FIELD -> {
                resId = R.layout.item_setting_field
            }

            SettingItemType.ITEM_GAP -> {
                resId = R.layout.item_setting_gap
            }
        }

        // create a new view
        val itemView = LayoutInflater.from(parent.context)
                .inflate(resId, parent, false)

        // set the view's size, margins, paddings and layout parameters
        return ViewHolder(itemView)
    }

    override fun getItemViewType(position: Int): Int {
        return myDataset[position].itemType.ordinal
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val settingData = myDataset[position]

        if (getItemViewType(position) == SettingItemType.ITEM_SETTING_SUB.ordinal ||
                getItemViewType(position) == SettingItemType.ITEM_FIELD.ordinal) {

            if (settingData.value.isEmpty()) {
                holder.holderView.findViewById<TextView>(R.id.setting_value).visibility = View.INVISIBLE
            } else {

                holder.holderView.findViewById<TextView>(R.id.setting_value).setText(settingData.value)

                if (settingData.alwaysShowArrow) {
                    holder.holderView.findViewById<View>(R.id.icon_setting_arrow).visibility = View.VISIBLE
                } else {
                    holder.holderView.findViewById<View>(R.id.icon_setting_arrow).visibility = View.INVISIBLE
                }

                if (settingData.isUseSmallFontForFieldValue) {
                    //Work around for wallet id font size.
                    holder.holderView.findViewById<TextView>(R.id.setting_value).setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
                }
            }

            if (settingData.showArrow) {
                holder.holderView.findViewById<View>(R.id.icon_setting_arrow).visibility = View.VISIBLE
            }

            holder.holderView.findViewById<TextView>(R.id.setting_title).setText(settingData.titleResId)
        }

        if (getItemViewType(position) == SettingItemType.ITEM_SETTING.ordinal) {
            holder.holderView.findViewById<TextView>(R.id.setting_title).setText(settingData.titleResId)
            holder.holderView.findViewById<ImageView>(R.id.setting_ic).setImageResource(settingData.icResId)
        }

        if (getItemViewType(position) == SettingItemType.ITEM_CHECKED.ordinal) {

            holder.holderView.findViewById<TextView>(R.id.setting_title).setText(settingData.titleResId)

            holder.holderView.findViewById<ImageView>(R.id.ic_language_checked).visibility = if (settingData.isChecked) View.VISIBLE else View.INVISIBLE

        }

        holder.holderView.setOnClickListener {
            myDataset[position].lambda.invoke()
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = myDataset.size

}