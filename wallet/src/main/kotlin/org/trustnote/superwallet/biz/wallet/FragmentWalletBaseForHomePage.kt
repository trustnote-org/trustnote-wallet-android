package org.trustnote.superwallet.biz.wallet

import org.trustnote.superwallet.R
import org.trustnote.superwallet.TApp
import org.trustnote.superwallet.biz.ActivityMain
import org.trustnote.superwallet.uiframework.FragmentBaseForHomePage

abstract class FragmentWalletBaseForHomePage : FragmentBaseForHomePage() {


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

