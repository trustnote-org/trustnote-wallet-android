package org.trustnote.wallet.uiframework

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

abstract class FragmentBase : Fragment() {
    lateinit var mRootView: View
    var isCreated = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mRootView = inflater.inflate(getLayoutId(), container, false)
        return mRootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mRootView = view
        isCreated = false

        mRootView = view
        initFragment(mRootView!!)

        isCreated = true
    }

    open fun initFragment(view: View) {
        setupToolbar()
    }

    override fun onResume() {
        super.onResume()
        updateUI()
    }

    abstract fun getLayoutId(): Int

    open fun setupToolbar() {

    }

    open fun updateUI() {

    }

}