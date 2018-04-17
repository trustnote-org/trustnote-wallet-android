package org.trustnote.wallet

import android.app.Application
import android.content.Context
import dagger.Lazy
import org.trustnote.wallet.js.TWebView
import org.trustnote.wallet.network.Hub
import org.trustnote.wallet.util.Prefs
import org.trustnote.wallet.walletadmin.WalletModel
import timber.log.Timber
import javax.inject.Inject

class TApp : Application() {

    @Inject
    lateinit var debugTree: Lazy<Timber.DebugTree>

    companion object {
        //TODO:
        lateinit @JvmStatic var context: Context
        lateinit var graph: TApplicationComponent
    }

    override fun onCreate() {
        super.onCreate()

        initDependencyGraph()

        if (BuildConfig.DEBUG) {
            Timber.plant(debugTree.get())
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
        context = this
        TWebView.init(this)
        Prefs.with(this)
        WalletModel()
        Hub.instance
    }
}