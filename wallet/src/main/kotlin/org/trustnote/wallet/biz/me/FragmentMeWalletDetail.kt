package org.trustnote.wallet.biz.me

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import org.trustnote.wallet.R
import org.trustnote.wallet.TTT
import org.trustnote.wallet.biz.MainActivity
import org.trustnote.wallet.biz.home.FragmentMainWalletTxDetail
import org.trustnote.wallet.biz.wallet.*
import org.trustnote.wallet.util.AndroidUtils
import org.trustnote.wallet.util.TTTUtils
import org.trustnote.wallet.widget.RecyclerItemClickListener
import org.trustnote.wallet.widget.TMnAmount

class FragmentMeWalletDetail : FragmentWalletBase() {

    override fun getLayoutId(): Int {
        return R.layout.f_me_wallet_detail
    }

    //TODO: listen the wallet update event.

    private lateinit var totalBalanceView: TMnAmount
    private lateinit var credentialName: TextView
    private lateinit var walletId: TextView
    private lateinit var recyclerView: RecyclerView


    override fun initFragment(view: View) {

        super.initFragment(view)

        totalBalanceView = mRootView.findViewById<TMnAmount>(R.id.wallet_summary)

        walletId = mRootView.findViewById(R.id.credential_walletid)

        credentialName = mRootView.findViewById(R.id.credential_name)

        mRootView.findViewById<View>(R.id.credential_remove_btn).setOnClickListener {
        }

        recyclerView = mRootView.findViewById(R.id.list)
        recyclerView.layoutManager = LinearLayoutManager(activity)

    }

    override fun updateUI() {
        super.updateUI()
        walletId.text = TTTUtils.formatWalletId(credential.walletId)
        totalBalanceView.setMnAmount(credential.balance)
        credentialName.text = credential.walletName

        val a = SettingItem.getSettingForWalletDetail(credential, activity as MainActivity)
        val c = mutableListOf<SettingItem>()
        c.addAll(a)
        if (!credential.isObserveOnly) {
            val b = SettingItem.getSettingMoreForColdeWalletDetail(credential, activity as MainActivity)
            c.addAll(b)
        }

        recyclerView.adapter = SettingItemAdapter(c.toTypedArray())

    }

}

