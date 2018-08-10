package org.trustnote.wallet.biz.me

import android.os.Bundle
import org.trustnote.wallet.BuildConfig
import org.trustnote.wallet.R
import org.trustnote.wallet.TApp
import org.trustnote.wallet.biz.TTT
import org.trustnote.wallet.biz.ActivityMain
import org.trustnote.wallet.biz.init.CWFragmentDisclaimer
import org.trustnote.wallet.biz.wallet.Credential
import org.trustnote.wallet.biz.wallet.WalletManager
import org.trustnote.wallet.uiframework.ActivityBase
import org.trustnote.wallet.uiframework.FragmentEditBase
import org.trustnote.wallet.util.AndroidUtils
import org.trustnote.wallet.util.Prefs
import org.trustnote.wallet.util.Utils
import org.trustnote.wallet.widget.FragmentDialogInputPwd

class SettingItem(
        var itemType: SettingItemType = SettingItemType.ITEM_SETTING,
        var icResId: Int = R.drawable.logo,
        var titleResId: Int = R.string.place_holder,
        var value: String = Utils.emptyString,
        var isChecked: Boolean = false,
        var showArrow: Boolean = false,
        var isUseSmallFontForFieldValue:Boolean = false,
        var alwaysShowArrow:Boolean = false,
        var lambda: () -> Unit = Utils.emptyLambda) {

    companion object {

        private fun getSettingMain(activity: ActivityMain): Array<SettingItem> {
            return arrayOf(

                    SettingItem(titleResId = R.string.setting_wallet_tools,
                            icResId = R.drawable.me_wallet_tool,
                            lambda = {
                                openSubSetting(activity, SettingItemsGroup.WALLET_TOOL, R.string.setting_wallet_tools)
                            }),

                    SettingItem(itemType = SettingItemType.ITEM_LINE),

                    SettingItem(titleResId = R.string.me_wallet_manager,
                            icResId = R.drawable.ic_me_wallet_manager,
                            lambda = {
                                activity.addL2Fragment(FragmentMeWalletManager())
                            }),

                    SettingItem(itemType = SettingItemType.ITEM_LINE),

                    SettingItem(titleResId = R.string.setting_system,
                            icResId = R.drawable.ic_setting,
                            lambda = {
                                openSubSetting(activity, SettingItemsGroup.SYSTEM_SETTING, R.string.setting_system)
                            }),

                    SettingItem(itemType = SettingItemType.ITEM_GAP),

                    SettingItem(titleResId = R.string.setting_about,
                            icResId = R.drawable.me_about,
                            lambda = {
                                openSubSetting(activity, SettingItemsGroup.ABOUT, R.string.setting_about)
                            })
            )
        }

        fun openSubSetting(activity: ActivityMain, groupType: SettingItemsGroup, titleResId: Int, showTitleInToolbar: Boolean = false) {

            val f = FragmentMeSettingBase()
            f.showTitleInToolbar = showTitleInToolbar
            val arguments = Bundle()
            arguments.putInt(AndroidUtils.KEY_SETTING_PAGE_TYPE, groupType.ordinal)
            arguments.putInt(AndroidUtils.KEY_SETTING_PAGE_TITLE, titleResId)
            f.arguments = arguments
            activity.addL2Fragment(f)

        }

        private fun getSettingAbout(activity: ActivityMain): Array<SettingItem> {
            return arrayOf(
                    SettingItem(itemType = SettingItemType.ITEM_SETTING_SUB, alwaysShowArrow = true,
                            titleResId = R.string.setting_about_version, value = BuildConfig.VERSION_NAME) {
                        (activity as ActivityBase).checkUpgradeInfoFromPrefs()
                    },
                    SettingItem(itemType = SettingItemType.ITEM_LINE_SUB),
                    SettingItem(itemType = SettingItemType.ITEM_SETTING_SUB,
                            titleResId = R.string.setting_about_hash, value = BuildConfig.GitHash),
                    SettingItem(itemType = SettingItemType.ITEM_LINE_SUB),
                    SettingItem(itemType = SettingItemType.ITEM_SETTING_SUB,
                            titleResId = R.string.setting_about_tou) {
                        openTou(activity)
                    }
            )
        }

        private fun getSettingSystem(activity: ActivityMain): Array<SettingItem> {

            return arrayOf(

                    SettingItem(itemType = SettingItemType.ITEM_SETTING_SUB,
                            titleResId = R.string.setting_system_language, showArrow = true, value = getCurrentLanguageAsString()) {
                        selectLanguageUI(activity)
                    },

                    SettingItem(itemType = SettingItemType.ITEM_LINE_SUB),
                    SettingItem(itemType = SettingItemType.ITEM_SETTING_SUB,
                            titleResId = R.string.setting_system_password) {
                        openChangePwdUI(activity)
                    }
            )
        }

        private fun getSettingWalletTools(activity: ActivityMain): Array<SettingItem> {
            return arrayOf(
                    SettingItem(itemType = SettingItemType.ITEM_SETTING_SUB,
                            titleResId = R.string.setting_wallet_tools_backupmem) {
                        backupMnemonic(activity)
                    },
                    SettingItem(itemType = SettingItemType.ITEM_LINE_SUB),

                    SettingItem(itemType = SettingItemType.ITEM_SETTING_SUB,
                            titleResId = R.string.setting_wallet_tools_restoremem) {
                        restoreFromMnemonic(activity)
                    },
                    SettingItem(itemType = SettingItemType.ITEM_GAP),

                    SettingItem(itemType = SettingItemType.ITEM_SETTING_SUB,
                            titleResId = R.string.setting_wallet_tools_fullsync) {
                        fullSync(activity)
                    }
            )
        }

        fun getSettingForWalletDetail(credential: Credential, activity: ActivityMain): Array<SettingItem> {
            return arrayOf(
                    SettingItem(itemType = SettingItemType.ITEM_GAP),

                    SettingItem(itemType = SettingItemType.ITEM_FIELD,
                            titleResId = R.string.me_wallet_detail_name_title,
                            value = credential.walletName,
                            showArrow = true),

                    SettingItem(itemType = SettingItemType.ITEM_LINE_SUB),
                    SettingItem(itemType = SettingItemType.ITEM_FIELD,
                            titleResId = R.string.me_wallet_detail_id_title,
                            isUseSmallFontForFieldValue = true,
                            value = credential.walletId)

            )
        }

        private fun getSettingMoreForColdeWalletDetail(credential: Credential, activity: ActivityMain): Array<SettingItem> {
            return arrayOf(
                    SettingItem(itemType = SettingItemType.ITEM_LINE_SUB),

                    SettingItem(itemType = SettingItemType.ITEM_SETTING_SUB,
                            titleResId = R.string.me_wallet_detail_label_cold_code)
            )
        }

        private fun getSettingLanguages(activity: ActivityMain): Array<SettingItem> {
            return arrayOf(
                    SettingItem(itemType = SettingItemType.ITEM_GAP),

                    SettingItem(isChecked = !AndroidUtils.isZh(activity), itemType = SettingItemType.ITEM_CHECKED,
                            titleResId = R.string.language_en, lambda = {
                        AndroidUtils.setLanguage("en", activity)
                    }),

                    SettingItem(itemType = SettingItemType.ITEM_LINE_SUB),

                    SettingItem(isChecked = AndroidUtils.isZh(activity), itemType = SettingItemType.ITEM_CHECKED,
                            titleResId = R.string.language_zh, lambda = {
                        AndroidUtils.setLanguage("zh", activity)
                    })
            )
        }

        private fun backupMnemonic(activity: ActivityMain) {

            if (WalletManager.model.isMnemonicExist()) {
                activity.addL2Fragment(FragmentMeBackupMnemonic())
            } else {
                activity.addL2Fragment(FragmentMeBackupMnemonicRemoved())
            }
        }

        fun restoreFromMnemonic(activity: ActivityMain) {

            activity.addL2Fragment(FragmentMeWalletRestore())
        }

        fun fullSync(activity: ActivityMain) {
            activity.addL2Fragment(FragmentMeWalletFullSync())
        }

        fun selectLanguageUI(activity: ActivityMain) {
            openSubSetting(activity, SettingItemsGroup.LANGUAGE, R.string.setting_system_language, showTitleInToolbar = true)
        }

        fun openChangePwdUI(activity: ActivityMain) {
            activity.addL2Fragment(FragmentMeChangePwd())
        }

        private fun openTou(activity: ActivityMain) {
            val f = FragmentMeTou()
            f.fromInitActivity = false
            activity.addL2Fragment(f)
        }

        private fun getCurrentLanguageAsString(): String {

            return if ("zh" == TApp.resources.configuration.locale?.language) {
                TApp.resources.getString(R.string.language_zh)
            } else {
                TApp.resources.getString(R.string.language_en)
            }
        }

        fun getSettingPageParameter(typeIndex: Int, activity: ActivityMain): Pair<Array<SettingItem>, Int> {
            val settingItemsGroup = SettingItemsGroup.values()[typeIndex]

            return when (settingItemsGroup) {
                SettingItemsGroup.UNKNOWN -> Pair(emptyArray<SettingItem>(), 0)
                SettingItemsGroup.MAIN -> Pair(getSettingMain(activity), 0)
                SettingItemsGroup.ABOUT -> Pair(getSettingAbout(activity), 0)
                SettingItemsGroup.WALLET_TOOL -> Pair(getSettingWalletTools(activity), 0)
                SettingItemsGroup.SYSTEM_SETTING -> Pair(getSettingSystem(activity), 0)
                SettingItemsGroup.LANGUAGE -> Pair(getSettingLanguages(activity), 0)
                else -> Pair(emptyArray(), 0)
            }
        }
    }
}

enum class SettingItemType {
    ITEM_SETTING,
    ITEM_SETTING_SUB,
    ITEM_LINE,
    ITEM_LINE_SUB,
    ITEM_GAP,
    ITEM_CHECKED,
    ITEM_FIELD,
    UNKNOWN
}

enum class SettingItemsGroup {
    UNKNOWN,
    MAIN,
    ABOUT,
    WALLET_TOOL,
    WALLET_MANAGER,
    SYSTEM_SETTING,
    LANGUAGE,

}


