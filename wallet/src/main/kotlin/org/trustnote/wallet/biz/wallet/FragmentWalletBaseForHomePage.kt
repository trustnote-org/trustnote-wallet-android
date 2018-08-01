package org.trustnote.wallet.biz.wallet

import org.trustnote.wallet.R
import org.trustnote.wallet.TApp
import org.trustnote.wallet.biz.ActivityMain
import org.trustnote.wallet.uiframework.FragmentBaseForHomePage

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

