package org.trustnote.wallet.biz.me

import android.os.Bundle
import org.trustnote.wallet.R
import org.trustnote.wallet.biz.MainActivity

class SettingItem(var icResId: Int = R.drawable.logo,
                  var titleResId: Int = R.string.place_holder,
                  var lambda: () -> Unit = {}) {

    companion object {
        fun getSettingMain(activity: MainActivity): Array<SettingItem> {
            return arrayOf(
                    SettingItem(titleResId = R.string.setting_wallet_tools,
                            lambda = {
                                activity.openLevel2Fragment(Bundle(), FragmentMeWalletTools::class.java)
                            }),
                    SettingItem(titleResId = R.string.setting_system,
                            lambda = {
                                activity.openLevel2Fragment(Bundle(), FragmentMeSystem::class.java)

                            }),
                    SettingItem(titleResId = R.string.setting_about,
                            lambda = {
                                activity.openLevel2Fragment(Bundle(), FragmentMeAbout::class.java)
                            })
            )
        }
    }
}
