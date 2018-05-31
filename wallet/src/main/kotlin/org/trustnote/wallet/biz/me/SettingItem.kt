package org.trustnote.wallet.biz.me

import org.trustnote.wallet.BuildConfig
import org.trustnote.wallet.R
import org.trustnote.wallet.biz.MainActivity
import org.trustnote.wallet.debug.FragmentMeDebug
import org.trustnote.wallet.util.Utils

class SettingItem(
        var itemType: SettingItemType = SettingItemType.ITEM_SETTING,
        var icResId: Int = R.drawable.logo,
        var titleResId: Int = R.string.place_holder,
        var value: String = Utils.emptyString,
        var lambda: () -> Unit = Utils.emptyLambda) {

    companion object {

        fun getSettingMain(activity: MainActivity): Array<SettingItem> {
            return arrayOf(

                    SettingItem(titleResId = R.string.setting_ttt_pwd,
                            icResId = R.drawable.me_ttt_pwd,
                            lambda = {
                            }),

                    SettingItem(itemType = SettingItemType.ITEM_GAP),

                    SettingItem(titleResId = R.string.setting_wallet_tools,
                            icResId = R.drawable.me_wallet_tool,
                            lambda = {
                                openSubSetting(activity, getSettingWalletTools(activity), R.string.setting_wallet_tools)
                            }),

                    SettingItem(itemType = SettingItemType.ITEM_LINE),

                    SettingItem(titleResId = R.string.setting_system,
                            lambda = {
                                openSubSetting(activity, getSettingSystem(activity), R.string.setting_system)
                            }),

                    SettingItem(itemType = SettingItemType.ITEM_GAP),

                    SettingItem(titleResId = R.string.setting_about,
                            icResId = R.drawable.me_about,
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
                    SettingItem(itemType = SettingItemType.ITEM_SETTING_SUB,
                            titleResId = R.string.setting_about_version),
                    SettingItem(itemType = SettingItemType.ITEM_LINE_SUB),
                    SettingItem(itemType = SettingItemType.ITEM_SETTING_SUB,
                            titleResId = R.string.setting_about_hash, value = BuildConfig.GitHash),
                    SettingItem(itemType = SettingItemType.ITEM_LINE_SUB),
                    SettingItem(itemType = SettingItemType.ITEM_SETTING_SUB,
                            titleResId = R.string.setting_about_tou)
            )
        }

        fun getSettingSystem(activity: MainActivity): Array<SettingItem> {
            return arrayOf(
                    SettingItem(itemType = SettingItemType.ITEM_SETTING_SUB,
                            titleResId = R.string.setting_system_language),
                    SettingItem(itemType = SettingItemType.ITEM_LINE_SUB),
                    SettingItem(itemType = SettingItemType.ITEM_SETTING_SUB,
                            titleResId = R.string.setting_system_password)
            )
        }

        fun getSettingWalletTools(activity: MainActivity): Array<SettingItem> {
            return arrayOf(
                    SettingItem(itemType = SettingItemType.ITEM_SETTING_SUB,
                            titleResId = R.string.setting_wallet_tools_backupmem),
                    SettingItem(itemType = SettingItemType.ITEM_LINE_SUB),

                    SettingItem(itemType = SettingItemType.ITEM_SETTING_SUB,
                            titleResId = R.string.setting_wallet_tools_restoremem),
                    SettingItem(itemType = SettingItemType.ITEM_LINE_SUB),
                    SettingItem(itemType = SettingItemType.ITEM_SETTING_SUB,
                            titleResId = R.string.setting_wallet_tools_fullsync)
            )
        }

    }
}

enum class SettingItemType {
    ITEM_SETTING,
    ITEM_SETTING_SUB,
    ITEM_LINE,
    ITEM_LINE_SUB,
    ITEM_GAP,
    UNKNOWN
}
