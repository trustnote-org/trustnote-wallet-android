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
import org.trustnote.wallet.TApp
import org.trustnote.wallet.biz.me.createNewWallet
import org.trustnote.wallet.biz.msgs.FragmentMsgMyPairId
import org.trustnote.wallet.biz.msgs.FragmentMsgsChat
import org.trustnote.wallet.biz.msgs.FragmentMsgsContactsAdd
import org.trustnote.wallet.uiframework.ActivityBase
import org.trustnote.wallet.uiframework.FragmentBase
import org.trustnote.wallet.uiframework.FragmentEditBase
import org.trustnote.wallet.widget.CustomViewFinderScannerActivity
import org.trustnote.wallet.widget.MyDialogFragment

class FragmentPopupmenu : FragmentBase() {

    //TODO: scan logic should not depend fragment.
    var scanLogic: () -> Unit = {}
    var popLayoutId: Int = R.layout.l_quick_action
    var currentChatRef: FragmentMsgsChat? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val activityMain = activity as ActivityMain
        val view = inflater.inflate(popLayoutId, container, false)

        view.isClickable = true

        mRootView = view

        mRootView.setOnClickListener {
            onBackPressed()
        }

        mToolbar = view.findViewById(R.id.toolbar)


        findNullableViewById<View>(R.id.action_scan)?.setOnClickListener {

            onBackPressed()
            scanLogic.invoke()

        }

        findNullableViewById<View>(R.id.action_contacts_add)?.setOnClickListener {

            onBackPressed()
            activityMain.addL2Fragment(FragmentMsgsContactsAdd())
        }

        findNullableViewById<View>(R.id.action_wallet_create)?.setOnClickListener {
            onBackPressed()
            createNewWallet(activityMain)
        }

        findNullableViewById<View>(R.id.action_my_pair_code)?.setOnClickListener {
            onBackPressed()
            activityMain.addL2Fragment(FragmentMsgMyPairId())
        }

        findNullableViewById<View>(R.id.ic_edit_contacts_memo)?.setOnClickListener {
            onBackPressed()
            currentChatRef?.editFriendMemoName(currentChatRef!!.activity as ActivityBase)
        }

        findNullableViewById<View>(R.id.ic_remove_msg_contact)?.setOnClickListener {
            onBackPressed()
            currentChatRef?.removeContact()
        }

        findNullableViewById<View>(R.id.ic_clear_chat_history)?.setOnClickListener {
            onBackPressed()
            currentChatRef?.removeChatHistory()
        }

        return view
    }

    override fun getLayoutId(): Int {
        return R.layout.l_quick_action
    }



}
