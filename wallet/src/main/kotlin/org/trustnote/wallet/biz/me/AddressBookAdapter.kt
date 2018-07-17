package org.trustnote.wallet.biz.me

import android.view.View
import android.widget.TextView
import com.chauthai.swipereveallayout.SwipeRevealLayout
import org.trustnote.db.entity.TransferAddresses
import org.trustnote.wallet.R
import org.trustnote.wallet.widget.EmptyAdapter
import org.trustnote.wallet.widget.MyViewHolder
import com.chauthai.swipereveallayout.ViewBinderHelper
import org.trustnote.wallet.util.AndroidUtils

class AddressBookAdapter(val myDataset: List<TransferAddresses>,
                         val layoutResId: Int = R.layout.item_addressbook,
                         val emptyLayoutResId: Int = R.layout.item_address_book_empty) :
        EmptyAdapter<TransferAddresses>(layoutResId, emptyLayoutResId, myDataset) {

    private val viewBinderHelper = ViewBinderHelper()
    var removeLambda: (TransferAddresses) -> Unit = {}
    var editLambda: (TransferAddresses) -> Unit = {}

    init {
        viewBinderHelper.setOpenOnlyOne(true)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        //TODO: how about the first item.
        if (getItemViewType(position) == VIEW_TYPE_ITEM) {
            holder.itemView.findViewById<View>(R.id.item_data).setOnClickListener {
                itemClickListener.invoke(position, mDatas[position])
            }
            handleItemView(holder, mDatas[position])
        }
    }


    override fun handleItemView(holder: MyViewHolder, dataItem: TransferAddresses) {

        holder.itemView.findViewById<TextView>(R.id.address).setText(dataItem.address)
        holder.itemView.findViewById<TextView>(R.id.name).setText(dataItem.name)

        holder.itemView.findViewById<View>(R.id.ic_listitem_edit).setOnClickListener {
            editLambda.invoke(dataItem)
        }

        holder.itemView.findViewById<View>(R.id.ic_listitem_remove).setOnClickListener {
            removeLambda.invoke(dataItem)
        }

        viewBinderHelper.bind(holder.itemView.findViewById(R.id.swipe_reveal_layout), dataItem.address)

    }

}