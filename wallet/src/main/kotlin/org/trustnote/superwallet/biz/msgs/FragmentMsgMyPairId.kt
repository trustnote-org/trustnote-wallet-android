package org.trustnote.superwallet.biz.msgs

import android.view.View
import android.webkit.ValueCallback
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import org.trustnote.superwallet.R
import org.trustnote.superwallet.biz.FragmentPageBase
import org.trustnote.superwallet.biz.wallet.WalletManager
import org.trustnote.superwallet.util.AndroidUtils
import org.trustnote.superwallet.util.TTTUtils
import org.trustnote.superwallet.widget.PageHeader

class FragmentMsgMyPairId : FragmentPageBase() {

    lateinit var pairIdQR: ImageView
    lateinit var pairIdText: TextView
    lateinit var copyBtn: Button

    override fun getLayoutId(): Int {
        return R.layout.l_dialog_msg_mypairid
    }

    override fun initFragment(view: View) {
        super.initFragment(view)

        mRootView.findViewById<PageHeader>(R.id.page_header).closeAction = {
            onBackPressed()
        }

        pairIdQR = mRootView.findViewById(R.id.qr_code_imageview)
        pairIdText = mRootView.findViewById(R.id.mypairid_text)
        copyBtn = mRootView.findViewById(R.id.mypairid_copy_btn)

        copyBtn.setOnClickListener {

            AndroidUtils.copyTextToClipboard("TTT:" + pairIdText.text.toString())

            AndroidUtils.showIosToast(activity.getString(R.string.receive_copy_successful))

        }

        WalletManager.model.generateMyPairId(ValueCallback {
            //TODO: use struct for qr code.
            if (it.isNotEmpty() && it.length > 4) {
                pairIdText.text = it.substring(4)
            } else {
                pairIdText.text = it
            }
            TTTUtils.setupQRCode(it, pairIdQR)
        })

    }

    override fun updateUI() {
        super.updateUI()
        mToolbar.visibility = View.INVISIBLE
    }

}

