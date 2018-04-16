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

class MeFragment : BaseFragment() {

    private var adapter: SettingsAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.me_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateUI()
    }

    override fun onResume() {
        super.onResume()
        updateUI()
    }

    private fun updateUI() {
        val recyclerView: RecyclerView = rootView
                .findViewById<RecyclerView>(R.id.me_list)
        //recyclerView.addItemDecoration(SimpleDividerItemDecoration(rootView.context))
        recyclerView.layoutManager = LinearLayoutManager(activity)

        adapter = SettingsAdapter(context, makeSettings())

        recyclerView.adapter = adapter

    }

    //TODO: save the adapter's state for restore.

}

