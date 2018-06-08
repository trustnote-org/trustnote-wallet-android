package org.trustnote.wallet.biz.home

import android.content.Context
import android.support.design.widget.TabLayout
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.view.View
import android.webkit.WebView
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.google.gson.JsonObject
import org.trustnote.wallet.R
import org.trustnote.wallet.biz.TTT
import org.trustnote.wallet.biz.wallet.FragmentWalletBase
import org.trustnote.wallet.biz.wallet.WalletManager
import org.trustnote.wallet.uiframework.FragmentBase
import org.trustnote.wallet.util.AndroidUtils
import org.trustnote.wallet.util.MyThreadManager
import org.trustnote.wallet.util.TTTUtils
import org.trustnote.wallet.widget.FragmentDialogInputPwd
import org.trustnote.wallet.widget.MyTextWatcher

class FragmentMainCreateWallet : FragmentWalletBase() {

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
        val tabLayout = findViewById<TabLayout>(R.id.sliding_tabs)
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

            FragmentDialogInputPwd.showMe(activity) {
                MyThreadManager.instance.runJSInNonUIThread { WalletManager.model.newManualWallet(it, editText.text.toString()) }
                activity.onBackPressed()
            }
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

    private lateinit var textView: TextView
    private lateinit var btn: Button
    private lateinit var scanResultJson: JsonObject
    private var myQrCode = ""
    private var observerAddress = ""

    override fun initFragment(view: View) {
        super.initFragment(view)
        val webView: WebView = view.findViewById(R.id.create_wallet_observer_warning)
        AndroidUtils.setupWarningWebView(webView, "OVSERVER_WALLET")

        val scanIcon = mRootView.findViewById<View>(R.id.create_wallet_observer_scan)
        setupScan(scanIcon) { showScanResult(it) }

        textView = mRootView.findViewById(R.id.create_wallet_observer_input)

        btn = mRootView.findViewById(R.id.create_wallet_observer_startbtn)

        btn.setOnClickListener {

            val f = FragmentDialogCreateObserverQR {

                AndroidUtils.openDialog(activity, FragmentDialogCreateObserverFinish {
                    MyThreadManager.instance.runJSInNonUIThread {
                        createObserverWallet()
                    }
                    observerAddress = it
                    activity.onBackPressed()
                })
            }

            AndroidUtils.addFragmentArguments(f, TTT.KEY_QR_CODE, myQrCode)
            AndroidUtils.openDialog(activity, f, false)

        }
    }

    private fun createObserverWallet() {

        val index = scanResultJson.get("n")?.asInt
        val pubkey = scanResultJson.get("pub")?.asString
        val title = scanResultJson.get("name")?.asString

        WalletManager.model.newObserveWallet(index ?: 0, pubkey ?: "", title ?: "")

    }

    override fun updateUI() {
        super.updateUI()
        if (textView.text.toString().isBlank()) {
            AndroidUtils.disableBtn(btn)
        } else {
            AndroidUtils.enableBtn(btn)
        }
    }

    fun showScanResult(scanResultStr: String) {
        scanResultJson = TTTUtils.scanStringToJsonObject(scanResultStr)
        textView.text = scanResultJson.get("pub")?.asString

        MyThreadManager.instance.runJSInNonUIThread {
            myQrCode = TTTUtils.genColdScancodeStep2(scanResultJson)
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

