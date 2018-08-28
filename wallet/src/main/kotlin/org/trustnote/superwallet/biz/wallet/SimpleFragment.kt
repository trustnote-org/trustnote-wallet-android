package org.trustnote.superwallet.biz.wallet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import org.trustnote.superwallet.R
import org.trustnote.superwallet.uiframework.FragmentBase
import org.trustnote.superwallet.util.MyThreadManager
import org.trustnote.superwallet.util.Utils

class SimpleFragment : FragmentBase() {

    override fun getLayoutId(): Int {
        return R.layout.f_new_wallet
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.f_new_wallet, container, false)
    }

    override fun initFragment(view: View) {
        super.initFragment(view)
        view!!.findViewById<View>(R.id.wallet_new_ok).setOnClickListener {
            val walletNameTV = view!!.findViewById<EditText>(R.id.wallet_new_name)
            val walletName = walletNameTV.text.toString()
            if (walletName.isNullOrBlank()) {
                Utils.toastMsg(R.string.wallet_new_msg_emptyname)
            } else {
//                MyThreadManager.instance.runJSInNonUIThread {
//                    WalletManager.model.newWallet(walletName)
//                    activity.finish()
//                }
            }
        }

    }
}