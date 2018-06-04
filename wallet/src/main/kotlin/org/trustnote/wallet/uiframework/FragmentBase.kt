package org.trustnote.wallet.uiframework

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.annotation.IdRes
import android.support.v4.app.Fragment
import android.support.v7.widget.Toolbar
import com.google.zxing.integration.android.IntentIntegrator
import org.trustnote.wallet.R
import org.trustnote.wallet.biz.TTT
import org.trustnote.wallet.biz.FragmentPageBase
import org.trustnote.wallet.biz.ActivityMain
import org.trustnote.wallet.biz.wallet.Credential
import org.trustnote.wallet.biz.wallet.WalletManager
import org.trustnote.wallet.util.Utils
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import android.widget.TextView
import org.trustnote.wallet.TApp
import org.trustnote.wallet.biz.me.FragmentMeMain

abstract class FragmentBase : Fragment() {

    lateinit var credential: Credential

    var isBottomLayerUI = false
    lateinit var mRootView: View
    lateinit var mToolbar: Toolbar
    var isCreated = false
    private val ttag = "TTTUI"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.l_f_with_toolbar, container, false)
        mRootView = inflater.inflate(getLayoutId(), null)
        view.findViewById<FrameLayout>(R.id.fragment_frame).addView(mRootView)
        view.isClickable = true
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mRootView = view
        isCreated = false

        mRootView = view
        initFragment(mRootView!!)

        isCreated = true
    }

    open fun initFragment(view: View) {

        if (arguments != null && arguments.containsKey(TTT.KEY_WALLET_ID)) {
            val walletId = arguments.getString(TTT.KEY_WALLET_ID)
            credential = WalletManager.model.findWallet(walletId)
        }

        mToolbar = findViewById(R.id.toolbar)

        setupToolbar()
    }

    open fun inflateMenu(menu: Menu, inflater: MenuInflater) {
        //inflater.inflate(R.menu.menu_parent_fragment, menu)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflateMenu(menu, inflater)
    }

    override fun onResume() {
        super.onResume()
        Utils.debugLog("$ttag:${this.javaClass.canonicalName}::onResume")
        updateUI()
    }

    override fun onPause() {
        super.onPause()
        Utils.debugLog("$ttag:${this.javaClass.canonicalName}::onPause")
        updateUI()
    }

    abstract fun getLayoutId(): Int

    open fun setupToolbar() {

        //TODO: better to do it in xml
        if (activity is ActivityMain) {
            if (this is FragmentPageBase) {
                mToolbar.setBackgroundResource(R.color.page_bg)
            } else if (this is FragmentMeMain) {
                mToolbar.setBackgroundResource(R.color.home_line_middle)
            } else {
                mToolbar.setBackgroundResource(R.color.bg_white)
            }
        }

        setHasOptionsMenu(true)

        (activity as ActivityBase).setSupportActionBar(mToolbar)
        val actionBar = (activity as ActivityBase).supportActionBar!!

        actionBar.setDisplayShowTitleEnabled(false)

        if (!isBottomLayerUI) {
            actionBar.setDisplayHomeAsUpEnabled(!isBottomLayerUI)
            actionBar.setDisplayShowHomeEnabled(!isBottomLayerUI)
            mToolbar.setNavigationIcon(TApp.smallIconBackHome)
            mToolbar.setNavigationOnClickListener {
                onBackPressed()
            }
        }

        mToolbar.overflowIcon = TApp.smallIconQuickAction


        mToolbar.findViewById<TextView>(R.id.toolbar_title).text = getTitle()

    }

    open fun getTitle(): String {
        return ""
    }

    fun <T : View> findViewById(@IdRes id: Int): T {
        return mRootView.findViewById(id)
    }

    open fun updateUI() {
        Utils.debugLog("$ttag:${this.javaClass.canonicalName}::updateUI")
    }

    var scanResHandler: (String) -> Unit = {}

    fun startScan(scanResHandler: (String) -> Unit = {}) {
        this.scanResHandler = scanResHandler

        val integrator = IntentIntegrator.forSupportFragment(this)
        integrator.setOrientationLocked(true)
        integrator.setBeepEnabled(true)
        integrator.initiateScan()

    }

    fun setupScan(scanIcon: View, scanResHandler: (String) -> Unit = {}) {

        scanIcon.setOnClickListener {
            startScan(scanResHandler)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Utils.logW("$requestCode ___  $resultCode")
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {
                Utils.debugToast("Cancelled")
            } else {
                scanResHandler.invoke(result.contents ?: "")
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    fun showRefreshingUI(isShow: Boolean = false) {
        (activity as ActivityBase).showRefreshingUI(isShow)
    }

    fun showErrorUI(isShow: Boolean = false) {
        (activity as ActivityBase).showErrorUI(isShow)
    }

    fun openFragment(f: FragmentEditBase) {
        (activity as ActivityMain).openLevel2Fragment(f)
    }

    fun hideSystemSoftKeyboard() {
        val imm = activity.getSystemService (Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(mRootView.getWindowToken(), 0)
    }

    open fun onBackPressed() {
        hideSystemSoftKeyboard()
        activity.onBackPressed()
    }

}