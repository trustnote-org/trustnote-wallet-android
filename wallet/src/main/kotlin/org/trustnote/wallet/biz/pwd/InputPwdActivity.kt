package org.trustnote.wallet.biz.pwd

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.ViewPager
import android.view.View
import android.view.WindowManager
import org.trustnote.wallet.*
import org.trustnote.wallet.uiframework.BaseActivity
import org.trustnote.wallet.util.AndroidUtils


class InputPwdActivity : BaseActivity() {

    override fun injectDependencies(graph: TApplicationComponent) {
    }

    private lateinit var mPager: ViewPager
    private lateinit var mPagerAdapter: PagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!BuildConfig.DEBUG) {
            window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        }

        setContentView(R.layout.activity_input_pwd)

        setupViewPager()

        adjustUIBySetting()

    }

    private fun setupViewPager() {
        mPager = findViewById(R.id.view_pager)
        mPagerAdapter = PagerAdapter(supportFragmentManager)
        mPager.adapter = mPagerAdapter
    }

    private fun switchToPage(position: Int) {
    }

    fun adjustUIBySetting() {
        AndroidUtils.hideStatusBar(this, true)
        findViewById<View>(R.id.top_back_arrow).visibility = View.GONE
    }

}

class PagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {
    @Synchronized override fun getItem(position: Int): Fragment {
        return FragmentInputPwd(R.layout.f_input_pwd)
    }

    override fun getCount(): Int {
        return 1
    }

}

