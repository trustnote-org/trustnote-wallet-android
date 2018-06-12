package org.trustnote.wallet.biz.wallet

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import org.trustnote.wallet.R
import org.trustnote.wallet.TApp
import org.trustnote.wallet.biz.ActivityMain
import org.trustnote.wallet.biz.TTT
import org.trustnote.wallet.biz.home.FragmentDialogCreateObserverFinish
import org.trustnote.wallet.biz.home.FragmentDialogCreateObserverQR
import org.trustnote.wallet.uiframework.FragmentBase
import org.trustnote.wallet.util.AndroidUtils
import org.trustnote.wallet.util.MyThreadManager

abstract class FragmentWalletBase : FragmentBase() {

    protected val disposables: CompositeDisposable = CompositeDisposable()

    //TODO: empty constructor.
    fun getMyActivity(): ActivityMain {
        return activity as ActivityMain
    }

    override fun onResume() {

        super.onResume()

        val d = WalletManager.mWalletEventCenter.observeOn(AndroidSchedulers.mainThread()).subscribe {
            updateUI()
        }
        disposables.add(d)

    }

    override fun onPause() {
        super.onPause()
        disposables.clear()
    }

    fun scanEveryThing() {
        startScan {
            openSimpleInfoPage(it, TApp.getString(R.string.scan_result_title))
        }
    }

}

