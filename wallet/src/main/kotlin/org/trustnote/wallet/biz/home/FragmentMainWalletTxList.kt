package org.trustnote.wallet.biz.home

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import org.trustnote.wallet.R
import org.trustnote.wallet.biz.wallet.WalletManager
import org.trustnote.wallet.uiframework.BaseActivity
import org.trustnote.wallet.uiframework.BaseFragment
import org.trustnote.wallet.util.AndroidUtils
import org.trustnote.wallet.widget.TMnAmount
import android.widget.AdapterView.OnItemClickListener
import android.widget.Button
import android.widget.TextView
import org.trustnote.wallet.TTT
import org.trustnote.wallet.biz.MainActivity
import org.trustnote.wallet.biz.init.CWFragmentBackup
import org.trustnote.wallet.biz.init.CreateWalletFragment
import org.trustnote.wallet.biz.wallet.TestData
import org.trustnote.wallet.util.Utils
import org.trustnote.wallet.widget.RecyclerItemClickListener


class FragmentMainWalletTxList : CreateWalletFragment(R.layout.f_main_wallet_tx_list) {


    //TODO: listen the wallet update event.
    protected val disposables: CompositeDisposable = CompositeDisposable()


    override fun initFragment(view: View) {

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

