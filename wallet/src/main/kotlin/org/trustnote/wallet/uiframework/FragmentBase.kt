package org.trustnote.wallet.uiframework

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.annotation.IdRes
import android.support.v7.widget.Toolbar
import com.google.zxing.integration.android.IntentIntegrator
import org.trustnote.wallet.R
import org.trustnote.wallet.biz.TTT
import org.trustnote.wallet.biz.wallet.Credential
import org.trustnote.wallet.biz.wallet.WalletManager
import org.trustnote.wallet.util.Utils
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.Subject
import me.yokeyword.swipebackfragment.SwipeBackFragment
import org.trustnote.wallet.TApp
import org.trustnote.wallet.biz.home.FragmentMainCreateWalletObserve
import org.trustnote.wallet.util.AndroidUtils
import org.trustnote.wallet.util.SCAN_RESULT_TYPE
import org.trustnote.wallet.util.TTTUtils
import org.trustnote.wallet.widget.FragmentDialogSelectWallet

abstract class FragmentBase : SwipeBackFragment() {

    lateinit var credential: Credential

    var mToolbarVisibility = View.VISIBLE
    var isBottomLayerUI = false
    lateinit var mRootView: View
    lateinit var mToolbar: Toolbar
    var isCreated = false
    private val ttag = "TTTUI"
    var supportSwipeBack = true
    protected val disposables: CompositeDisposable = CompositeDisposable()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.f_base, container, false)
        val childView = inflater.inflate(getLayoutId(), null)

        view.findViewById<FrameLayout>(R.id.f_base_container).addView(childView)
        view.isClickable = true

        mRootView = childView
        mToolbar = view.findViewById(R.id.toolbar)

        return if (supportSwipeBack) attachToSwipeBack(view) else view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isCreated = false

        initFragment(mRootView!!)

        isCreated = true
    }

    open fun initFragment(view: View) {

        if (arguments != null && arguments.containsKey(TTT.KEY_WALLET_ID)) {
            val walletId = arguments.getString(TTT.KEY_WALLET_ID)
            credential = WalletManager.model.findWallet(walletId)
        }

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
        disposables.clear()
    }

    fun listener(eventCenter: Subject<Boolean>, function: () -> Unit = { updateUI() }) {
        val d = eventCenter.observeOn(AndroidSchedulers.mainThread()).subscribe {
            function.invoke()
        }
        disposables.add(d)
    }

    abstract fun getLayoutId(): Int

    open fun setupToolbar() {

        mToolbar.setBackgroundResource(R.color.bg_white)

        setHasOptionsMenu(true)

        (activity as ActivityBase).setSupportActionBar(mToolbar)
        val actionBar = (activity as ActivityBase).supportActionBar!!

        actionBar.setDisplayShowTitleEnabled(false)
        actionBar.setDisplayHomeAsUpEnabled(false)
        actionBar.setDisplayShowHomeEnabled(false)

        if (!isBottomLayerUI) {
            mToolbar.findViewById<ImageView>(R.id.toolbar_left_arrow).setOnClickListener {
                onBackPressed()
            }
        } else {
            mToolbar.findViewById<ImageView>(R.id.toolbar_left_arrow).visibility = View.INVISIBLE
        }

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

    private var scanResHandler: (String) -> Unit = {}

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

    fun addL2Fragment(f: FragmentBase) {

        (activity as ActivityBase).addL2Fragment(f)

    }

    fun addFragment(f: FragmentBase) {

        (activity as ActivityBase).addFragment(f)

    }


    fun showFragment(f: FragmentBase) {

        (activity as ActivityBase).showFragment(f)

    }


    fun removeMeFromBackStack() {

        val manager = activity.supportFragmentManager
        val trans = manager.beginTransaction()
        trans.remove(this)
        trans.commit()
        //manager.popBackStack()

    }

    fun hideSystemSoftKeyboard() {
        val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(mRootView.getWindowToken(), 0)
    }

    open fun onBackPressed() {
        hideSystemSoftKeyboard()
        activity.onBackPressed()
    }

    fun openSimpleInfoPage(msg: String, title: String) {
        val bundle = Bundle()
        bundle.putString(AndroidUtils.KEY_BUNDLE_MSG, msg)
        bundle.putString(AndroidUtils.KEY_BUNDLE_TITLE, title)
        val f = FragmentSimplePage()
        f.arguments = bundle
        addL2Fragment(f)
    }

    fun showOrHideToolbar() {
        mToolbar.visibility = mToolbarVisibility
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {

            R.id.action_scan -> {
                startScan {
                    handleUnknownScanRes(it)
                }
                return true
            }

        }
        return false
    }

    fun handleUnknownScanRes(qrCode: String) {
        val qrType = TTTUtils.parseQrCodeType(qrCode)
        when (qrType) {

            SCAN_RESULT_TYPE.MN_TRANSFER -> {
                val f = FragmentDialogSelectWallet()
                AndroidUtils.addFragmentArguments(f, TTT.KEY_TRANSFER_QRCODEW, qrCode)
                addL2Fragment(f)
            }

            SCAN_RESULT_TYPE.COLD_WALLET -> {
                val f = FragmentMainCreateWalletObserve()
                AndroidUtils.addFragmentArguments(f, AndroidUtils.KEY_BUNDLE_QRCODE, qrCode)
                addL2Fragment(f)
            }

            SCAN_RESULT_TYPE.TTT_PAIRID -> {
            }

            SCAN_RESULT_TYPE.UNKNOWN -> {
                openSimpleInfoPage(qrCode, TApp.getString(R.string.scan_result_title))
            }
        }
    }


}