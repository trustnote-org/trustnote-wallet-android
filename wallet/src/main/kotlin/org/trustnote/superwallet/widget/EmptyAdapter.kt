package org.trustnote.superwallet.widget

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

open class EmptyAdapter<T : Any>(val layoutId: Int, val emptyLayoutId: Int,
                                    val mDatas: List<T>) : RecyclerView.Adapter<MyViewHolder>() {

    var itemClickListener: (index: Int, item: T) -> Unit = { _, _ -> }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val resId = if (viewType == VIEW_TYPE_EMPTY) emptyLayoutId else layoutId
        val view = LayoutInflater.from(parent.context).inflate(resId, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        //TODO: how about the first item.
        if (getItemViewType(position) == VIEW_TYPE_ITEM) {
            holder.itemView.setOnClickListener {
                itemClickListener.invoke(position, mDatas[position])
            }
            handleItemView(holder, mDatas[position])
        }
    }

    open fun handleItemView(holder: MyViewHolder, dataItem: T) {

    }

    override fun getItemCount(): Int {
        return if (mDatas.isEmpty()) {
            1
        } else mDatas.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (mDatas.isEmpty()) {
            VIEW_TYPE_EMPTY
        } else VIEW_TYPE_ITEM
    }

    companion object {
        val VIEW_TYPE_ITEM = 1
        val VIEW_TYPE_EMPTY = 0
    }
}

class MyViewHolder(val holderView: View) : RecyclerView.ViewHolder(holderView) {
    init {
    }
}
