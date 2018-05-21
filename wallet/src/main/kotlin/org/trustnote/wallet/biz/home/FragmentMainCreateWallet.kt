package org.trustnote.wallet.biz.home

import android.content.Context
import android.content.Intent
import android.support.design.widget.TabLayout
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.view.View
import android.webkit.WebView
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.google.zxing.integration.android.IntentIntegrator
import org.trustnote.wallet.R
import org.trustnote.wallet.biz.wallet.WalletManager
import org.trustnote.wallet.uiframework.FragmentBase
import org.trustnote.wallet.util.AndroidUtils
import org.trustnote.wallet.util.Utils
import org.trustnote.wallet.widget.MyTextWatcher

class FragmentMainCreateWallet : FragmentMainBase() {

    override fun getLayoutId(): Int {
        return R.layout.f_main_create_wallet
    }

    override fun initFragment(view: View) {
        super.initFragment(view)

        // Find the view pager that will allow the user to swipe between fragments
        val viewPager: ViewPager = mRootView.findViewById(R.id.viewpager)

        // Create an adapter that knows which fragment should be shown on each page
        val adapter = SimpleFragmentPagerAdapter(activity, childFragmentManager)

        // Set the adapter onto the view pager
        viewPager.adapter = adapter

        // Give the TabLayout the ViewPager
        val tabLayout = findViewById(R.id.sliding_tabs) as TabLayout
        tabLayout.setupWithViewPager(viewPager)

    }

}

class FragmentMainCreateWalletNormal : FragmentBase() {
    override fun getLayoutId(): Int {
        return R.layout.f_main_create_wallet_normal
    }

    lateinit var editText: EditText
    lateinit var button: Button
    lateinit var err: TextView

    private val textWatcher = MyTextWatcher(this)

    override fun initFragment(view: View) {
        super.initFragment(view)

        editText = view.findViewById(R.id.create_wallet_normal)
        editText.addTextChangedListener(textWatcher)

        err = view.findViewById<EditText>(R.id.create_wallet_normal_err)

        button = view.findViewById<Button>(R.id.create_wallet_normal_btn)

        button.setOnClickListener {
            Utils.runInbackground { WalletManager.model.newWallet(editText.text.toString()) }
            activity.onBackPressed()
        }
    }

    override fun updateUI() {
        super.updateUI()

        AndroidUtils.disableBtn(button)
        err.visibility = View.INVISIBLE

        val siz = editText.text.toString().length
        if (siz in 1..20) {
            AndroidUtils.enableBtn(button)
            err.visibility = View.INVISIBLE
        }

        if (siz > 20) {
            AndroidUtils.disableBtn(button)
            err.visibility = View.VISIBLE
        }
    }
}

class FragmentMainCreateWalletObserve : FragmentBase() {
    override fun getLayoutId(): Int {
        return R.layout.f_main_create_wallet_observe
    }

    lateinit var textView: TextView
    lateinit var btn: Button
    var scanResultStr = ""
    var myQrCode = ""
    var observerAddress = ""

    override fun initFragment(view: View) {
        super.initFragment(view)
        val webView: WebView = view.findViewById(R.id.create_wallet_observer_warning)
        AndroidUtils.setupWarningWebView(webView, "OVSERVER_WALLET")

        mRootView.findViewById<View>(R.id.create_wallet_observer_scan).setOnClickListener {

            val integrator = IntentIntegrator.forSupportFragment(this)
            integrator.setOrientationLocked(true)
            integrator.setBeepEnabled(true)
            integrator.initiateScan()

            //(parentFragment.activity as MainActivity).openLevel2Fragment(Bundle(), QRFragment::class.java)
        }

        textView = mRootView.findViewById(R.id.create_wallet_observer_input)

        btn = mRootView.findViewById(R.id.create_wallet_observer_startbtn)

        btn.setOnClickListener {
            FragmentDialogCreateObserverQR.showMe(myQrCode, activity, {
                FragmentDialogCreateObserverFinish.showMe(activity, {
                    observerAddress = it
                    activity.onBackPressed()
                })
            })
        }
    }

    override fun updateUI() {
        super.updateUI()
        if (textView.text.toString().isBlank()) {
            AndroidUtils.enableBtn(btn)
            //AndroidUtils.disableBtn(btn)
        } else {
            AndroidUtils.enableBtn(btn)
        }
    }

    fun showScanResult(scanResultStr: String) {
        this.scanResultStr = scanResultStr
        val walletPubkey = WalletManager.model.parseObserverScanResult(scanResultStr)
        textView.text = walletPubkey

        Utils.runInbackground {
            myQrCode = WalletManager.model.genColdScancodeFromWalletId(scanResultStr)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Utils.logW("$requestCode ___  $resultCode")
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {
                Utils.debugToast("Cancelled")
            } else {
                showScanResult(result.contents)
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
}

class SimpleFragmentPagerAdapter(private val mContext: Context, fm: FragmentManager) : FragmentPagerAdapter(fm) {
    override fun getCount(): Int {
        return 2
    }

    override fun getItem(position: Int): FragmentBase {
        return if (position == 0) {
            FragmentMainCreateWalletNormal()
        } else
            FragmentMainCreateWalletObserve()
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when (position) {
            0 -> mContext.getString(R.string.create_wallet_tab_title_normal)
            1 -> mContext.getString(R.string.create_wallet_tab_title_observe)
            else -> null
        }
    }

}

