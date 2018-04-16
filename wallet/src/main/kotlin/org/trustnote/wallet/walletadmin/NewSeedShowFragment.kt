package org.trustnote.wallet.walletadmin

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import org.trustnote.wallet.R
import org.trustnote.wallet.R.*
import org.trustnote.wallet.uiframework.BaseFragment
import org.trustnote.wallet.widget.MnemonicGridView

@SuppressLint("ValidFragment")  //TODO: the fragment cannot re-create from tomb.
class NewSeedShowFragment(_layoutId: Int, _pager: ViewPager) : BaseFragment() {

    val mPager = _pager
    val mLayoutId = _layoutId
    var mRootView: View? = null
    //TODO: empty constructor.
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(mLayoutId, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<View>(R.id.next_step)?.setOnClickListener(View.OnClickListener {
            mPager.currentItem = mPager.currentItem + 1
        })

        view.findViewById<View>(R.id.pre_step)?.setOnClickListener(View.OnClickListener {
            mPager.currentItem = mPager.currentItem - 1
        })

        view.findViewById<View>(R.id.mnemonic_remove)?.setOnClickListener(View.OnClickListener {
            mPager.currentItem = mPager.currentItem + 1
        })

        view.findViewById<View>(R.id.mnemonic_remove_confirm)?.setOnClickListener(View.OnClickListener {
            createWallet(true)
        })
        view.findViewById<View>(R.id.mnemonic_remove_ignore)?.setOnClickListener(View.OnClickListener {
            createWallet(false)
        })

        mRootView = view
        initFragment(mRootView!!)
    }

    fun createWallet(removeMnemonic: Boolean) {
        WalletModel.instance.createWallet(removeMnemonic, Runnable {
            activity.finish()
        })
    }

    fun onShowPage() {
        if (mRootView != null) {
            initFragment(mRootView!!)
        }
    }

    private fun initFragment(view: View) {
        when (mLayoutId) {
            layout.f_new_seed_welcome -> {
                val deviceNameTV = view.findViewById<EditText>(R.id.welcome_device_name)
                deviceNameTV.setText(WalletModel.instance.deviceName)
                deviceNameTV.setOnKeyListener { v: View, keyCode: Int, event: KeyEvent ->
                    if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                        //do something here
                        WalletModel.instance.deviceName = deviceNameTV.text.toString()
                        mPager.currentItem = mPager.currentItem + 1
                        true
                    } else {
                        false
                    }
                }
            }

            layout.f_new_seed_show_warning -> {

                val newSeedTV = view.findViewById<TextView>(R.id.new_seed)
                newSeedTV.text = WalletModel.instance.getFullMnemonic()
            }

            layout.f_new_seed_or_restore -> {
                view.findViewById<View>(R.id.btn_new_seed).setOnClickListener {
                    WalletModel().newMnemonic(Runnable {
                        mPager.currentItem = mPager.currentItem + 1
                    })
                }
            }

            layout.f_new_seed_confirm -> {
                (view.findViewById<MnemonicGridView>(R.id.grid_view)).init(WalletModel.instance.currentMnemonic)
                (view.findViewById<MnemonicGridView>(R.id.grid_view)).onWordCheckResult = { isOk: Boolean ->
                    val btnPreStep = view.findViewById<Button>(R.id.pre_step)
                    val btnRemove = view.findViewById<Button>(R.id.mnemonic_remove)
                    val btnIgnore = view.findViewById<Button>(R.id.mnemonic_remove_ignore)
                    val errMsg = view.findViewById<TextView>(R.id.err_msg)
                    if (isOk) {
                        errMsg.visibility = View.VISIBLE
                        btnPreStep.visibility = View.GONE
                        btnRemove.visibility = View.VISIBLE
                        btnIgnore.visibility = View.VISIBLE
                    } else {
                        errMsg.visibility = View.INVISIBLE
                        btnPreStep.visibility = View.VISIBLE
                        btnRemove.visibility = View.GONE
                        btnIgnore.visibility = View.GONE
                    }
                }

            }
        }
    }

}