package org.trustnote.wallet.biz.me

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.TextView
import org.trustnote.wallet.R
import org.trustnote.wallet.biz.MainActivity
import org.trustnote.wallet.uiframework.FragmentBase

class FragmentMeSettingBase(val settingItems: Array<SettingItem>,
                            val titlResId: Int) : FragmentBase() {

    override fun getLayoutId(): Int {
        return R.layout.f_me_setting_base
    }

    override fun updateUI() {
        super.updateUI()

        val title = mRootView.findViewById<TextView>(R.id.setting_title)
        title.setText(titlResId)

        val recyclerView = mRootView.findViewById<RecyclerView>(R.id.setting_item_list)
        recyclerView.layoutManager = LinearLayoutManager(activity)

        recyclerView.adapter = SettingItemAdapter(settingItems)
    }

}

