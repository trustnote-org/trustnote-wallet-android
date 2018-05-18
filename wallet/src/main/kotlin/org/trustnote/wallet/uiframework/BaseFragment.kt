package org.trustnote.wallet.uiframework

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.View

open class BaseFragment : Fragment() {
    lateinit var mRootView: View

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mRootView = view
    }
}