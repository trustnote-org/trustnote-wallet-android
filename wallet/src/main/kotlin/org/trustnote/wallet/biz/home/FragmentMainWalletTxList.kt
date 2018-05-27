package org.trustnote.wallet.biz.home

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import org.trustnote.wallet.R
import org.trustnote.wallet.TTT
import org.trustnote.wallet.biz.MainActivity
import org.trustnote.wallet.biz.wallet.Credential
import org.trustnote.wallet.biz.wallet.FragmentWalletReceive
import org.trustnote.wallet.biz.wallet.FragmentWalletTransfer
import org.trustnote.wallet.biz.wallet.WalletManager
import org.trustnote.wallet.widget.RecyclerItemClickListener
import org.trustnote.wallet.widget.TMnAmount

class FragmentMainWalletTxList : FragmentMainBase() {

    override fun getLayoutId(): Int {
        return R.layout.f_main_wallet_tx_list
    }

    //TODO: listen the wallet update event.
    lateinit var credential: Credential

    override fun initFragment(view: View) {

        super.initFragment(view)

        val walletId = arguments.getString(TTT.KEY_WALLET_ID)
        credential = WalletManager.model.findWallet(walletId)

        val totalBalanceView = mRootView.findViewById<TMnAmount>(R.id.wallet_summary)
        totalBalanceView.setMnAmount(credential.balance)

        mRootView.findViewById<TextView>(R.id.credential_name).text = (credential.walletName)

        val recyclerView = mRootView.findViewById<RecyclerView>(R.id.tx_list)
        recyclerView.layoutManager = LinearLayoutManager(activity)

        val a = TxAdapter(credential.txDetails.toTypedArray())

        recyclerView.adapter = a

        recyclerView.addOnItemTouchListener(
                RecyclerItemClickListener(context,
                        recyclerView,
                        object : RecyclerItemClickListener.OnItemClickListener {
                            override fun onItemClick(view: View, position: Int) {
                                val bundle = Bundle()
                                bundle.putString(TTT.KEY_WALLET_ID, credential.walletId)
                                bundle.putInt(TTT.KEY_TX_INDEX, position)
                                (activity as MainActivity).openLevel2Fragment(bundle,
                                        FragmentMainWalletTxDetail::class.java)
                            }

                            override fun onLongItemClick(view: View, position: Int) {
                            }
                        })
        )

        mRootView.findViewById<View>(R.id.btn_receive).setOnClickListener {
            getMyActivity().receiveAmount = 0L
            val f = FragmentWalletReceive()
            (activity as MainActivity).openPage(f, TTT.KEY_WALLET_ID, credential.walletId)
        }

        mRootView.findViewById<View>(R.id.btn_transfer).setOnClickListener {
            val f = FragmentWalletTransfer()
            (activity as MainActivity).openLevel2Fragment(f)
        }

    }

}

