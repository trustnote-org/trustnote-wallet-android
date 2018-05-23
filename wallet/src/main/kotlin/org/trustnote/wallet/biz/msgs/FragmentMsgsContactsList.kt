package org.trustnote.wallet.biz.msgs

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import org.trustnote.wallet.R

class FragmentMsgsContactsList : FragmentMsgsBase() {

    override fun getLayoutId(): Int {
        return R.layout.f_msg_home
    }

    //TODO: listen the wallet update event.

    override fun initFragment(view: View) {


        super.initFragment(view)

        val recyclerView = mRootView.findViewById<RecyclerView>(R.id.msg_contacts_list)
        recyclerView.layoutManager = LinearLayoutManager(activity)

        val a = EmptyAdapter(R.layout.l_contacts_empty, listOf())

        recyclerView.adapter = a

    }


}

