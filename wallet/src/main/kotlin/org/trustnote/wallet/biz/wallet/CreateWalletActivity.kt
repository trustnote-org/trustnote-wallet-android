package org.trustnote.wallet.biz.wallet

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.ViewPager
import android.support.v4.view.ViewPager.OnPageChangeListener
import android.view.View
import android.view.WindowManager
import org.trustnote.wallet.R
import org.trustnote.wallet.TApp
import org.trustnote.wallet.TApplicationComponent
import org.trustnote.wallet.uiframework.BaseActivity
import org.trustnote.wallet.util.AndroidUtils
import com.koushikdutta.async.AsyncServer.post



class CreateWalletActivity : BaseActivity() {

    override fun injectDependencies(graph: TApplicationComponent) {
    }

    private lateinit var mPager: ViewPager
    private lateinit var mPagerAdapter: PagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupUISettings()

        window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)

        setContentView(R.layout.activity_create_wallet)

        mPager = findViewById(R.id.view_pager)
        mPagerAdapter = PagerAdapter(supportFragmentManager, mPager!!)

        mPager.adapter = mPagerAdapter

        val pageChangeListener = object:OnPageChangeListener {

            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                (mPagerAdapter.getItem(position) as CreateWalletFragment).onShowPage()
            }

        }
        mPager.addOnPageChangeListener(pageChangeListener)

        mPager.post { pageChangeListener.onPageSelected(mPager.getCurrentItem()) }

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
}


class PagerAdapter(fm: FragmentManager, private val pager: ViewPager) : FragmentStatePagerAdapter(fm) {
    var allPageLayoutIds: Array<Int> = arrayOf(
            R.layout.f_new_seed_or_restore,
            R.layout.f_new_seed_prompt,
            R.layout.f_new_seed_show_warning,
            R.layout.f_new_seed_confirm,
            R.layout.f_new_seed_remove_confirm
    )

    //TODO: possible bug?
    private val cache: MutableMap<Int, CreateWalletFragment> = HashMap()


    override fun getItem(position: Int): Fragment {
        if (cache[position] == null) {
            cache += position to CreateWalletFragment(allPageLayoutIds[position], pager)
        }
        return cache[position]!!
    }

    override fun getCount(): Int {
        return allPageLayoutIds.size
    }
}