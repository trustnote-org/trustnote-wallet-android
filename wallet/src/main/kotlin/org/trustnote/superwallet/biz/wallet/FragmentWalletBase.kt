package org.trustnote.superwallet.biz.wallet

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import org.trustnote.superwallet.R
import org.trustnote.superwallet.TApp
import org.trustnote.superwallet.biz.ActivityMain
import org.trustnote.superwallet.uiframework.FragmentBase

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

