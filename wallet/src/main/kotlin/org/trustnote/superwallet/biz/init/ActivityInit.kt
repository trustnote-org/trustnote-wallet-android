package org.trustnote.superwallet.biz.init

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import org.trustnote.superwallet.BuildConfig
import org.trustnote.superwallet.R
import org.trustnote.superwallet.TApp
import org.trustnote.superwallet.TApplicationComponent
import org.trustnote.superwallet.biz.startMainActivityWithMenuId
import org.trustnote.superwallet.biz.wallet.CREATE_WALLET_STATUS
import org.trustnote.superwallet.uiframework.ActivityBase
import org.trustnote.superwallet.util.AndroidUtils
import org.trustnote.superwallet.util.Utils

class ActivityInit : ActivityBase() {

    override fun injectDependencies(graph: TApplicationComponent) {
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (CreateWalletModel.getCreationProgress() == CREATE_WALLET_STATUS.FINISHED) {
            finish()
            startMainActivityWithMenuId(R.id.menu_me)
        }

        setupUISettings()

        if (!BuildConfig.DEBUG) {
            window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        }

        setContentView(R.layout.activity_init)

        setupFirstPage()

    }

    private fun setupFirstPage() {
        val layoutId = CreateWalletModel.getStartPageLayoutId()
        val pageSetting = getPageSetting(layoutId)
        adjustUIBySetting(pageSetting)

        val f = pageSetting.clz.newInstance()

        showFragment(f)
    }

    companion object {
        @JvmStatic
        fun startMe() {
            val intent = Intent(TApp.context, ActivityInit::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            TApp.context.startActivity(intent)
        }
    }

    fun adjustUIBySetting(pageSetting: PageSetting) {
        AndroidUtils.hideStatusBar(this, !pageSetting.showStatusBar)
    }

}


