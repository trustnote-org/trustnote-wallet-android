package org.trustnote.wallet.biz.me

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import org.trustnote.wallet.R
import org.trustnote.wallet.TApp
import org.trustnote.wallet.biz.ActivityMain
import org.trustnote.wallet.biz.wallet.FragmentWalletBase
import org.trustnote.wallet.biz.wallet.WalletManager
import org.trustnote.wallet.uiframework.FragmentEditBase
import org.trustnote.wallet.util.AndroidUtils
import org.trustnote.wallet.util.TTTUtils
import org.trustnote.wallet.widget.FragmentDialogInputPwd
import org.trustnote.wallet.widget.MyDialogFragment
import org.trustnote.wallet.widget.TMnAmount

class FragmentMeWalletDetail : FragmentWalletBase() {

    override fun getLayoutId(): Int {
        return R.layout.f_me_wallet_detail
    }

    //TODO: listen the wallet update event.

    private lateinit var totalBalanceView: TMnAmount
    private lateinit var credentialName: TextView
    private lateinit var walletId: TextView
    private lateinit var recyclerView: RecyclerView

    override fun initFragment(view: View) {

        super.initFragment(view)

        totalBalanceView = mRootView.findViewById<TMnAmount>(R.id.wallet_summary)

        walletId = mRootView.findViewById(R.id.credential_walletid)

        credentialName = mRootView.findViewById(R.id.credential_name)

        mRootView.findViewById<View>(R.id.credential_remove_btn).setOnClickListener {
            removeWallet()
        }

        recyclerView = mRootView.findViewById(R.id.list)
        recyclerView.layoutManager = LinearLayoutManager(activity)

    }

    private fun removeWallet() {

        if (!WalletManager.model.canRemove(credential)) {
            MyDialogFragment.showMsg(getMyActivity(), R.string.me_wallet_remove_wallet_deny)
            return
        }

        val f = FragmentDialogMeRemoveWallet {

            val f = FragmentDialogInputPwd()
            f.confirmLogic = {
                val removeResult = WalletManager.model.removeWallet(credential)
                if (removeResult) {
                    activity.onBackPressed()
                } else {
                    MyDialogFragment.showMsg(getMyActivity(), R.string.me_wallet_remove_wallet_deny)
                }
            }

            addL2Fragment(f)

        }
        addL2Fragment(f)

//        val f = FragmentDialogInputPwd()
//        f.confirmLogic = {
//            val f = FragmentDialogMeRemoveWallet {
//                val removeResult = WalletManager.model.removeWallet(credential)
//                if (removeResult) {
//                    activity.onBackPressed()
//                } else {
//                    MyDialogFragment.showMsg(getMyActivity(), R.string.me_wallet_remove_wallet_deny)
//                }
//            }
//
//            addL2Fragment(f)
//
//        }
//        addL2Fragment(f)
//
    }

        override fun updateUI() {
            super.updateUI()
            walletId.text = TTTUtils.formatWalletId(credential.walletId)
            totalBalanceView.setMnAmount(credential.balance)
            totalBalanceView.removeMarginBottom()
            credentialName.text = credential.walletName

            val a = SettingItem.getSettingForWalletDetail(credential, activity as ActivityMain)
            val c = mutableListOf<SettingItem>()
            c.addAll(a)
            //        if (!credential.isObserveOnly) {
            //            val b = SettingItem.getSettingMoreForColdeWalletDetail(credential, activity as ActivityMain)
            //            c.addAll(b)
            //        }

            //TODO: iff UI changes
            c[1].lambda = { editWalletname() }

            recyclerView.adapter = SettingItemAdapter(c.toTypedArray())

        }

        private fun editWalletname() {
            val f = FragmentEditBase()
            f.buildPage(credential.walletName,
                    activity.getString(R.string.wallet_name_err),
                    {
                        it.length <= 10
                    },
                    {
                        WalletManager.model.udpateCredentialName(credential, it)
                    }, activity.getString(R.string.me_wallet_detail_name_title))
            addL2Fragment(f)
        }

    }

