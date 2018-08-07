package org.trustnote.wallet.biz.home

import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import org.trustnote.db.TxType
import org.trustnote.wallet.R
import org.trustnote.wallet.biz.TTT
import org.trustnote.wallet.biz.ActivityMain
import org.trustnote.wallet.biz.msgs.chatWithFriend
import org.trustnote.wallet.biz.wallet.*
import org.trustnote.wallet.uiframework.ActivityBase
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
    private lateinit var receiveBtnView: View
    private lateinit var walletSummaryTitle: TextView

    override fun initFragment(view: View) {

        super.initFragment(view)

        totalBalanceView = mRootView.findViewById<TMnAmount>(R.id.wallet_summary)
        totalBalanceView.setupForTxListHeader()

        credentialName = mRootView.findViewById(R.id.credential_name)

        recyclerView = mRootView.findViewById(R.id.tx_list)

        receiveBtnView = mRootView.findViewById<View>(R.id.btn_receive)

        receiveBtnView.setOnClickListener {
            val f = FragmentWalletReceive()
            f.mnAmount = 0L
            AndroidUtils.addFragmentArguments(f, TTT.KEY_WALLET_ID, credential.walletId)
            addL2Fragment(f)
        }

        mRootView.findViewById<View>(R.id.btn_transfer).setOnClickListener {
            val f = FragmentWalletTransfer()
            AndroidUtils.addFragmentArguments(f, TTT.KEY_WALLET_ID, credential.walletId)
            (activity as ActivityMain).addL2Fragment(f)
        }


        mSwipeRefreshLayout = mRootView.findViewById(R.id.swiperefresh)

        mSwipeRefreshLayout.setProgressViewOffset(true, -60, 40)
        mSwipeRefreshLayout.setOnRefreshListener {
            WalletManager.model.refreshOneWallet(credential.walletId)
        }

        walletSummaryTitle = findViewById(R.id.wallet_summary_title)

        walletSummaryTitle.setText(R.string.wallet_amount_subtitle)

    }

    override fun updateUI() {
        super.updateUI()

        receiveBtnView.isEnabled = credential.myReceiveAddresses.isNotEmpty()

        totalBalanceView.setMnAmount(credential.balance)
        credentialName.text = credential.walletName

        recyclerView.layoutManager = LinearLayoutManager(activity)

        val a = TxAdapter(credential.txDetails.filter {
            TxType.moved != it.txType
        })

        a.itemClickListener = {index, _ ->
            val bundle = Bundle()
            bundle.putString(TTT.KEY_WALLET_ID, credential.walletId)
            bundle.putInt(TTT.KEY_TX_INDEX, index)
            val f = FragmentMainWalletTxDetail()
            f.arguments = bundle
            addL2Fragment(f)
        }

        recyclerView.adapter = a

        mSwipeRefreshLayout.isRefreshing = WalletManager.model.isRefreshing()


        mRootView.findViewById<View>(R.id.btn_receive)
    }

}

