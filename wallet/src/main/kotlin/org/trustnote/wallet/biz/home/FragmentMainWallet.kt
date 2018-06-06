package org.trustnote.wallet.biz.home

import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.TextView
import org.trustnote.wallet.R
import org.trustnote.wallet.TApp
import org.trustnote.wallet.biz.TTT
import org.trustnote.wallet.biz.ActivityMain
import org.trustnote.wallet.biz.wallet.FragmentWalletBase
import org.trustnote.wallet.biz.wallet.WalletManager
import org.trustnote.wallet.util.AndroidUtils
import org.trustnote.wallet.widget.TMnAmount

class FragmentMainWallet : FragmentWalletBase() {

    override fun getLayoutId(): Int {
        return R.layout.f_main_wallet
    }

    lateinit var mRecyclerView: RecyclerView
    lateinit var mSwipeRefreshLayout: SwipeRefreshLayout
    lateinit var mAppBarLayout: AppBarLayout
    lateinit var mMNAmount: TMnAmount
    lateinit var mAmountTitle: TextView
    lateinit var mToolbarTitle: TextView

    lateinit var mMNAmountToolbar: TMnAmount
    lateinit var mAmountTitleToolbar: TextView
    lateinit var mMNAmountToolbarLayout: View
    lateinit var mMNAmountHeaderLayout: View

    var currentRatio = 0f
    var totalOffsetDistance = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(getLayoutId(), container, false)
        mRootView = view
        view.isClickable = true
        return view
    }

    override fun getTitle(): String {
        return TApp.context.getString(R.string.wallet_toolbar_title)
    }

    override fun initFragment(view: View) {

        isBottomLayerUI = true

        super.initFragment(view)

        mRecyclerView = mRootView.findViewById(R.id.credential_list)
        mRecyclerView.layoutManager = LinearLayoutManager(activity)

        mSwipeRefreshLayout = mRootView.findViewById(R.id.swiperefresh)

        mSwipeRefreshLayout.setProgressViewOffset(true, -60, 40)

        mSwipeRefreshLayout.setOnRefreshListener {
            WalletManager.model.refreshExistWallet()
        }

        mAppBarLayout = mRootView.findViewById(R.id.main_appbar)
        mAppBarLayout.addOnOffsetChangedListener({ _, verticalOffset ->

            mSwipeRefreshLayout.isEnabled = verticalOffset >= 0

            doAnimation(verticalOffset.toFloat())

        })

        mMNAmount = findViewById(R.id.wallet_summary)
        mAmountTitle = findViewById(R.id.wallet_summary_title)

        mToolbarTitle = findViewById(R.id.toolbar_title)
        mMNAmountToolbar = findViewById(R.id.toolbar_wallet_summary)
        mAmountTitleToolbar = findViewById(R.id.toolbar_wallet_summary_title)
        mMNAmountToolbarLayout = findViewById(R.id.toolbar_wallet_summary_layout)
        mMNAmountHeaderLayout = findViewById(R.id.amount_summary_layout)

        mMNAmountToolbarLayout.visibility = View.VISIBLE

        mMNAmountToolbarLayout.alpha = 0f
        mToolbarTitle.alpha = 1f

        mMNAmountToolbar.setupStyle(true)

    }

    override fun inflateMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_action, menu)
    }

    private fun doAnimation(offset: Float) {
        if (offset > 0) {
            return
        }

        totalOffsetDistance = mAppBarLayout.height - mToolbar.height

        currentRatio = Math.abs(offset) / totalOffsetDistance

        mToolbarTitle.alpha = 1 - currentRatio

        mMNAmountToolbarLayout.alpha = currentRatio

        mMNAmountHeaderLayout.alpha = (1 - currentRatio) * (1 - currentRatio)

        //        val expectedAmountTitleDistance = mAmountTitleTargetDistance * (1 - currentRatio)
        //        val actualAmountTitleDistance = mToolbarTitle.y - mAmountTitle.y
        //        mAmountTitle.translationY -= (expectedAmountTitleDistance - actualAmountTitleDistance)

    }

    override fun updateUI() {

        super.updateUI()
        if (!WalletManager.model.profileExist()) {
            return
        }

        mMNAmount.setMnAmount(WalletManager.model.mProfile.balance)
        mMNAmountToolbar.setMnAmount(WalletManager.model.mProfile.balance)

        val myAllWallets = WalletManager.model.getAvaiableWalletsForUser()

        val adapter = CredentialAdapter(myAllWallets)
        mRecyclerView.adapter = adapter

        AndroidUtils.addItemClickListenerForRecycleView(mRecyclerView) {

            val bundle = Bundle()
            val insideAdapter = mRecyclerView.adapter as CredentialAdapter
            bundle.putString(TTT.KEY_WALLET_ID, insideAdapter.myDataset[it].walletId)
            (activity as ActivityMain).openLevel2Fragment(bundle, FragmentMainWalletTxList::class.java)

        }

        mSwipeRefreshLayout.isRefreshing = WalletManager.model.isRefreshing()

    }

}

