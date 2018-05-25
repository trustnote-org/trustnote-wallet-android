package org.trustnote.wallet.biz.home

import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import org.trustnote.wallet.R
import org.trustnote.wallet.TTT
import org.trustnote.wallet.biz.MainActivity
import org.trustnote.wallet.biz.wallet.WalletManager
import org.trustnote.wallet.widget.RecyclerItemClickListener
import org.trustnote.wallet.widget.TMnAmount

class FragmentMainWallet : FragmentMainBase() {

    override fun getLayoutId(): Int {
        return R.layout.f_main_wallet
    }

    override fun setupToolbar() {

    }

    lateinit var mRecyclerView: RecyclerView
    lateinit var mSwipeRefreshLayout: SwipeRefreshLayout
    lateinit var mTotalBalanceView: TMnAmount

    override fun initFragment(view: View) {
        super.initFragment(view)

        mTotalBalanceView = mRootView.findViewById(R.id.wallet_summary)

        mRecyclerView = mRootView.findViewById(R.id.credential_list)
        mRecyclerView.layoutManager = LinearLayoutManager(activity)

        mSwipeRefreshLayout = mRootView.findViewById(R.id.swiperefresh)

        mSwipeRefreshLayout.setOnRefreshListener(
                SwipeRefreshLayout.OnRefreshListener {
                    WalletManager.model.restoreBg()
                }
        )

    }

    override fun updateUI() {

        if (!WalletManager.model.profileExist()) {
            return
        }

        val myAllWallets = WalletManager.model.mProfile.credentials.filter { it.balance > 0 || it.isObserveOnly }
        val adapter = CredentialAdapter(myAllWallets.toTypedArray())
        mRecyclerView.adapter = adapter

        mRecyclerView.addOnItemTouchListener(
                RecyclerItemClickListener(context, mRecyclerView, object : RecyclerItemClickListener.OnItemClickListener {
                    override fun onItemClick(view: View, position: Int) {
                        val bundle = Bundle()
                        bundle.putInt(TTT.KEY_CREDENTIAL_INDEX, position)
                        (activity as MainActivity).openLevel2Fragment(bundle, FragmentMainWalletTxList::class.java)
                    }

                    override fun onLongItemClick(view: View, position: Int) {

                    }
                })
        )

        mTotalBalanceView.setMnAmount(WalletManager.model.mProfile.balance)

        mSwipeRefreshLayout.isRefreshing = WalletManager.model.isRefreshing()

    }

}

