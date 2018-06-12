package org.trustnote.wallet.biz.msgs

import org.trustnote.wallet.R
import org.trustnote.wallet.widget.EmptyAdapter
import org.trustnote.wallet.widget.MyViewHolder

class ContactsAdapter(val myDataset: List<String>,
                      val layoutResId: Int = R.layout.item_addressbook,
                      val emptyLayoutResId: Int = R.layout.item_contacts_empty) :
        EmptyAdapter<String>(layoutResId, emptyLayoutResId, myDataset) {

    override fun handleItemView(holder: MyViewHolder, dataItem: String) {

    }

}