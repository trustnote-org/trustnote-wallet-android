package org.trustnote.wallet.biz.me

import android.view.View
import android.webkit.WebView
import org.trustnote.wallet.R
import org.trustnote.wallet.biz.FragmentProgressBlocking
import org.trustnote.wallet.biz.init.CreateWalletModel
import org.trustnote.wallet.biz.wallet.FragmentWalletBase
import org.trustnote.wallet.biz.wallet.WalletManager
import org.trustnote.wallet.util.AndroidUtils
import org.trustnote.wallet.widget.FragmentDialogInputPwd

class FragmentMeWalletFullSync : FragmentWalletBase() {
    override fun getLayoutId(): Int {
        return R.layout.f_me_clone_sync
    }

    override fun initFragment(view: View) {

        super.initFragment(view)

        val webView: WebView = view.findViewById(R.id.full_sync_warning)

        AndroidUtils.setupWarningWebView(webView, R.string.FULLSYNC_WARNING1)

        view.findViewById<View>(R.id.me_clone_sync_btn).setOnClickListener {

            val f = FragmentProgressBlocking()
            f.afterWaitingLogic = {
                AndroidUtils.showIosToast(activity.getString(R.string.fullsync_finished))
            }
            addL2Fragment(f)

            WalletManager.model.refreshExistWallet()

//            FragmentDialogInputPwd.showMe(activity, {
//
//                CreateWalletModel.savePassphraseInRam(it)
//                activity.onBackPressed()
//            })
        }

    }
}

