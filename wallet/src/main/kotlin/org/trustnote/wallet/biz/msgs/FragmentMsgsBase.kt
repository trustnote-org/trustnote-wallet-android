package org.trustnote.wallet.biz.msgs

import io.reactivex.disposables.CompositeDisposable
import org.trustnote.wallet.uiframework.FragmentBase
import org.trustnote.wallet.util.AndroidUtils

abstract class FragmentMsgsBase : FragmentBase() {


    val model: MsgsModel = MsgsModel.instance

    protected val disposables: CompositeDisposable = CompositeDisposable()

    override fun onPause() {
        super.onPause()
        disposables.clear()
    }

}

