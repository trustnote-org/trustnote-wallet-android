package org.trustnote.wallet.biz.home

import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import org.trustnote.wallet.R
import org.trustnote.wallet.biz.TTT
import org.trustnote.wallet.biz.ActivityMain
import org.trustnote.wallet.biz.wallet.*
import org.trustnote.wallet.util.AndroidUtils
import org.trustnote.wallet.widget.TMnAmount

class FragmentMainWalletTxList : FragmentWalletBase() {

    override fun getLayoutId(): Int {
        return R.layout.f_main_wallet_tx_list
    }

    //TODO: listen the wallet update event.

    private lateinit var totalBalanceView: TMnAmount
    private lateinit var credentialName: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var mSwipeRefreshLayout: SwipeRefreshLayout

    override fun initFragment(view: View) {

        super.initFragment(view)

        totalBalanceView = mRootView.findViewById<TMnAmount>(R.id.wallet_summary)

        credentialName = mRootView.findViewById(R.id.credential_name)

        recyclerView = mRootView.findViewById(R.id.tx_list)

        AndroidUtils.addItemClickListenerForRecycleView(recyclerView) {
            val bundle = Bundle()
            bundle.putString(TTT.KEY_WALLET_ID, credential.walletId)
            bundle.putInt(TTT.KEY_TX_INDEX, it)
            (activity as ActivityMain).openLevel2Fragment(bundle,
                    FragmentMainWalletTxDetail::class.java)
        }

        mRootView.findViewById<View>(R.id.btn_receive).setOnClickListener {
            getMyActivity().receiveAmount = 0L
            val f = FragmentWalletReceive()
            (activity as ActivityMain).openPage(f, TTT.KEY_WALLET_ID, credential.walletId)
        }

        mRootView.findViewById<View>(R.id.btn_transfer).setOnClickListener {
            val f = FragmentWalletTransfer()
            AndroidUtils.addFragmentArguments(f, TTT.KEY_WALLET_ID, credential.walletId)
            (activity as ActivityMain).openLevel2Fragment(f)
        }


        mSwipeRefreshLayout = mRootView.findViewById(R.id.swiperefresh)

        mSwipeRefreshLayout.setProgressViewOffset(true, -60, 40)
        mSwipeRefreshLayout.setOnRefreshListener {
            WalletManager.model.refreshOneWallet(credential.walletId)
        }

    }

    override fun updateUI() {
        super.updateUI()
        totalBalanceView.setMnAmount(credential.balance)
        credentialName.text = credential.walletName

        recyclerView.layoutManager = LinearLayoutManager(activity)

        val a = TxAdapter(credential.txDetails.toTypedArray())

        recyclerView.adapter = a

        mSwipeRefreshLayout.isRefreshing = WalletManager.model.isRefreshing()

    }

}

