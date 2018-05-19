package org.trustnote.wallet.settings

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import org.trustnote.wallet.R
import org.trustnote.wallet.uiframework.FragmentBase
import org.trustnote.wallet.settings.SettingsDataFactory.makeSettings

class FragmentMainMe : FragmentBase() {

    override fun getLayoutId(): Int {
        return R.layout.f_main_me
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

