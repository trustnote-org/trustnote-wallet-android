package org.trustnote.superwallet.debug

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import org.trustnote.superwallet.R
import org.trustnote.superwallet.uiframework.FragmentBase
import org.trustnote.superwallet.debug.SettingsDataFactory.makeSettings

class FragmentMeDebug : FragmentBase() {

    override fun getLayoutId(): Int {
        return R.layout.f_me_debug
    }

    private var adapter: SettingsAdapter? = null


    override fun updateUI() {
        val recyclerView: RecyclerView = mRootView
                .findViewById<RecyclerView>(R.id.me_list)
        //recyclerView.addItemDecoration(SimpleDividerItemDecoration(mRootView.context))
        recyclerView.layoutManager = LinearLayoutManager(activity)

        adapter = SettingsAdapter(context, makeSettings())

        recyclerView.adapter = adapter

    }

    //TODO: save the adapter's state for restore.

}

