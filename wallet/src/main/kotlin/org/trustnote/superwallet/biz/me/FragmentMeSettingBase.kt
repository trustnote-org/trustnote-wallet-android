package org.trustnote.superwallet.biz.me

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import org.trustnote.superwallet.R
import org.trustnote.superwallet.biz.ActivityMain
import org.trustnote.superwallet.uiframework.FragmentBase
import org.trustnote.superwallet.util.AndroidUtils

class FragmentMeSettingBase : FragmentBase() {

    var showTitleInToolbar = false
    override fun getLayoutId(): Int {
        return R.layout.f_me_setting_base
    }

    override fun updateUI() {
        super.updateUI()

        val pageTypeIndex = arguments.getInt(AndroidUtils.KEY_SETTING_PAGE_TYPE)
        val pageTitleResId = arguments.getInt(AndroidUtils.KEY_SETTING_PAGE_TITLE)

        val title = mRootView.findViewById<TextView>(R.id.setting_title_top_outer)
        if (showTitleInToolbar) {
            title.visibility = View.GONE
            val toolbarTitle = mToolbar.findViewById<TextView>(R.id.toolbar_title)
            toolbarTitle.setText(pageTitleResId)
        } else {
            title.setText(pageTitleResId)
        }

        val recyclerView = mRootView.findViewById<RecyclerView>(R.id.setting_item_list)
        recyclerView.layoutManager = LinearLayoutManager(activity)

        recyclerView.adapter = SettingItemAdapter(SettingItem.getSettingPageParameter(pageTypeIndex, activity as ActivityMain).first)
    }

}

