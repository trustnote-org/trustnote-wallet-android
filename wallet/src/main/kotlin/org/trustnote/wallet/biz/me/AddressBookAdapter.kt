package org.trustnote.wallet.biz.me

import android.widget.TextView
import org.trustnote.db.entity.TransferAddresses
import org.trustnote.wallet.R
import org.trustnote.wallet.widget.EmptyAdapter
import org.trustnote.wallet.widget.MyViewHolder

class AddressBookAdapter(val myDataset: List<TransferAddresses>,
                         val layoutResId: Int = R.layout.item_addressbook,
                         val emptyLayoutResId: Int = R.layout.item_address_book_empty) :
        EmptyAdapter<TransferAddresses>(layoutResId, emptyLayoutResId, myDataset) {

    override fun handleItemView(holder: MyViewHolder, dataItem: TransferAddresses) {
        holder.itemView.findViewById<TextView>(R.id.address).setText(dataItem.address)
        holder.itemView.findViewById<TextView>(R.id.name).setText(dataItem.name)
    }

}