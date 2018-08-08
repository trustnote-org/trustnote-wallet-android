package org.trustnote.wallet.biz.wallet

import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import org.trustnote.wallet.R
import org.trustnote.wallet.TApp
import org.trustnote.wallet.biz.TTT
import org.trustnote.wallet.biz.FragmentPageBase
import org.trustnote.wallet.biz.ActivityMain
import org.trustnote.wallet.util.AndroidUtils
import org.trustnote.wallet.util.TTTUtils
import org.trustnote.wallet.util.Utils
import org.trustnote.wallet.widget.PageHeader
import org.trustnote.wallet.widget.TMnAmount

class FragmentWalletReceive : FragmentPageBase() {

    lateinit var addressText: TextView
    lateinit var addressQR: ImageView
    lateinit var receiveAmount: TMnAmount
    lateinit var clearAmount: TextView
    lateinit var setupAmount: TextView
    lateinit var copyBtn: Button
    lateinit var pageHeader: PageHeader
    var mnAmount = 0L

    override fun getLayoutId(): Int {
        return R.layout.l_dialog_wallet_receive
    }

    override fun initFragment(view: View) {
        super.initFragment(view)

        mRootView.findViewById<PageHeader>(R.id.page_header).closeAction = {
            onBackPressed()
        }

        val walletId = arguments.getString(TTT.KEY_WALLET_ID)
        credential = WalletManager.model.findWallet(walletId)

        addressText = mRootView.findViewById(R.id.receive_address_text)
        addressQR = mRootView.findViewById(R.id.qr_code_imageview)
        receiveAmount = mRootView.findViewById(R.id.receive_amount)
        receiveAmount.setupMyReceiverAddress()
        clearAmount = mRootView.findViewById(R.id.receive_clear_amount)
        setupAmount = mRootView.findViewById(R.id.receive_setup_amount)
        copyBtn = mRootView.findViewById(R.id.receive_btn_copy)

        pageHeader = findViewById(R.id.page_header)
        pageHeader.hideCloseBtn()

        setupAmount.setOnClickListener {
            val f = FragmentWalletReceiveSetAmount()
            AndroidUtils.addFragmentArguments(f, TTT.KEY_WALLET_ID, walletId)
            f.doneAction = {
                mnAmount = it
                updateUI()
            }
            addL2Fragment(f, isUseAnimation = false)
        }


        clearAmount.setOnClickListener {
            mnAmount = 0L
            updateUI()
        }

        copyBtn.setOnClickListener {

            AndroidUtils.copyTextToClipboard(addressText.text.toString())

            AndroidUtils.showIosToast(activity.getString(R.string.receive_copy_successful))

        }

    }

    override fun updateUI() {

        addressText.text = WalletManager.model.receiveAddress(credential)

        TTTUtils.setupAddressQRCode(WalletManager.model.receiveAddress(credential), mnAmount, addressQR)

        if (mnAmount == 0L) {
            receiveAmount.visibility = View.GONE
            clearAmount.visibility = View.GONE
            setupAmount.visibility = View.VISIBLE
        } else {
            receiveAmount.visibility = View.VISIBLE
            clearAmount.visibility = View.VISIBLE
            setupAmount.visibility = View.GONE
            receiveAmount.setMnAmount(mnAmount)
        }

        //setupAmount.visibility = View.INVISIBLE

    }

}

