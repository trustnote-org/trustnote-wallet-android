package org.trustnote.wallet.biz.wallet

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.ViewPager
import android.support.v4.view.ViewPager.OnPageChangeListener
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import org.trustnote.wallet.uiframework.BaseActivity
import org.trustnote.wallet.util.AndroidUtils
import org.trustnote.wallet.*


class CreateWalletActivity : BaseActivity() {

    override fun injectDependencies(graph: TApplicationComponent) {
    }

    private lateinit var mPager: ViewPager
    private lateinit var mPagerAdapter: PagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (CreateWalletModel.getCreationProgress() == CREATE_WALLET_STATUS.FINISHED) {
            finish()
            startMainActivityWithMenuId(R.id.action_home)
        }

        setupUISettings()

        window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)

        setContentView(R.layout.activity_create_wallet)

        mPager = findViewById(R.id.view_pager)
        mPagerAdapter = PagerAdapter(supportFragmentManager, mPager!!)

        mPager.adapter = mPagerAdapter

        val pageChangeListener = object : OnPageChangeListener {

            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
            }

        }

        val layoutId = CreateWalletModel.getStartPageLayoutId()
        val pageSetting = getPageSetting(layoutId)
        adjustUIBySetting(pageSetting)
        mPager.currentItem = getPagePosition(layoutId)

    }

    companion object {
        @JvmStatic
        fun startMe() {
            val intent = Intent(TApp.context, CreateWalletActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            TApp.context.startActivity(intent)
        }
    }


    fun adjustUIBySetting(pageSetting: PageSetting) {
        AndroidUtils.hideStatusBar(this, !pageSetting.showStatusBar)
        findViewById<View>(R.id.titlebar_back_arrow).visibility = (if (pageSetting.showBackArrow) View.VISIBLE else View.INVISIBLE)
    }

    fun nextPage() {

    }

    fun nextPage(pageLayoutId: Int) {
        mPager.currentItem = getPagePosition(pageLayoutId)
    }

    override fun onBackPressed() {
        val currentFragment = mPagerAdapter.getFragmentFromCache(mPager.currentItem)
        currentFragment.onBackPressed()
    }

    fun nextPage(pageLayoutId: Int, nextLayoutId: Int) {
        val nextFragment = mPagerAdapter.getFragmentFromCache(getPagePosition(pageLayoutId))
        nextFragment.mNextLayoutId = nextLayoutId
        mPager.currentItem = getPagePosition(pageLayoutId)
    }

}

class PagerAdapter(fm: FragmentManager, private val pager: ViewPager) : FragmentStatePagerAdapter(fm) {
    private val cacheFragement: MutableMap<Int, CreateWalletFragment> = mutableMapOf()

    override fun getItem(position: Int): Fragment {
        val f = createFragment(position)
        cacheFragement[position] = f
        return f
    }

    fun getFragmentFromCache(position: Int): CreateWalletFragment {
        return cacheFragement[position]!!
    }

    override fun getCount(): Int {
        return allPagesSize()
    }

    override fun setPrimaryItem(container: ViewGroup, position: Int, obj: Any ) {
        cacheFragement[position]!!.onShowPage()
    }
}

