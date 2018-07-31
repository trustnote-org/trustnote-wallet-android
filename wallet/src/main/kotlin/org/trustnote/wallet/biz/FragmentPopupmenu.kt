package org.trustnote.wallet.biz;

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import org.trustnote.wallet.R
import org.trustnote.wallet.biz.me.createNewWallet
import org.trustnote.wallet.biz.msgs.FragmentMsgMyPairId
import org.trustnote.wallet.biz.msgs.FragmentMsgsContactsAdd
import org.trustnote.wallet.uiframework.FragmentBase
import org.trustnote.wallet.widget.CustomViewFinderScannerActivity

class FragmentPopupmenu : FragmentBase() {

    //TODO: scan logic should not depend fragment.
    var scanLogic: () -> Unit = {}
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val activityMain = activity as ActivityMain
        val view = inflater.inflate(R.layout.l_quick_action, container, false)

        view.isClickable = true

        mRootView = view

        mRootView.setOnClickListener {
            onBackPressed()
        }

        mToolbar = view.findViewById(R.id.toolbar)


        findViewById<View>(R.id.action_scan).setOnClickListener {

            onBackPressed()
            scanLogic.invoke()

        }

        findViewById<View>(R.id.action_contacts_add).setOnClickListener {

            onBackPressed()
            activityMain.addL2Fragment(FragmentMsgsContactsAdd())

        }

        findViewById<View>(R.id.action_wallet_create).setOnClickListener {
            onBackPressed()
            createNewWallet(activityMain)
        }

        findViewById<View>(R.id.action_my_pair_code).setOnClickListener {
            onBackPressed()
            activityMain.addL2Fragment(FragmentMsgMyPairId())
        }



        return view
    }

    override fun getLayoutId(): Int {
        return R.layout.l_quick_action
    }

}
