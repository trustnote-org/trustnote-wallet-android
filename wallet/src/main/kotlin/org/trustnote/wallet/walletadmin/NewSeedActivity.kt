package org.trustnote.wallet.walletadmin

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.ViewPager
import android.support.v4.view.ViewPager.OnPageChangeListener
import android.view.WindowManager
import org.trustnote.wallet.R
import org.trustnote.wallet.TApp
import org.trustnote.wallet.TApplicationComponent
import org.trustnote.wallet.uiframework.BaseActivity

class NewSeedActivity : BaseActivity() {

    override fun injectDependencies(graph: TApplicationComponent) {
    }

    private lateinit var mPager: ViewPager
    private lateinit var mPagerAdapter: PagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)

        setContentView(R.layout.activity_new_seed)

        mPager = findViewById<ViewPager>(R.id.view_pager)
        mPagerAdapter = PagerAdapter(supportFragmentManager, mPager!!) //TODO:??

        mPager.addOnPageChangeListener(object : OnPageChangeListener {

            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }
            override fun onPageSelected(position: Int) {
                (mPagerAdapter.getItem(position) as NewSeedShowFragment).onShowPage()
            }

        })

        mPager.adapter = mPagerAdapter;
    }

    companion object {
        @JvmStatic fun startMe() {
            val intent = Intent(TApp.context, NewSeedActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            TApp.context.startActivity(intent)
        }
    }
}


class PagerAdapter(fm: FragmentManager, pager: ViewPager) : FragmentStatePagerAdapter(fm) {
    val mPager = pager;
    var allPageLayoutIds: Array<Int> = arrayOf(R.layout.f_new_seed_welcome,
            R.layout.f_new_seed_or_restore,
            R.layout.f_new_seed_prompt,
            R.layout.f_new_seed_show_warning,
            R.layout.f_new_seed_confirm,
            R.layout.f_new_seed_remove_confirm
    )

    //TODO: possible bug?
    val cache: MutableMap<Int, NewSeedShowFragment> = HashMap()


    override fun getItem(position: Int): Fragment {
        if (cache[position] == null) {
            cache += position to NewSeedShowFragment(allPageLayoutIds[position], mPager)
        }
        return cache[position]!!
    }

    override fun getCount(): Int {
        return allPageLayoutIds.size
    }
}