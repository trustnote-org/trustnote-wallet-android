package org.trustnote.wallet.biz.me

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import org.trustnote.wallet.R
import org.trustnote.wallet.uiframework.FragmentBase

class FragmentMeMain : FragmentBase() {

    override fun getLayoutId(): Int {
        return R.layout.f_me_home
    }



    override fun updateUI() {

        val recyclerView = mRootView.findViewById<RecyclerView>(R.id.setting_item_list)
        recyclerView.layoutManager = LinearLayoutManager(activity)


        val listOfSettings = mutableListOf<SettingItem>()
        listOfSettings.add(SettingItem())
        listOfSettings.add(SettingItem())
        listOfSettings.add(SettingItem())
        listOfSettings.add(SettingItem())

        val a = SettingItemAdapter(listOfSettings.toTypedArray())

        recyclerView.adapter = a

    }
}

