package org.trustnote.wallet.biz.me

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import org.trustnote.wallet.R
import org.trustnote.wallet.TApp
import org.trustnote.wallet.biz.ActivityMain
import org.trustnote.wallet.biz.wallet.FragmentWalletBase
import org.trustnote.wallet.debug.FragmentMeDebug
import org.trustnote.wallet.util.Utils

class FragmentMeMain : FragmentWalletBase() {

    override fun getLayoutId(): Int {
        return R.layout.f_me_home
    }

    lateinit var btnWalletManager: View
    lateinit var btnWalletTx: View

    override fun initFragment(view: View) {
        isBottomLayerUI = true

        super.initFragment(view)

        btnWalletManager = findViewById(R.id.me_wallet_manager)
        btnWalletTx = findViewById(R.id.me_wallet_tx)

        btnWalletManager.setOnClickListener {
            getMyActivity().openLevel2Fragment(FragmentMeWalletManager())
        }

    }


    override fun getTitle(): String {
        return TApp.getString(R.string.menu_me)
    }

    override fun updateUI() {

        val recyclerView = mRootView.findViewById<RecyclerView>(R.id.setting_item_list)
        recyclerView.layoutManager = LinearLayoutManager(activity)

        val fullMainSettings = mutableListOf<SettingItem>()
        var settingsMain = SettingItem.getSettingMain(activity as ActivityMain)
        fullMainSettings.addAll(settingsMain.toList())

        if (Utils.isUseDebugOption()) {

            val debugSettings = SettingItem(titleResId = R.string.menu_debug,
                    lambda = {
                        val f = FragmentMeDebug()
                        (activity as ActivityMain).openLevel2Fragment(f)
                    })

            fullMainSettings.add(SettingItem(itemType = SettingItemType.ITEM_GAP))
            fullMainSettings.add(debugSettings)

        }

        recyclerView.adapter = SettingItemAdapter(fullMainSettings.toTypedArray())

    }
}

