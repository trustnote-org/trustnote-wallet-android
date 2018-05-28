package org.trustnote.wallet.biz

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.widget.FrameLayout
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import org.trustnote.wallet.R
import org.trustnote.wallet.TApp
import org.trustnote.wallet.biz.MainActivity
import org.trustnote.wallet.biz.wallet.WalletManager
import org.trustnote.wallet.uiframework.FragmentBase

abstract class FragmentPageBase: FragmentBase() {



    fun getMyActivity(): MainActivity {
        return activity as MainActivity
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.l_page_bg, container, false)
        mRootView = inflater.inflate(getLayoutId(), null)
        view.findViewById<FrameLayout>(R.id.dialog_frame).addView(mRootView)
        return view
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        menu!!.clear() //Empty the old menu
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun setupToolbar() {

        setHasOptionsMenu(true)

        getMyActivity().getSupportActionBar()!!.setDisplayShowTitleEnabled(false);

        //getMyActivity().getSupportActionBar()!!.closeOptionsMenu()
        //getMyActivity().supportActionBar!!.setHomeAsUpIndicator(TApp.smallIconBackHome)

        getMyActivity().getSupportActionBar()!!.setDisplayHomeAsUpEnabled(true);
        getMyActivity().getSupportActionBar()!!.setDisplayShowHomeEnabled(true);

        getMyActivity().mToolbar.setNavigationIcon(TApp.smallIconBackHome)
        getMyActivity().mToolbar.setNavigationOnClickListener {
            getMyActivity().onBackPressed()
        }

        getMyActivity().changeToolbarBackground(R.color.page_bg)
//        getMyActivity().getSupportActionBar()!!.setBackgroundDrawable(ColorDrawable(activity.getResources().getColor(R.color.page_bg)))


    }

}

