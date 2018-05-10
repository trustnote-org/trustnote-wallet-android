package org.trustnote.wallet.biz.wallet

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import org.trustnote.wallet.R
import org.trustnote.wallet.R.*
import org.trustnote.wallet.THandler
import org.trustnote.wallet.uiframework.BaseFragment
import org.trustnote.wallet.util.AndroidUtils
import org.trustnote.wallet.util.Utils
import org.trustnote.wallet.widget.MnemonicGridView

@SuppressLint("ValidFragment")  //TODO: the fragment cannot re-create from tomb.
class CreateWalletFragment(_layoutId: Int, _pager: ViewPager) : BaseFragment() {

    val mPager = _pager
    val mLayoutId = _layoutId
    var mRootView: View? = null
    //TODO: empty constructor.
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(mLayoutId, container, false)
    }

    private fun getMyActivity(): CreateWalletActivity {
        return getActivity() as CreateWalletActivity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<View>(R.id.next_step)?.setOnClickListener(View.OnClickListener {
            mPager.currentItem = mPager.currentItem + 1
        })

//        view.findViewById<View>(R.id.pre_step)?.setOnClickListener(View.OnClickListener {
//            mPager.currentItem = mPager.currentItem - 1
//        })
//
//        view.findViewById<View>(R.id.mnemonic_remove)?.setOnClickListener(View.OnClickListener {
//            mPager.currentItem = mPager.currentItem + 1
//        })

//        view.findViewById<View>(R.id.mnemonic_remove_confirm)?.setOnClickListener(View.OnClickListener {
//            createWallet(true)
//        })
//        view.findViewById<View>(R.id.mnemonic_remove_ignore)?.setOnClickListener(View.OnClickListener {
//            createWallet(false)
//        })

        val webView: WebView? = view.findViewById<WebView>(R.id.pwd_warning)
        if (webView != null) {
            val data = AndroidUtils.readAssetFile("pwd_warning.html")
            val localData = AndroidUtils.replaceTTTTag(data)
            webView.loadDataWithBaseURL("", localData, "text/html", "UTF-8", "")
        }

        mRootView = view
        initFragment(mRootView!!)
    }

    fun createWallet(removeMnemonic: Boolean) {
        Utils.runInbackground(Runnable {
            WalletManager.initWithMnemonic(removeMnemonic)
            activity.finish()
        })
    }

    fun onShowPage() {
        if (mRootView != null) {
            initFragment(mRootView!!)
        }
        getMyActivity().adjustUIBySetting(getPageSetting(mLayoutId))
    }

    private fun initFragment(view: View) {
        when (mLayoutId) {
            layout.f_new_seed_welcome -> {
                val deviceNameTV = view.findViewById<EditText>(R.id.welcome_device_name)
                //deviceNameTV.setText(WalletManager.model.deviceName)
                deviceNameTV.setOnKeyListener { v: View, keyCode: Int, event: KeyEvent ->
                    if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                        //do something here
                        WalletManager.model.deviceName = deviceNameTV.text.toString()
                        mPager.currentItem = mPager.currentItem + 1
                        true
                    } else {
                        false
                    }
                }
            }

            layout.f_new_seed_show_warning -> {

//                val newSeedTV = view.findViewById<TextView>(R.id.new_seed)
//                newSeedTV.text = WalletManager.getTmpMnemonic()
            }

            layout.f_new_seed_or_restore -> {
                view.findViewById<View>(R.id.btn_new_seed).setOnClickListener {
                    Utils.runInbackground(Runnable {
                        WalletManager.getOrCreateMnemonic()
                        THandler.instance.post {
                            mPager.currentItem = mPager.currentItem + 1
                        }
                    })
                }
            }

            layout.f_new_seed_verify -> {
//                (view.findViewById<MnemonicGridView>(R.id.grid_view)).init(WalletManager.getTmpMnemonic())
//                (view.findViewById<MnemonicGridView>(R.id.grid_view)).onWordCheckResult = { isOk: Boolean ->
//                    val btnPreStep = view.findViewById<Button>(R.id.pre_step)
//                    val btnRemove = view.findViewById<Button>(R.id.mnemonic_remove)
//                    val btnIgnore = view.findViewById<Button>(R.id.mnemonic_remove_ignore)
//                    val errMsg = view.findViewById<TextView>(R.id.err_msg)
//                    if (isOk) {
//                        errMsg.visibility = View.VISIBLE
//                        btnPreStep.visibility = View.GONE
//                        btnRemove.visibility = View.VISIBLE
//                        btnIgnore.visibility = View.VISIBLE
//                    } else {
//                        errMsg.visibility = View.INVISIBLE
//                        btnPreStep.visibility = View.VISIBLE
//                        btnRemove.visibility = View.GONE
//                        btnIgnore.visibility = View.GONE
//                    }
//                }

            }
        }
    }
}

//    button.setAlpha(.5f);
//    button.setClickable(false);}