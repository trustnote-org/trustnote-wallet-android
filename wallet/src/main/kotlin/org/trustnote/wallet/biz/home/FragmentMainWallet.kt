package org.trustnote.wallet.biz.home

import android.content.Context
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.AppBarLayout.ScrollingViewBehavior
import android.support.design.widget.CoordinatorLayout
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import org.trustnote.wallet.R
import org.trustnote.wallet.TTT
import org.trustnote.wallet.biz.MainActivity
import org.trustnote.wallet.biz.wallet.WalletManager
import org.trustnote.wallet.util.AndroidUtils
import org.trustnote.wallet.util.Utils
import org.trustnote.wallet.widget.RecyclerItemClickListener
import org.trustnote.wallet.widget.TMnAmount

class FragmentMainWallet : FragmentMainBase() {

    override fun getLayoutId(): Int {
        return R.layout.f_main_wallet
    }

    override fun setupToolbar() {

    }

    lateinit var mRecyclerView: RecyclerView
    lateinit var mSwipeRefreshLayout: SwipeRefreshLayout
    lateinit var mAppBarLayout: AppBarLayout
    lateinit var mMNAmount: TMnAmount
    lateinit var mAmountTitle: TextView
    lateinit var mToolbarTitle: TextView
    lateinit var mToolbar: Toolbar

    lateinit var mMNAmountToolbar: TMnAmount
    lateinit var mAmountTitleToolbar: TextView

    var mAmountTitleTargetDistance = 0F
    var mAmountTargetDistanceY = 0F
    var mAmountTargetDistanceX = 0F

    override fun initFragment(view: View) {
        super.initFragment(view)

        mRecyclerView = mRootView.findViewById(R.id.credential_list)
        mRecyclerView.layoutManager = LinearLayoutManager(activity)

        mSwipeRefreshLayout = mRootView.findViewById(R.id.swiperefresh)

        mSwipeRefreshLayout.setProgressViewOffset(true, -60, 40)
        //mSwipeRefreshLayout.setColorSchemeColors(Color.GRAY, Color.GRAY, Color.GRAY, Color.GRAY)

        mSwipeRefreshLayout.setOnRefreshListener(
                SwipeRefreshLayout.OnRefreshListener {
                    WalletManager.model.fullRefreshing()
                }
        )


        mAppBarLayout = mRootView.findViewById(R.id.main_appbar)
        mAppBarLayout.addOnOffsetChangedListener({ _, verticalOffset ->

            mSwipeRefreshLayout.isEnabled = verticalOffset >= 0

            doAnimation(verticalOffset.toFloat())
            //            Utils.debugLog("addOnOffsetChangedListener::${verticalOffset}")
            //            Utils.debugLog("mAmountTitle.y::${mToolbarTitle.y}")
            //
            //            Utils.debugLog("mAmountTitle.y::${mAmountTitle.y}")
            //            Utils.debugLog("mAmountTitle.y::${mAmountTitle.translationY}")

        })

        mMNAmount = findViewById(R.id.wallet_summary)
        mAmountTitle = findViewById(R.id.wallet_summary_title)

        mToolbarTitle = findViewById(R.id.wallet_toolbar_title)
        mMNAmountToolbar = findViewById(R.id.toolbar_wallet_summary)
        mAmountTitleToolbar = findViewById(R.id.toolbar_wallet_summary_title)

        mMNAmountToolbar.alpha = 0f
        mAmountTitleToolbar.alpha = 0f
        mToolbarTitle.alpha = 1f

        mMNAmountToolbar.setupStyle(true)

        mToolbar = findViewById(R.id.toolbar)

        getMyActivity().setupToolbar(mToolbar)

    }

    private fun doAnimation(offset: Float) {
        if (offset > 0) {
            return
        }

        val targetDistance = mAppBarLayout.height - mToolbar.height
        val currentRatio = Math.abs(offset) / targetDistance

        mToolbarTitle.alpha = 1 - currentRatio
        mMNAmountToolbar.alpha = currentRatio
        mAmountTitleToolbar.alpha = currentRatio


        mMNAmount.alpha = (1 - currentRatio)
        mAmountTitle.alpha = (1 - currentRatio) * (1 - currentRatio)

//        val expectedAmountTitleDistance = mAmountTitleTargetDistance * (1 - currentRatio)
//        val actualAmountTitleDistance = mToolbarTitle.y - mAmountTitle.y
//        mAmountTitle.translationY -= (expectedAmountTitleDistance - actualAmountTitleDistance)
//
//        val expectedAmountDistance = mAmountTargetDistanceY * (1 - currentRatio)
//        val actualAmountDistance = mToolbarTitle.y - mMNAmount.y
//        mMNAmount.translationY -= (expectedAmountDistance - actualAmountDistance)
//
//
//        val expectedAmountDistance = mAmountTargetDistanceY * (1 - currentRatio)
//        val actualAmountDistance = mToolbarTitle.y - mMNAmount.y
//        mMNAmount.translationY -= (expectedAmountDistance - actualAmountDistance)

        //mAmountTitle.translationY = -100 * currentRatio

//        mAmountTitle.remove
//        val location = IntArray(2)
//        findViewById<View>(R.id.wallet_summary_title).getLocationInWindow(location)
//
//        mAmountTitle.top = location[1]

    }

    override fun updateUI() {

        super.updateUI()
        if (!WalletManager.model.profileExist()) {
            return
        }

        mMNAmount.setMnAmount(WalletManager.model.mProfile.balance)
        mMNAmountToolbar.setMnAmount(WalletManager.model.mProfile.balance)

        mAmountTitleTargetDistance = mToolbarTitle.y - mAmountTitle.y
        mAmountTargetDistanceY = mToolbarTitle.y - mMNAmount.y
        mAmountTargetDistanceX = mAmountTitle.x + mAmountTitle.width + -mMNAmount.x

        val myAllWallets = WalletManager.model.mProfile.credentials.filter { (it.account == 0 && !it.isObserveOnly) || !it.isAuto || it.balance > 0 || it.isObserveOnly }

        val adapter = CredentialAdapter(myAllWallets)
        mRecyclerView.adapter = adapter

        AndroidUtils.addItemClickListenerForRecycleView(mRecyclerView) {

            val bundle = Bundle()
            val insideAdapter = mRecyclerView.adapter as CredentialAdapter
            bundle.putString(TTT.KEY_WALLET_ID, insideAdapter.myDataset[it].walletId)
            (activity as MainActivity).openLevel2Fragment(bundle, FragmentMainWalletTxList::class.java)

        }


        mSwipeRefreshLayout.isRefreshing = WalletManager.model.isRefreshing()

    }

}

class MyScrollingViewBehavior(val context: Context, val attrs: AttributeSet) : ScrollingViewBehavior(context, attrs) {

    override fun setTopAndBottomOffset(offset: Int): Boolean {
        Utils.debugLog("setTopAndBottomOffset::$offset")
        return super.setTopAndBottomOffset(offset)
    }

    override fun onDependentViewChanged(parent: CoordinatorLayout?, child: View?,
                                        dependency: View?): Boolean {
        if (dependency!!.getY() < -250) {
            return false

        }
        Utils.debugLog("onDependentViewChanged::${child!!.getY()}")
        Utils.debugLog("onDependentViewChanged::${dependency!!.getY()}")
        Utils.debugLog("onDependentViewChanged::${dependency!!.top}")
        return super.onDependentViewChanged(parent, child, dependency)
    }

}

