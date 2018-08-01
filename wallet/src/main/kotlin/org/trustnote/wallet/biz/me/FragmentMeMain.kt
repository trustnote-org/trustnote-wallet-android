package org.trustnote.wallet.biz.me

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.TextView
import org.trustnote.wallet.R
import org.trustnote.wallet.TApp
import org.trustnote.wallet.biz.ActivityMain

import org.trustnote.wallet.biz.wallet.FragmentWalletBaseForHomePage
import org.trustnote.wallet.biz.wallet.WalletManager
import org.trustnote.wallet.debug.FragmentMeDebug
import org.trustnote.wallet.uiframework.FragmentEditBase
import org.trustnote.wallet.util.Prefs
import org.trustnote.wallet.util.TTTUtils
import org.trustnote.wallet.util.Utils

class FragmentMeMain : FragmentWalletBaseForHomePage() {

    override fun getLayoutId(): Int {
        return R.layout.f_me_home
    }

    lateinit var meDeviceName: TextView
    lateinit var meIcon: TextView

    override fun initFragment(view: View) {

        isBottomLayerUI = true

        super.initFragment(view)

        findViewById<View>(R.id.me_header_edit).setOnClickListener { editDevicename() }

        meDeviceName = findViewById(R.id.me_device_name)
        meIcon = findViewById(R.id.me_icon)
    }

    override fun getTitle(): String {
        return activity.getString(R.string.menu_me)
    }

    override fun updateUI() {

        meDeviceName.text = (Utils.formatWalletIdEllipse(Prefs.readDeviceName()))

        meIcon.text = (TTTUtils.formatIconText(Prefs.readDeviceName()))

        val recyclerView = mRootView.findViewById<RecyclerView>(R.id.setting_item_list)
        recyclerView.layoutManager = LinearLayoutManager(activity)

        val fullMainSettings = mutableListOf<SettingItem>()

        var settingsMain = SettingItem.getSettingPageParameter(SettingItemsGroup.MAIN.ordinal,
                (activity as ActivityMain))
        fullMainSettings.addAll(settingsMain.first.toList())

        if (Utils.isTesterFeature()) {

            val debugSettings = SettingItem(titleResId = R.string.menu_debug,
                    lambda = {
                        val f = FragmentMeDebug()
                        (activity as ActivityMain).addL2Fragment(f)
                    })

            fullMainSettings.add(SettingItem(itemType = SettingItemType.ITEM_GAP))
            fullMainSettings.add(debugSettings)

        }

        recyclerView.adapter = SettingItemAdapter(fullMainSettings.toTypedArray())

    }

    override fun setupToolbar() {
        super.setupToolbar()
        mToolbar.setBackgroundResource(R.color.tx_list_bg)
    }

    override fun inflateMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.only_my_pair_id, menu)
    }

    private fun editDevicename() {
        val f = FragmentEditBase()
        f.buildPage(Prefs.readDeviceName(),
                activity.getString(R.string.mnemonic_devicename_err),
                {
                    it.length <= 20
                },

                {
                    Prefs.writeDeviceName(it)
                    WalletManager.mWalletEventCenter.onNext(true)
                }, activity.getString(R.string.title_edit_devicename), hint = activity.getString(R.string.mnemonic_devicename_hint))
        addL2Fragment(f)
    }

}

