package org.trustnote.wallet.uiframework

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.View

abstract class BaseFragment : Fragment() {
    lateinit var mRootView: View
    var isCreated = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mRootView = view
        isCreated = false

        mRootView = view
        initFragment(mRootView!!)

        isCreated = true
    }

    open fun initFragment(view: View) {

    }

    abstract fun getLayoutId(): Int


}