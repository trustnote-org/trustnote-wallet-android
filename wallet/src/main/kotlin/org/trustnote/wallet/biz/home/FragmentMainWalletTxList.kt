package org.trustnote.wallet.biz.home

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.widget.TextView
import org.trustnote.wallet.R
import org.trustnote.wallet.TTT
import org.trustnote.wallet.biz.MainActivity
import org.trustnote.wallet.biz.wallet.WalletManager
import org.trustnote.wallet.widget.RecyclerItemClickListener
import org.trustnote.wallet.widget.TMnAmount

class FragmentMainWalletTxList : FragmentMainBase() {

    override fun getLayoutId(): Int {
        return R.layout.f_main_wallet_tx_list
    }

    //TODO: listen the wallet update event.

    override fun initFragment(view: View) {


        super.initFragment(view)

        val credentialIndex = arguments.getInt("CREDENTIAL_INDEX", 0)

        val totalBalanceView = mRootView.findViewById<TMnAmount>(R.id.wallet_summary)
        totalBalanceView.setMnAmount(WalletManager.model.mProfile.credentials[credentialIndex].balance)

        mRootView.findViewById<TextView>(R.id.credential_name).text = (WalletManager.model.mProfile.credentials[credentialIndex].walletName)

        val recyclerView = mRootView.findViewById<RecyclerView>(R.id.tx_list)
        recyclerView.layoutManager = LinearLayoutManager(activity)

        val a = TxAdapter(WalletManager.model.mProfile.credentials[credentialIndex].txDetails.toTypedArray())

        recyclerView.adapter = a

        recyclerView.addOnItemTouchListener(
                RecyclerItemClickListener(context, recyclerView, object : RecyclerItemClickListener.OnItemClickListener {
                    override fun onItemClick(view: View, position: Int) {
                        val bundle = Bundle()
                        bundle.putInt(TTT.KEY_CREDENTIAL_INDEX, credentialIndex)
                        bundle.putInt(TTT.KEY_TX_INDEX, position)
                        (activity as MainActivity).openLevel2Fragment(bundle, FragmentMainWalletTxDetail::class.java)
                    }

                    override fun onLongItemClick(view: View, position: Int) {
                    }
                })
        )

    }


}

