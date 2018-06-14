package org.trustnote.wallet.widget

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import org.trustnote.wallet.R
import org.trustnote.wallet.biz.ActivityMain
import org.trustnote.wallet.biz.FragmentPageBase
import org.trustnote.wallet.biz.TTT
import org.trustnote.wallet.biz.home.CredentialAdapter
import org.trustnote.wallet.biz.wallet.FragmentWalletTransfer
import org.trustnote.wallet.biz.wallet.WalletManager
import org.trustnote.wallet.util.AndroidUtils

class FragmentDialogSelectWallet : FragmentPageBase() {

    var msg: String = "TTT Welcome"
    var confirmLogic: (String) -> Unit = {}
    lateinit var list: RecyclerView

    override fun getLayoutId(): Int {
        return R.layout.l_dialog_select_wallet
    }

    override fun initFragment(view: View) {

        list = view.findViewById(R.id.list)
        list.layoutManager = LinearLayoutManager(activity)


    }

    override fun updateUI() {
        super.updateUI()
        val myAllWallets = WalletManager.model.getAvaiableWalletsForUser()
        val adapter = CredentialAdapter(myAllWallets)
        list.adapter = adapter

        AndroidUtils.addItemClickListenerForRecycleView(list) {
            val walletId = (list.adapter as CredentialAdapter).myDataset[it].walletId

            //TODO: move qrCode logic somewhere.

            val f = FragmentWalletTransfer()
            AndroidUtils.addFragmentArguments(f, TTT.KEY_WALLET_ID, walletId)
            AndroidUtils.addFragmentArguments(f, TTT.KEY_TRANSFER_QRCODEW, arguments.getString(TTT.KEY_TRANSFER_QRCODEW))
            (activity as ActivityMain).addL2Fragment(f)

        }

    }

}