package org.trustnote.wallet.biz.msgs

import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import org.trustnote.wallet.R
import org.trustnote.wallet.TApp
import org.trustnote.wallet.biz.ActivityMain
import org.trustnote.wallet.uiframework.ActivityBase
import org.trustnote.wallet.uiframework.FragmentBaseForHomePage

class FragmentMsgsContactsList : FragmentBaseForHomePage() {

    val model: MessageModel = MessageModel.instance

    override fun getLayoutId(): Int {
        return R.layout.f_msg_home
    }

    lateinit var recyclerView: RecyclerView
    lateinit var mSwipeRefreshLayout: SwipeRefreshLayout
    lateinit var topShadow: View
    override fun initFragment(view: View) {

        isBottomLayerUI = true

        super.initFragment(view)

        recyclerView = mRootView.findViewById(R.id.list)
        recyclerView.layoutManager = LinearLayoutManager(activity)

        mSwipeRefreshLayout = mRootView.findViewById(R.id.swiperefresh)

        mSwipeRefreshLayout.setProgressViewOffset(true, -60, 40)

        mSwipeRefreshLayout.setOnRefreshListener {

            model.refreshHomeList()
            mSwipeRefreshLayout.isRefreshing = false
            updateUI()
        }

        model.refreshHomeList()

        icQuickAction = mToolbar.findViewById(R.id.ic_quick_action_container)

        if (icQuickAction != null) {
            icQuickAction!!.visibility = View.VISIBLE
            icQuickAction!!.setOnClickListener {
                (activity as ActivityMain).showPopupmenu{
                    startScan {
                        handleUnknownScanRes(it)
                    }
                }
            }
        }


        topShadow = findViewById(R.id.top_shadow)
        topShadow.alpha = 0f
        recyclerView.addOnScrollListener(object: RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val y = Math.abs(recyclerView!!.computeVerticalScrollOffset())
                if (y > 200) {
                    topShadow.alpha = 1f
                } else {
                    topShadow.alpha = (y/2f)/100f
                }
            }
        })



    }

    override fun onResume() {
        super.onResume()
        listener(model.mMessagesEventCenter)
    }

    override fun getTitle(): String {
        return activity.getString(R.string.messages_contacts_title)
    }

    override fun updateUI() {
        super.updateUI()

        val a = ContactsAdapter(model.latestHomeList)

        val shadow = findViewById<View>(R.id.top_shadow)
        if (model.latestHomeList.isEmpty()) {
            shadow.visibility = View.INVISIBLE
        } else {
            shadow.visibility = View.VISIBLE
        }

        a.itemClickListener = { _, item ->
            chatWithFriend(item.deviceAddress, activity as ActivityBase)
        }

        recyclerView.adapter = a

        mSwipeRefreshLayout.isRefreshing = model.isRefreshing()

    }

    override fun inflateMenu(menu: Menu, inflater: MenuInflater) {
        //inflater.inflate(R.menu.main_action, menu)
    }

}

