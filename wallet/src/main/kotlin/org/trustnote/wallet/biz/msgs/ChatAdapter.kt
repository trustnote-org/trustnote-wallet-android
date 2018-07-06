package org.trustnote.wallet.biz.msgs

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import org.trustnote.db.entity.ChatMessages
import org.trustnote.wallet.R
import org.trustnote.wallet.util.Utils

open class ChatAdapter(private val mDatas: List<ChatMessages>) : RecyclerView.Adapter<MyViewHolder>() {

    var itemClickListener: (item: ChatMessages) -> Unit = {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val resId = when (viewType) {

            FromMe -> R.layout.item_chat_msg_from_me
            FromFriend -> R.layout.item_chat_msg_from_friend
            FromSystem -> R.layout.item_chat_msg_from_system

            else -> R.layout.item_chat_msg_from_me
        }

        val view = LayoutInflater.from(parent.context).inflate(resId, parent, false)

        return MyViewHolder(viewType, view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.itemView.setOnClickListener {
            itemClickListener.invoke(mDatas[position])
        }
        handleItemView(holder, mDatas[position])

    }

    fun handleItemView(holder: MyViewHolder, dataItem: ChatMessages) {
        holder.msg.setText(dataItem.message)
        holder.dateOrTime.visibility = View.VISIBLE
        holder.dateOrTime.setText(Utils.getTimeAgoForCn(dataItem.creationDate))
        if (dataItem.showTimeOrDate) {
            holder.dateOrTime.visibility = View.VISIBLE
            holder.dateOrTime.setText(Utils.getTimeAgoForCn(dataItem.creationDate))
        } else {
            holder.dateOrTime.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return mDatas.size
    }

    override fun getItemViewType(position: Int): Int {
        val chatMessages = mDatas[position]
        return when {
            TMessageType.system.name == chatMessages.type -> FromSystem
            chatMessages.isIncoming == 0 -> FromMe
            else -> FromFriend
        }
    }

    companion object {
        val FromMe = 0
        val FromFriend = 1
        val FromSystem = 2
    }

}

class MyViewHolder(val viewType: Int, val holderView: View) : RecyclerView.ViewHolder(holderView) {

    val msg: TextView = holderView.findViewById(R.id.msg)
    val dateOrTime: TextView = holderView.findViewById(R.id.time)

}
