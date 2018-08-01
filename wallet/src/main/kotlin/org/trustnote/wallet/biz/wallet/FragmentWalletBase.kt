package org.trustnote.wallet.biz.wallet

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import org.trustnote.wallet.R
import org.trustnote.wallet.TApp
import org.trustnote.wallet.biz.ActivityMain
import org.trustnote.wallet.uiframework.FragmentBase

abstract class FragmentWalletBase : FragmentBase() {

    //TODO: empty constructor.
    fun getMyActivity(): ActivityMain {
        return activity as ActivityMain
    }

    override fun onResume() {

        super.onResume()

        listener(WalletManager.mWalletEventCenter)

    }

    fun scanEveryThing() {
        startScan {
            openSimpleInfoPage(it, activity.getString(R.string.scan_result_title))
        }
    }

}

