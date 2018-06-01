package org.trustnote.wallet.biz.me

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import org.trustnote.wallet.R
import org.trustnote.wallet.biz.TTT
import org.trustnote.wallet.biz.MainActivity
import org.trustnote.wallet.biz.home.CredentialAdapter
import org.trustnote.wallet.biz.wallet.FragmentWalletBase
import org.trustnote.wallet.biz.wallet.WalletManager
import org.trustnote.wallet.util.AndroidUtils

class FragmentMeWalletManager : FragmentWalletBase() {

    override fun getLayoutId(): Int {
        return R.layout.f_me_walleg_manager
    }

    lateinit var mRecyclerView: RecyclerView

    override fun initFragment(view: View) {
        super.initFragment(view)
        mRecyclerView = view.findViewById(R.id.wallet_list)
        mRecyclerView.layoutManager = LinearLayoutManager(activity)
    }

    override fun updateUI() {
        super.updateUI()

        val myAllWallets = WalletManager.model.getAvaiableWalletsForUser()

        val adapter = CredentialAdapter(myAllWallets, R.layout.item_wallet_manager)

        mRecyclerView.adapter = adapter

        AndroidUtils.addItemClickListenerForRecycleView(mRecyclerView) {

            val bundle = Bundle()
            val insideAdapter = mRecyclerView.adapter as CredentialAdapter
            bundle.putString(TTT.KEY_WALLET_ID, insideAdapter.myDataset[it].walletId)
            (activity as MainActivity).openLevel2Fragment(bundle, FragmentMeWalletDetail::class.java)

        }

    }
}

