package org.trustnote.wallet.debugui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.trustnote.wallet.R
import org.trustnote.wallet.uiframework.BaseFragment

class EmptyFragment : BaseFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.debug_empty, container, false);
    }
}

