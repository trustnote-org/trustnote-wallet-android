package org.trustnote.wallet.biz.home

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.trustnote.wallet.R
import org.trustnote.wallet.biz.wallet.WalletManager
import org.trustnote.wallet.uiframework.BaseActivity
import org.trustnote.wallet.uiframework.BaseFragment
import org.trustnote.wallet.util.AndroidUtils

class FragmentMainWallet : BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.f_main_wallet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateUI()
    }

    override fun onResume() {
        super.onResume()
        updateUI()
    }

    private fun updateUI() {
        val recyclerView = rootView.findViewById<RecyclerView>(R.id.credential_list)
        recyclerView.layoutManager = LinearLayoutManager(activity)

        val a = CredentialAdapter(WalletManager.model.mProfile.credentials.toTypedArray())

        recyclerView.adapter = a

        (activity as BaseActivity).supportActionBar?.title = AndroidUtils.getString(R.string.wallet_toolbar_title)

    }

    //TODO: save the adapter's state for restore.

}

