package org.trustnote.wallet.biz.wallet

import org.trustnote.wallet.R


data class PageSetting(val layoutId: Int, val showStatusBar: Boolean = true, val showBackArrow: Boolean = true) {
}

val mPageConfiguration: MutableMap<Int, PageSetting> = mutableMapOf()

fun setupUISettings() {

    addConfig(R.layout.f_new_seed_disclaimer, false, false)

    addConfig(R.layout.f_new_seed_or_restore, false, false)

    addConfig(R.layout.f_new_seed_pwd, true, false)

    addConfig(R.layout.f_new_seed_or_restore)

    addConfig(R.layout.f_new_seed_verify)

    addConfig(R.layout.f_new_seed_backup)

    addConfig(R.layout.f_new_seed_remove)

    addConfig(R.layout.f_new_seed_restore)

    addConfig(R.layout.f_new_seed_devicename)

}

fun addConfig(layoutId: Int, showStatusBar: Boolean = true, showBackArrow: Boolean = true) {
    mPageConfiguration[layoutId] = PageSetting(layoutId = layoutId, showStatusBar = showStatusBar, showBackArrow = showBackArrow)
}

fun getPageSetting(layoutId: Int): PageSetting {
    return mPageConfiguration[layoutId]!!
}

fun allAllPageIds(): List<Int> {
    return mPageConfiguration.keys.toList()
}
