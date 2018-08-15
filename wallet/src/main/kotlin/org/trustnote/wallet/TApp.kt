package org.trustnote.wallet

import android.app.Application
import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.os.Build
import android.support.annotation.StringRes
import android.webkit.WebView
import org.trustnote.wallet.biz.js.TWebView
import org.trustnote.wallet.network.HubManager
import org.trustnote.wallet.util.*
import timber.log.Timber
import javax.inject.Inject

class TApp : Application() {

    var debugTree: Timber.DebugTree = Timber.DebugTree()

    companion object {

        lateinit var context: Context
        lateinit var resources: Resources
        lateinit var graph: TApplicationComponent
        var isAlreadyShowUpgradeInfo = false

        var smallIconSize = 14
        lateinit var smallIconError: Drawable
        lateinit var smallIconBackHome: Drawable

        var userAlreadyInputPwd = false

        fun getString(@StringRes strResId: Int): String {
            return context.getString(strResId)
        }
    }

    override fun onCreate() {
        super.onCreate()

        initDependencyGraph()

        if (BuildConfig.DEBUG) {
            Timber.plant(debugTree)
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

        smallIconSize = TApp.context.resources.getDimension(R.dimen.small_icon).toInt()
        smallIconError = AndroidUtils.resizeErrDrawable(R.drawable.err, R.dimen.small_icon)
        smallIconBackHome = AndroidUtils.resizeDrawable(R.drawable.arrow_left, R.dimen.home_back_arrow_size)

        WebView.setWebContentsDebuggingEnabled(BuildConfig.DEBUG)

        MyThreadManager.instance.runInBack {
            AndroidUtils.readBip38List()
        }

    }
}