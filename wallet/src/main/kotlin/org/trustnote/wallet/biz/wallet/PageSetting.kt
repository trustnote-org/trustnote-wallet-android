package org.trustnote.wallet.biz.wallet

import org.trustnote.wallet.R


data class PageSetting(val showStatusBar: Boolean = true, val showBackArrow: Boolean = true) {
}

val layoutId2Setting: MutableMap<Int, PageSetting> = mutableMapOf()

fun setupUISettings() {

    layoutId2Setting.put(R.layout.f_new_seed_or_restore, PageSetting(showStatusBar = false, showBackArrow = false))
    layoutId2Setting.put(R.layout.f_new_seed_prompt, PageSetting(showStatusBar = false, showBackArrow = false))

//    R.layout.f_new_seed_or_restore,
//    R.layout.f_new_seed_prompt,
//    R.layout.f_new_seed_show_warning,
//    R.layout.f_new_seed_verify,
//    R.layout.f_new_seed_remove

}


fun getPageSetting(layoutId: Int): PageSetting {
    val res: PageSetting? = layoutId2Setting.get(layoutId)
    return res ?: PageSetting()
}