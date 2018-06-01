package org.trustnote.wallet.uiframework

import org.trustnote.wallet.R
import org.trustnote.wallet.uiframework.FragmentBase

class EmptyFragment : FragmentBase() {
    override fun getLayoutId(): Int {
        return R.layout.debug_empty
    }
}

