package org.trustnote.wallet.biz.wallet

import org.trustnote.wallet.R


data class PageSetting(val layoutId: Int,
                       val showStatusBar: Boolean = true,
                       val showBackArrow: Boolean = true,
                       val clz: Class<out CreateWalletFragment> = CreateWalletFragment::class.java)

val mPageConfiguration: MutableMap<Int, PageSetting> = mutableMapOf()
val mPagePositionsToLayoutId: MutableMap<Int, Int> = mutableMapOf()
val mPageLayoutIdToPosition: MutableMap<Int, Int> = mutableMapOf()

fun setupUISettings() {

    if (mPageConfiguration.isNotEmpty()) {
        return
    }

    addConfig(R.layout.f_new_seed_disclaimer, CWFragmentDisclaimer::class.java, false, false)

    addConfig(R.layout.f_new_seed_devicename, CWFragmentDeviceName::class.java, true, false)

    addConfig(R.layout.f_new_seed_or_restore, CWFragmentNewSeedOrRestore::class.java,false, false)

    addConfig(R.layout.f_new_seed_pwd, CWFragmentPwd::class.java)

    addConfig(R.layout.f_new_seed_verify, CWFragmentVerify::class.java)

    addConfig(R.layout.f_new_seed_backup, CWFragmentBackup::class.java)

    addConfig(R.layout.f_new_seed_remove, CWFragmentRemove::class.java, true, false)

    addConfig(R.layout.f_new_seed_restore, CWFragmentRestore::class.java)

}

fun addConfig(layoutId: Int, clz: Class<out CreateWalletFragment> = CreateWalletFragment::class.java, showStatusBar: Boolean = true, showBackArrow: Boolean = true) {
    mPageConfiguration[layoutId] = PageSetting(layoutId = layoutId, showStatusBar = showStatusBar, showBackArrow = showBackArrow, clz = clz)
    mPagePositionsToLayoutId[mPagePositionsToLayoutId.size] = layoutId
    mPageLayoutIdToPosition[layoutId] = mPageLayoutIdToPosition.size
}



fun getPageSetting(layoutId: Int): PageSetting {
    return mPageConfiguration[layoutId]!!
}

fun getPageSettingByPosition(position: Int): PageSetting {
    return getPageSetting(mPagePositionsToLayoutId[position]!!)
}

fun getPageLayoutId(position: Int): Int {
    return mPagePositionsToLayoutId[position]!!
}

fun getPagePosition(layoutId: Int): Int {
    return mPageLayoutIdToPosition[layoutId]!!
}

fun createFragment(position: Int): CreateWalletFragment{
    val pageSetting = getPageSettingByPosition(position)
    val cons = pageSetting.clz.constructors[0]
    return cons.newInstance(pageSetting.layoutId) as CreateWalletFragment
}


fun allPagesSize(): Int {
    return mPageConfiguration.size
}


