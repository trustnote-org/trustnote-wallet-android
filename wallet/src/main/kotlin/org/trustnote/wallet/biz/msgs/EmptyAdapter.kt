package org.trustnote.wallet.biz.msgs

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import org.trustnote.wallet.R
import org.trustnote.wallet.widget.TMnAmount

class EmptyAdapter(val emptyLayoutId: Int,
                   private val mDatas: List<String>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mListener: OnItemSelectListener? = null

    class ViewHolder(val holderView: View) : RecyclerView.ViewHolder(holderView) {


        val ic: ImageView = holderView.findViewById(R.id.credential_ic)
        val title: TextView = holderView.findViewById(R.id.credential_title)
        val amount: TMnAmount = holderView.findViewById(R.id.credential_amount)
        val observerTag: TextView = holderView.findViewById(R.id.credential_observer_tag)

        init {
            amount.setupStyle(true)
        }
    }

    internal class OnItemSelectListener {
        fun onItemSelected(v: View, position: Int) {

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        //在这里根据不同的viewType进行引入不同的布局
        if (viewType == VIEW_TYPE_EMPTY) {
            val view = LayoutInflater.from(parent.context).inflate(emptyLayoutId, parent, false)
            return object : RecyclerView.ViewHolder(view) {

            }
        }
        //其他的引入正常的
        val view = LayoutInflater.from(parent.context).inflate(emptyLayoutId, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder) {
            val viewHolder = holder as ViewHolder
            viewHolder.itemView.setOnClickListener {
                if (mListener != null) {
                    mListener!!.onItemSelected(it, position)
                }
            }
        }
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