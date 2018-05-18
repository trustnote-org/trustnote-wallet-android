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
import org.trustnote.db.TxType
import org.trustnote.wallet.TTT
import org.trustnote.wallet.biz.MainActivity
import org.trustnote.wallet.biz.init.CWFragmentBackup
import org.trustnote.wallet.biz.init.CreateWalletFragment
import org.trustnote.wallet.biz.wallet.TestData
import org.trustnote.wallet.util.Utils
import org.trustnote.wallet.widget.RecyclerItemClickListener


class FragmentMainWalletTxDetail : CreateWalletFragment(R.layout.f_main_wallet_tx_detail) {


    //TODO: listen the wallet update event.
    protected val disposables: CompositeDisposable = CompositeDisposable()

    override fun initFragment(view: View) {

        val credentialIndex = arguments.getInt(TTT.KEY_CREDENTIAL_INDEX, 0)
        val txIndex = arguments.getInt(TTT.KEY_TX_INDEX, 0)

        val tx = WalletManager.model.mProfile.credentials[credentialIndex].txDetails[txIndex]

        val amountView = mRootView.findViewById<TMnAmount>(R.id.wallet_summary)
        amountView.setupStyle(tx.txType)
        amountView.setMnAmount(tx.amount)

        val amountTitle = mRootView.findViewById<TextView>(R.id.wallet_summary_title)
        when (tx.txType) {
            TxType.received -> amountTitle.setText(R.string.tx_detial_receive_title)
            TxType.sent -> amountTitle.setText(R.string.tx_detial_sender_title)
        }
    }

}

