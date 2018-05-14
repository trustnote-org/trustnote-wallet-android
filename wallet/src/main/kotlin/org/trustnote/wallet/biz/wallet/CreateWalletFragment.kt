package org.trustnote.wallet.biz.wallet

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import org.trustnote.wallet.R
import org.trustnote.wallet.TApp
import org.trustnote.wallet.uiframework.BaseFragment
import org.trustnote.wallet.util.AndroidUtils

@SuppressLint("ValidFragment")  //TODO: the fragment cannot re-create from tomb.
class CreateWalletFragment(_layoutId: Int, _pager: ViewPager) : BaseFragment() {

    val mPager = _pager
    val mLayoutId = _layoutId
    var mRootView: View = View(TApp.context)

    //TODO: empty constructor.
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mRootView = inflater.inflate(mLayoutId, container, false)
        return mRootView
    }

    private fun getMyActivity(): CreateWalletActivity {
        return getActivity() as CreateWalletActivity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<View>(R.id.next_step)?.setOnClickListener(View.OnClickListener {
            mPager.currentItem = mPager.currentItem + 1
        })


        val webView: WebView? = view.findViewById<WebView>(R.id.pwd_warning)
        if (webView != null) {
            val data = AndroidUtils.readAssetFile("pwd_warning.html")
            val localData = AndroidUtils.replaceTTTTag(data)
            webView.loadDataWithBaseURL("", localData, "text/html", "UTF-8", "")
        }

        mRootView = view
        initFragment(mRootView!!)
    }

    fun onShowPage() {
        if (mRootView is ViewGroup) {
            initFragment(mRootView!!)
            getMyActivity().adjustUIBySetting(getPageSetting(mLayoutId))
        }
    }

    private fun initFragment(view: View) {

    }
}

//    button.setAlpha(.5f);
//    button.setClickable(false);}
//    button.setTextColorAlpha()
