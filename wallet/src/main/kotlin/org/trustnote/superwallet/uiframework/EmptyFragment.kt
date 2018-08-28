package org.trustnote.superwallet.uiframework

import org.trustnote.superwallet.R
import org.trustnote.superwallet.uiframework.FragmentBase

class EmptyFragment : FragmentBase() {
    override fun getLayoutId(): Int {
        return R.layout.debug_empty
    }
}

