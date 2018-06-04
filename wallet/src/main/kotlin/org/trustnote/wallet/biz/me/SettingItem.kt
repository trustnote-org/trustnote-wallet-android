package org.trustnote.wallet.biz.me

import android.os.Bundle
import org.trustnote.wallet.BuildConfig
import org.trustnote.wallet.R
import org.trustnote.wallet.TApp
import org.trustnote.wallet.biz.TTT
import org.trustnote.wallet.biz.ActivityMain
import org.trustnote.wallet.biz.init.CWFragmentRestore
import org.trustnote.wallet.biz.wallet.Credential
import org.trustnote.wallet.biz.wallet.WalletManager
import org.trustnote.wallet.util.AndroidUtils
import org.trustnote.wallet.util.Utils
import org.trustnote.wallet.widget.InputPwdDialogFragment

class SettingItem(
        var itemType: SettingItemType = SettingItemType.ITEM_SETTING,
        var icResId: Int = R.drawable.logo,
        var titleResId: Int = R.string.place_holder,
        var value: String = Utils.emptyString,
        var lambda: () -> Unit = Utils.emptyLambda) {

    companion object {

        fun getSettingMain(activity: ActivityMain): Array<SettingItem> {
            return arrayOf(

                    SettingItem(titleResId = R.string.setting_ttt_pwd,
                            icResId = R.drawable.me_ttt_pwd,
                            lambda = {
                                AndroidUtils.todo()
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

        private fun openSubSetting(activity: ActivityMain, items: Array<SettingItem>, titielResId: Int) {
            val f = FragmentMeSettingBase(items, titielResId)
            activity.openLevel2Fragment(f)
        }

        fun getSettingAbout(activity: ActivityMain): Array<SettingItem> {
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

        fun getSettingSystem(activity: ActivityMain): Array<SettingItem> {
            return arrayOf(
                    SettingItem(itemType = SettingItemType.ITEM_SETTING_SUB,
                            titleResId = R.string.setting_system_language),
                    SettingItem(itemType = SettingItemType.ITEM_LINE_SUB),
                    SettingItem(itemType = SettingItemType.ITEM_SETTING_SUB,
                            titleResId = R.string.setting_system_password)
            )
        }

        fun getSettingWalletTools(activity: ActivityMain): Array<SettingItem> {
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
                    SettingItem(itemType = SettingItemType.ITEM_LINE_SUB),

                    SettingItem(itemType = SettingItemType.ITEM_SETTING_SUB,
                            titleResId = R.string.setting_wallet_tools_fullsync) {
                        fullSync(activity)
                    }
            )
        }

        fun getSettingForWalletDetail(credential: Credential, activity: ActivityMain): Array<SettingItem> {
            return arrayOf(
                    SettingItem(itemType = SettingItemType.ITEM_GAP),

                    SettingItem(itemType = SettingItemType.ITEM_SETTING_SUB,
                            titleResId = R.string.me_wallet_detail_name_title,
                            value = credential.walletName),

                    SettingItem(itemType = SettingItemType.ITEM_LINE_SUB),
                    SettingItem(itemType = SettingItemType.ITEM_SETTING_SUB,
                            titleResId = R.string.me_wallet_detail_id_title,
                            value = credential.walletId)

            )
        }

        fun getSettingMoreForColdeWalletDetail(credential: Credential, activity: ActivityMain): Array<SettingItem> {
            return arrayOf(
                    SettingItem(itemType = SettingItemType.ITEM_LINE_SUB),

                    SettingItem(itemType = SettingItemType.ITEM_SETTING_SUB,
                            titleResId = R.string.me_wallet_detail_label_cold_code, lambda = {

                        InputPwdDialogFragment.showMe(activity, {

                            TApp.userAlreadyInputPwd = true
                            val bundle = Bundle()
                            bundle.putString(TTT.KEY_WALLET_ID, credential.walletId)
                            activity.openLevel2Fragment(bundle, FragmentMeWalletColdCode::class.java)

                        })

                    })
            )
        }

        fun backupMnemonic(activity: ActivityMain) {

            if (WalletManager.model.isMnemonicExist()) {
                activity.openLevel2Fragment(FragmentMeBackupMnemonic())
            } else {
                activity.openLevel2Fragment(FragmentMeBackupMnemonicRemoved())
            }
        }

        fun restoreFromMnemonic(activity: ActivityMain) {

            activity.openLevel2Fragment(FragmentMeWalletRestore())
        }

        fun fullSync(activity: ActivityMain) {
            activity.openLevel2Fragment(FragmentMeWalletFullSync())
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
