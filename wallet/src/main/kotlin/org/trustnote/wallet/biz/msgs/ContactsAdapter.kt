package org.trustnote.wallet.biz.msgs

import android.view.View
import android.widget.TextView
import org.trustnote.db.entity.ChatMessages
import org.trustnote.wallet.R
import org.trustnote.wallet.util.Utils
import org.trustnote.wallet.widget.EmptyAdapter
import org.trustnote.wallet.widget.MyViewHolder

class ContactsAdapter(val myDataset: List<ChatMessages>,
                      val layoutResId: Int = R.layout.item_msg_home,
                      val emptyLayoutResId: Int = R.layout.item_contacts_empty) :
        EmptyAdapter<ChatMessages>(layoutResId, emptyLayoutResId, myDataset) {

    override fun handleItemView(holder: MyViewHolder, dataItem: ChatMessages) {

        holder.itemView.findViewById<TextView>(R.id.msg_icon).setText(dataItem.correspondentName.substring(0, 1))

        holder.itemView.findViewById<TextView>(R.id.msg_contact_name).setText(dataItem.correspondentName)
        holder.itemView.findViewById<TextView>(R.id.msg_last_msg).setText(dataItem.message)
        val timeAsText = Utils.getTimeAgoForCn(dataItem.creationDate)
        holder.itemView.findViewById<TextView>(R.id.msg_time).setText(timeAsText)

        val unreadCounter = holder.itemView.findViewById<TextView>(R.id.msg_unread_counter)
        if (dataItem.unReadMsgsNumber > 10) {
            unreadCounter.visibility = View.VISIBLE
            unreadCounter.setText("…")
        } else if (dataItem.unReadMsgsNumber > 0){
            unreadCounter.visibility = View.VISIBLE
            unreadCounter.setText(dataItem.unReadMsgsNumber.toString())
        } else {
            unreadCounter.visibility = View.INVISIBLE
        }

    }

}