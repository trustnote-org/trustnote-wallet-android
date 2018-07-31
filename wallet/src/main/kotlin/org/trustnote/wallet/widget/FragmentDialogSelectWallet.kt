package org.trustnote.wallet.widget

import android.content.Context
import android.graphics.Canvas
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import org.trustnote.wallet.R
import org.trustnote.wallet.biz.ActivityMain
import org.trustnote.wallet.biz.FragmentPageBase
import org.trustnote.wallet.biz.TTT
import org.trustnote.wallet.biz.home.CredentialAdapter
import org.trustnote.wallet.biz.home.CredentialSelectAdapter
import org.trustnote.wallet.biz.wallet.FragmentWalletTransfer
import org.trustnote.wallet.biz.wallet.WalletManager
import org.trustnote.wallet.util.AndroidUtils
import android.graphics.drawable.Drawable



class FragmentDialogSelectWallet : FragmentPageBase() {

    var msg: String = "TTT Welcome"
    var confirmLogic: (String) -> Unit = {}

    lateinit var list: RecyclerView
    lateinit var header: PageHeader
    override fun getLayoutId(): Int {
        return R.layout.l_dialog_select_wallet
    }

    override fun initFragment(view: View) {

        list = view.findViewById(R.id.list)
        list.layoutManager = LinearLayoutManager(activity)

        list.addItemDecoration(SimpleDividerItemDecoration(activity))

        header = findViewById(R.id.title)
        header.closeAction = {
            onBackPressed()
        }

    }

    override fun updateUI() {
        super.updateUI()
        val myAllWallets = WalletManager.model.getAvaiableWalletsForUser()
        val adapter = CredentialSelectAdapter(myAllWallets)
        list.adapter = adapter

        AndroidUtils.addItemClickListenerForRecycleView(list) {

            removeMeFromBackStack()

            val walletId = (list.adapter as CredentialSelectAdapter).myDataset[it].walletId

            val f = FragmentWalletTransfer()
            AndroidUtils.addFragmentArguments(f, TTT.KEY_WALLET_ID, walletId)
            AndroidUtils.addFragmentArguments(f, TTT.KEY_TRANSFER_QRCODEW, arguments.getString(TTT.KEY_TRANSFER_QRCODEW))
            (activity as ActivityMain).addL2Fragment(f)

        }

        mToolbar.visibility = View.INVISIBLE

    }

    override fun setupToolbar() {
        super.setupToolbar()
        mToolbar.visibility = View.INVISIBLE
    }

    inner class SimpleDividerItemDecoration(context: Context) : RecyclerView.ItemDecoration() {
        private val mDivider: Drawable

        init {
            mDivider = context.getResources().getDrawable(R.drawable.line_divider_select_wallet)
        }

        override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
            val left = parent.paddingLeft
            val right = parent.width - parent.paddingRight

            val childCount = parent.childCount
            for (i in 0 until childCount) {
                val child = parent.getChildAt(i)

                val params = child.layoutParams as RecyclerView.LayoutParams

                val top = child.bottom + params.bottomMargin
                val bottom = top + mDivider.intrinsicHeight

                mDivider.setBounds(left, top, right, bottom)
                mDivider.draw(c)
            }
        }
    }
}