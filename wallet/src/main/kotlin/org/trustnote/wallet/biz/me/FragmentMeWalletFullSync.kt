package org.trustnote.wallet.biz.me

import android.view.View
import android.webkit.WebView
import org.trustnote.wallet.R
import org.trustnote.wallet.biz.wallet.FragmentWalletBase
import org.trustnote.wallet.biz.wallet.WalletManager
import org.trustnote.wallet.util.AndroidUtils
import org.trustnote.wallet.widget.MnemonicsGridView
import org.trustnote.wallet.widget.MyDialogFragment

class FragmentMeWalletFullSync : FragmentWalletBase() {
    override fun getLayoutId(): Int {
        return R.layout.f_me_clone_sync
    }




    override fun initFragment(view: View) {

        super.initFragment(view)

        val webView: WebView = view.findViewById(R.id.full_sync_warning)
        AndroidUtils.setupWarningWebView(webView, "FULLSYNC")

        view.findViewById<View>(R.id.me_clone_sync_btn).setOnClickListener {
            WalletManager.model.fullRefreshing()
            activity.onBackPressed()
        }

    }
}

