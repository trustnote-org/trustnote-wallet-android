package org.trustnote.wallet.debugui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.trustnote.wallet.R
import org.trustnote.wallet.uiframework.BaseFragment

class EmptyFragment : BaseFragment() {
    override fun getLayoutId(): Int {
        return R.layout.debug_empty
    }
}

