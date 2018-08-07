package org.trustnote.wallet.biz

import android.os.Bundle
import android.view.*
import android.widget.FrameLayout
import org.trustnote.wallet.R
import org.trustnote.wallet.TApp
import org.trustnote.wallet.uiframework.FragmentBase
import org.trustnote.wallet.widget.PageHeader

abstract class FragmentPageBase : FragmentBase() {

    fun getMyActivity(): ActivityMain {
        return activity as ActivityMain
    }

    var useLayoutFromTop: Boolean = false
    lateinit var pageOutmostLayout: View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(
                if (useLayoutFromTop) R.layout.l_page_bg_from_top else R.layout.l_page_bg, container, false)

        pageOutmostLayout = view.findViewById(R.id.page_outermost_layout)

        mToolbar = view.findViewById(R.id.toolbar)
        mRootView = inflater.inflate(getLayoutId(), null)

        view.findViewById<FrameLayout>(R.id.dialog_frame).addView(mRootView)

        return view
    }

    fun fixOutmostLayoutPaddingBottom(paddingBottomResId: Int) {

        pageOutmostLayout.setPadding(pageOutmostLayout.paddingLeft,
                pageOutmostLayout.paddingTop,
                pageOutmostLayout.paddingRight,
                TApp.resources.getDimensionPixelSize(paddingBottomResId))

    }

    override fun setupToolbar() {

        super.setupToolbar()
        mToolbar.setBackgroundResource(R.color.page_bg)

    }

}

