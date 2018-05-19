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
import org.trustnote.wallet.TTT
import org.trustnote.wallet.biz.wallet.WalletManager
import org.trustnote.wallet.uiframework.BaseActivity
import org.trustnote.wallet.uiframework.BaseFragment
import org.trustnote.wallet.util.AndroidUtils
import org.trustnote.wallet.widget.TMnAmount
import org.trustnote.wallet.biz.MainActivity
import org.trustnote.wallet.widget.RecyclerItemClickListener


class FragmentMainWallet : BaseFragment() {

    override fun getLayoutId(): Int {
        return R.layout.f_main_wallet
    }

    protected val disposables: CompositeDisposable = CompositeDisposable()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.f_main_wallet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateUI()
    }

    override fun onResume() {
        super.onResume()

        val d = WalletManager.model.mSubject.observeOn(AndroidSchedulers.mainThread()).subscribe {
            updateUI()
        }
        disposables.add(d)
        updateUI()
    }

    override fun onPause() {
        super.onPause()
        disposables.clear()
    }

    private fun updateUI() {

        if (!WalletManager.model.profileExist()) {
            return
        }

        val recyclerView = mRootView.findViewById<RecyclerView>(R.id.credential_list)
        recyclerView.layoutManager = LinearLayoutManager(activity)

        val a = CredentialAdapter(WalletManager.model.mProfile.credentials.toTypedArray())

        recyclerView.adapter = a

        recyclerView.addOnItemTouchListener(
                RecyclerItemClickListener(context, recyclerView, object : RecyclerItemClickListener.OnItemClickListener {
                    override fun onItemClick(view: View, position: Int) {
                        val bundle = Bundle()
                        bundle.putInt(TTT.KEY_CREDENTIAL_INDEX, position)
                        (activity as MainActivity).openLevel2Fragment(bundle, FragmentMainWalletTxList::class.java)
                    }

                    override fun onLongItemClick(view: View, position: Int) {
                    }
                })
        )


        (activity as BaseActivity).supportActionBar?.title = AndroidUtils.getString(R.string.wallet_toolbar_title)

        val totalBalanceView = mRootView.findViewById<TMnAmount>(R.id.wallet_summary)
        totalBalanceView.setMnAmount(WalletManager.model.mProfile.balance)

    }

}

