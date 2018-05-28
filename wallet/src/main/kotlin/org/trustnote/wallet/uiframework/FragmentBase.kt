package org.trustnote.wallet.uiframework

import android.content.Intent
import android.os.Bundle
import android.support.annotation.IdRes
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.zxing.integration.android.IntentIntegrator
import org.trustnote.wallet.TTT
import org.trustnote.wallet.biz.wallet.Credential
import org.trustnote.wallet.biz.wallet.WalletManager
import org.trustnote.wallet.util.Utils

abstract class FragmentBase : Fragment() {


    lateinit var credential: Credential

    lateinit var mRootView: View
    var isCreated = false
    private val ttag = "TTTUI"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mRootView = inflater.inflate(getLayoutId(), container, false)
        mRootView.isClickable = true
        return mRootView
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

        setupToolbar()
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

    }

    fun <T : View> findViewById(@IdRes id: Int): T {
        return mRootView.findViewById(id)
    }

    open fun updateUI() {
        Utils.debugLog("$ttag:${this.javaClass.canonicalName}::updateUI")

    }

    var scanResHandler: (String) -> Unit = {}

    fun setupScan(scanIcon: View, scanResHandler: (String) -> Unit = {}) {

        this.scanResHandler = scanResHandler
        scanIcon.setOnClickListener {

            val integrator = IntentIntegrator.forSupportFragment(this)
            integrator.setOrientationLocked(true)
            integrator.setBeepEnabled(true)
            integrator.initiateScan()
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
        (activity as BaseActivity).showRefreshingUI(isShow)
    }

    fun showErrorUI(isShow: Boolean = false) {
        (activity as BaseActivity).showErrorUI(isShow)
    }


}