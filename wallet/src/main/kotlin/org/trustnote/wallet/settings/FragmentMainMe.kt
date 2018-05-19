package org.trustnote.wallet.settings

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.trustnote.wallet.R
import org.trustnote.wallet.uiframework.BaseFragment
import org.trustnote.wallet.settings.SettingsDataFactory.makeSettings

class FragmentMainMe : BaseFragment() {

    override fun getLayoutId(): Int {
        return R.layout.f_main_me
    }

    private var adapter: SettingsAdapter? = null

    override fun onResume() {
        super.onResume()
        updateUI()
    }

    private fun updateUI() {
        val recyclerView: RecyclerView = mRootView
                .findViewById<RecyclerView>(R.id.me_list)
        //recyclerView.addItemDecoration(SimpleDividerItemDecoration(mRootView.context))
        recyclerView.layoutManager = LinearLayoutManager(activity)

        adapter = SettingsAdapter(context, makeSettings())

        recyclerView.adapter = adapter

    }

    //TODO: save the adapter's state for restore.

}

