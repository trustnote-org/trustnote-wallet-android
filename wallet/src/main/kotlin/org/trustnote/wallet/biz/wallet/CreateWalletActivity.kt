package org.trustnote.wallet.biz.wallet

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.ViewPager
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.EditText
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

        if (!BuildConfig.DEBUG) {
            window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        }

        setContentView(R.layout.activity_create_wallet)

        setupViewPager()

        setupFirstPage()

        findViewById<View>(R.id.titlebar_back_arrow).setOnClickListener {
            pageBackClicked()
        }

    }

    private fun setupFirstPage() {
        val layoutId = CreateWalletModel.getStartPageLayoutId()
        val pageSetting = getPageSetting(layoutId)
        adjustUIBySetting(pageSetting)
        switchToPage(getPagePosition(layoutId))
    }

    private fun setupViewPager() {
        mPager = findViewById(R.id.view_pager)
        mPagerAdapter = PagerAdapter(supportFragmentManager)
        mPager.adapter = mPagerAdapter
    }

    companion object {
        @JvmStatic
        fun startMe() {
            val intent = Intent(TApp.context, CreateWalletActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            TApp.context.startActivity(intent)
        }
    }

    private fun switchToPage(position: Int) {

        mPager.setCurrentItem(position, false)

        if (mPagerAdapter.getFragmentFromCache(position) != null) {
            mPagerAdapter.getFragmentFromCache(position)!!.onShowPage()
        }

    }

    fun adjustUIBySetting(pageSetting: PageSetting) {
        AndroidUtils.hideStatusBar(this, !pageSetting.showStatusBar)
        findViewById<View>(R.id.titlebar_back_arrow).visibility = (if (pageSetting.showBackArrow) View.VISIBLE else View.INVISIBLE)
    }

    fun nextPage() {

    }

    fun nextPage(pageLayoutId: Int) {
        switchToPage(getPagePosition(pageLayoutId))
    }

    override fun onBackPressed() {
        if (closeKeyboard()) {
            //
        } else {
            pageBackClicked()
        }
    }

    fun pageBackClicked() {
        closeKeyboard()
        val currentFragment = mPagerAdapter.getFragmentFromCache(mPager.currentItem)
        currentFragment!!.onBackPressed()
    }

    fun nextPage(pageLayoutId: Int, nextLayoutId: Int) {
        val nextFragment = mPagerAdapter.getFragmentFromCache(getPagePosition(pageLayoutId))
        nextFragment!!.mNextLayoutId = nextLayoutId
        switchToPage(getPagePosition(pageLayoutId))
    }

    fun iamDone() {
        finish()
        AndroidUtils.startActivity(StarterActivity::class.java)
    }

}

class PagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {
    private val cacheFragement: MutableMap<Int, CreateWalletFragment> = mutableMapOf()

    @Synchronized override fun getItem(position: Int): Fragment {
        val f = createFragment(position)
        cacheFragement[position] = f
        return f
    }

    @Synchronized fun getFragmentFromCache(position: Int): CreateWalletFragment? {
        return cacheFragement[position]
    }

    override fun getCount(): Int {
        return allPagesSize()
    }

}

