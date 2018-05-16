package org.trustnote.wallet.uiframework

import android.os.Bundle
import android.support.annotation.CallSuper
import android.support.v7.app.AppCompatActivity
import android.view.WindowManager
import org.trustnote.wallet.TApplicationComponent
import org.trustnote.wallet.TApp

abstract class BaseActivity: AppCompatActivity() {

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        injectDependencies(TApp.graph)

        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        //window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        //        if (Build.VERSION.SDK_INT >= 21) {
        //            window.statusBarColor = ContextCompat.getColor(this, R.color.bg_white)
        //        }

    }

    abstract fun injectDependencies(graph: TApplicationComponent)
}