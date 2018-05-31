package org.trustnote.wallet.biz.me

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import org.trustnote.wallet.R
import org.trustnote.wallet.biz.MainActivity
import org.trustnote.wallet.debug.FragmentMeDebug
import org.trustnote.wallet.uiframework.FragmentBase
import org.trustnote.wallet.util.Utils

class FragmentMeMain : FragmentBase() {

    override fun getLayoutId(): Int {
        return R.layout.f_me_home
    }


    override fun updateUI() {

        val recyclerView = mRootView.findViewById<RecyclerView>(R.id.setting_item_list)
        recyclerView.layoutManager = LinearLayoutManager(activity)

        val fullMainSettings = mutableListOf<SettingItem>()
        var settingsMain = SettingItem.getSettingMain(activity as MainActivity)
        fullMainSettings.addAll(settingsMain.toList())

        if (Utils.isUseDebugOption()) {

            val debugSettings = SettingItem(titleResId = R.string.menu_debug,
                    lambda = {
                        val f = FragmentMeDebug()
                        (activity as MainActivity).openLevel2Fragment(f)
                    })

            fullMainSettings.add(SettingItem(itemType = SettingItemType.ITEM_GAP))
            fullMainSettings.add(debugSettings)

        }

        recyclerView.adapter = SettingItemAdapter(fullMainSettings.toTypedArray())

    }
}

