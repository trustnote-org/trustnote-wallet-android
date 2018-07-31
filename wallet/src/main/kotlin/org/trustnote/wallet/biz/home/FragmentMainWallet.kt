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
import org.trustnote.wallet.biz.wallet.FragmentWalletBaseForHomePage
import org.trustnote.wallet.biz.wallet.WalletManager
import org.trustnote.wallet.util.AndroidUtils
import org.trustnote.wallet.widget.TMnAmount
import android.widget.Toast
import android.R.id.button1
import android.support.v7.widget.PopupMenu
import org.trustnote.wallet.util.Utils
import android.support.v4.view.ViewCompat.setElevation
import android.os.Build
import android.widget.PopupWindow
import android.content.Context.LAYOUT_INFLATER_SERVICE
import android.view.LayoutInflater
import android.R.id.closeButton
import org.trustnote.wallet.util.CopyJavaCode
import android.view.Gravity
import org.trustnote.wallet.biz.ActivityMain

class FragmentMainWallet : FragmentWalletBaseForHomePage() {

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
        view.isClickable = true

        mRootView = view
        mToolbar = view.findViewById(R.id.toolbar)

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

        mToolbarTitle = findViewById(R.id.toolbar_title_left)
        mMNAmountToolbar = findViewById(R.id.toolbar_wallet_summary)
        mAmountTitleToolbar = findViewById(R.id.toolbar_wallet_summary_title)
        mMNAmountToolbarLayout = findViewById(R.id.toolbar_wallet_summary_layout)
        mMNAmountHeaderLayout = findViewById(R.id.amount_summary_layout)

        mMNAmountToolbarLayout.visibility = View.VISIBLE

        mMNAmountToolbarLayout.alpha = 0f
        mToolbarTitle.alpha = 1f

        mMNAmountToolbar.setupStyle(true)


        mAmountTitle.setText(R.string.wallet_amount_subtitle)


        icQuickAction = findViewById(R.id.ic_quick_action_container)

        if (icQuickAction != null) {
            icQuickAction!!.visibility = View.VISIBLE
            icQuickAction!!.setOnClickListener {
                (activity as ActivityMain).showPopupmenu{
                    startScan {
                        handleUnknownScanRes(it)
                    }
                }
            }
        }

    }

    fun setupQuickActionMenu(icQuickAction: View) {

        val inflater = activity.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val customView = inflater.inflate(R.layout.l_quick_action, null)

        val mPopupWindow = PopupWindow(
                customView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        )

        customView.setOnClickListener {
            fun onClick(view: View) {
                // Dismiss the popup window
                mPopupWindow.dismiss()
            }
        }
        // Set an elevation value for popup window
        // Call requires API level 21
        if (Build.VERSION.SDK_INT >= 21) {
            mPopupWindow.setElevation(5.0f)
        }

        val pos = CopyJavaCode.calculatePopWindowPos(icQuickAction, mRootView)
        mPopupWindow.showAtLocation(icQuickAction, Gravity.TOP or Gravity.START, pos[0], pos[1])

    }

    override fun inflateMenu(menu: Menu, inflater: MenuInflater) {
        //inflater.inflate(R.menu.main_action, menu)
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
            val f = FragmentMainWalletTxList()
            f.arguments = bundle
            addL2Fragment(f)

        }

        mSwipeRefreshLayout.isRefreshing = WalletManager.model.isRefreshing()

    }

}

