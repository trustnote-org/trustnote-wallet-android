package org.trustnote.wallet

import android.app.Application
import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.os.Build
import android.webkit.WebView
import dagger.Lazy
import org.trustnote.wallet.biz.wallet.WalletManager
import org.trustnote.wallet.js.TWebView
import org.trustnote.wallet.network.HubManager
import org.trustnote.wallet.biz.wallet.WalletModel
import org.trustnote.wallet.util.*
import timber.log.Timber
import javax.inject.Inject

class TApp : Application() {

    @Inject
    lateinit var debugTree: Lazy<Timber.DebugTree>

    companion object {
        //TODO:
        lateinit @JvmStatic var context: Context
        lateinit var resources: Resources
        lateinit var graph: TApplicationComponent

        var smallIconSize = 14
        lateinit var smallIconError: Drawable
        lateinit var smallIconQuickAction: Drawable
        lateinit var smallIconBackHome: Drawable
        var userAlreadyInputPwd = false
    }

    override fun onCreate() {
        super.onCreate()

        initDependencyGraph()

        if (BuildConfig.DEBUG) {
            Timber.plant(debugTree.get())
            Timber.plant(TimberFile(this))
        }

        init()
    }

    private fun initDependencyGraph() {
        graph = DaggerTApplicationComponent.builder()
                .applicationModule(ApplicationModule(this))
                .build()
        graph.injectTo(this)

    }

    //TODO: try other way as init.
    private fun init() {


         //disable IPv6 in emulator
         if ("google_sdk" == Build.PRODUCT) {
            java.lang.System.setProperty("java.net.preferIPv6Addresses", "false");
            java.lang.System.setProperty("java.net.preferIPv4Stack", "true");
        }

        context = this
        TApp.resources = resources

        TWebView.init(this)
        Prefs.with(this)
        HubManager.instance

        smallIconSize = TApp.context.resources.getDimension(R.dimen.small_icon).toInt()
        smallIconError = AndroidUtils.resizeErrDrawable(R.drawable.err, R.dimen.small_icon)
        smallIconQuickAction = AndroidUtils.resizeDrawable(R.drawable.quick_action, R.dimen.quick_action_size)
        smallIconBackHome = AndroidUtils.resizeDrawable(R.drawable.arrow_left, R.dimen.home_back_arrow_size)

        WebView.setWebContentsDebuggingEnabled(BuildConfig.DEBUG)

        MyThreadManager.instance.runInBack {
            AndroidUtils.readBip38List()
        }

        MyThreadManager.instance.runLowPriorityInBack {
            WalletManager.generateMyPairIdForFutureUse()
        }
    }
}