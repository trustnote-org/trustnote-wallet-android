package org.trustnote.wallet.biz

import android.os.Bundle
import android.view.*
import android.widget.FrameLayout
import org.trustnote.wallet.R
import org.trustnote.wallet.uiframework.FragmentBase

abstract class FragmentPageBase : FragmentBase() {

    fun getMyActivity(): ActivityMain {
        return activity as ActivityMain
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.l_page_bg, container, false)

        mToolbar = view.findViewById(R.id.toolbar)
        mRootView = inflater.inflate(getLayoutId(), null)

        view.findViewById<FrameLayout>(R.id.dialog_frame).addView(mRootView)

        return view
    }

    override fun setupToolbar() {

        super.setupToolbar()
        mToolbar.setBackgroundResource(R.color.page_bg)

    }

}

