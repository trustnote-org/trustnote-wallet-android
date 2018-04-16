package org.trustnote.wallet.uiframework

import android.os.Bundle
import android.support.annotation.CallSuper
import android.support.v7.app.AppCompatActivity
import org.trustnote.wallet.TApplicationComponent
import org.trustnote.wallet.TApp

abstract class BaseActivity: AppCompatActivity() {

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        injectDependencies(TApp.graph)
    }

    abstract fun injectDependencies(graph: TApplicationComponent)
}