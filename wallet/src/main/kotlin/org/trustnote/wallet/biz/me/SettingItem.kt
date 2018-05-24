package org.trustnote.wallet.biz.me

import org.trustnote.wallet.R
import org.trustnote.wallet.biz.MainActivity
import org.trustnote.wallet.debug.FragmentMeDebug
import org.trustnote.wallet.util.Utils

class SettingItem(var icResId: Int = R.drawable.logo,
                  var titleResId: Int = R.string.place_holder,
                  var value: String = Utils.emptyString,
                  var lambda: () -> Unit = Utils.emptyLambda) {

    companion object {

        fun getSettingMain(activity: MainActivity): Array<SettingItem> {
            return arrayOf(

                    SettingItem(titleResId = R.string.setting_wallet_tools,
                            lambda = {
                                openSubSetting(activity, getSettingWalletTools(activity), R.string.setting_wallet_tools)
                            }),

                    SettingItem(titleResId = R.string.setting_system,
                            lambda = {
                                openSubSetting(activity, getSettingSystem(activity), R.string.setting_system)
                            }),

                    SettingItem(titleResId = R.string.setting_about,
                            lambda = {
                                openSubSetting(activity, getSettingAbout(activity), R.string.setting_about)
                            })
            )
        }

        private fun openSubSetting(activity: MainActivity, items: Array<SettingItem>, titielResId: Int) {
            val f = FragmentMeSettingBase(items, titielResId)
            activity.openLevel2Fragment(f)
        }

        fun getSettingAbout(activity: MainActivity): Array<SettingItem> {
            return arrayOf(
                    SettingItem(titleResId = R.string.setting_about_version),
                    SettingItem(titleResId = R.string.setting_about_hash),
                    SettingItem(titleResId = R.string.setting_about_tou)
            )
        }

        fun getSettingSystem(activity: MainActivity): Array<SettingItem> {
            return arrayOf(
                    SettingItem(titleResId = R.string.setting_system_language),
                    SettingItem(titleResId = R.string.setting_system_password)
            )
        }

        fun getSettingWalletTools(activity: MainActivity): Array<SettingItem> {
            return arrayOf(
                    SettingItem(titleResId = R.string.setting_wallet_tools_backupmem),
                    SettingItem(titleResId = R.string.setting_wallet_tools_restoremem),
                    SettingItem(titleResId = R.string.setting_wallet_tools_fullsync)
            )
        }

    }
}
