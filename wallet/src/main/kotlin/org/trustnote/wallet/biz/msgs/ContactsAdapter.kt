package org.trustnote.wallet.biz.msgs

import android.view.View
import android.widget.TextView
import org.trustnote.db.entity.CorrespondentDevices
import org.trustnote.wallet.R
import org.trustnote.wallet.util.Utils
import org.trustnote.wallet.widget.EmptyAdapter
import org.trustnote.wallet.widget.MyViewHolder

class ContactsAdapter(myDataset: List<CorrespondentDevices>,
                      layoutResId: Int = R.layout.item_msg_home,
                      emptyLayoutResId: Int = R.layout.item_contacts_empty) :
        EmptyAdapter<CorrespondentDevices>(layoutResId, emptyLayoutResId, myDataset) {

    override fun handleItemView(holder: MyViewHolder, dataItem: CorrespondentDevices) {

        holder.itemView.findViewById<TextView>(R.id.msg_icon).setText(dataItem.name.substring(0, 1))

        holder.itemView.findViewById<TextView>(R.id.msg_contact_name).setText(dataItem.name)
        holder.itemView.findViewById<TextView>(R.id.msg_last_msg).setText(dataItem.lastMessage)
        val timeAsText = Utils.getTimeAgoForCn(dataItem.updateDate)
        holder.itemView.findViewById<TextView>(R.id.msg_time).setText(timeAsText)

        val unreadCounter = holder.itemView.findViewById<TextView>(R.id.msg_unread_counter)

        if (dataItem.unReadMsgsCounter > 10) {
            unreadCounter.visibility = View.VISIBLE
            unreadCounter.setText("•••")
        } else if (dataItem.unReadMsgsCounter > 0){
            unreadCounter.visibility = View.VISIBLE
            unreadCounter.setText(dataItem.unReadMsgsCounter.toString())
        } else {
            unreadCounter.visibility = View.INVISIBLE
        }

    }

}