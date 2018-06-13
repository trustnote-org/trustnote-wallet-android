package org.trustnote.wallet.biz.me

import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import org.trustnote.wallet.R
import org.trustnote.wallet.biz.TTT
import org.trustnote.wallet.biz.wallet.FragmentWalletBase
import org.trustnote.wallet.biz.wallet.WalletManager
import org.trustnote.wallet.uiframework.FragmentBase
import org.trustnote.wallet.util.AndroidUtils
import org.trustnote.wallet.util.TTTUtils
import org.trustnote.wallet.util.Utils

class FragmentMeWalletColdCode : FragmentBase() {

    override fun getLayoutId(): Int {
        return R.layout.f_me_wallet_code_code
    }

    //TODO: listen the wallet update event.

    override fun initFragment(view: View) {

        super.initFragment(view)

        mRootView.findViewById<TextView>(R.id.show_qr_title).setText(R.string.me_wallet_detail_label_cold_code)

        mRootView.findViewById<TextView>(R.id.show_qr_msg).setText(R.string.me_wallet_cold_code_msg)

        mRootView.findViewById<TextView>(R.id.next_step).setText(R.string.me_wallet_cold_code_btn)

        val qrTextView = mRootView.findViewById<TextView>(R.id.show_qr_text)

        val qrStr = TTTUtils.genColdScancodeStep1(credential)
        qrTextView.visibility = View.VISIBLE
        qrTextView.text = qrStr

        val qrImageView = findViewById<ImageView>(R.id.qr_code_imageview)

        TTTUtils.setupQRCode(qrStr, qrImageView)

        val nextStepBtn = findViewById<Button>(R.id.next_step)

        setupScan(nextStepBtn) {
            showScanResult(it, qrStr)
        }

    }

    override fun updateUI() {
        super.updateUI()
    }

    private fun showScanResult(step2Str: String, step1Str: String) {
        val step2Json = TTTUtils.scanStringToJsonObject(step2Str)
        val checkCodeFromStep2 = step2Json.get("v")?.asInt

        val step1Json = TTTUtils.scanStringToJsonObject(step1Str)
        val checkCodeFromStep1 = step1Json.get("v")?.asInt

        if (compareCheckCode(checkCodeFromStep1, checkCodeFromStep2)) {
            val f = FragmentDialogAuthorizeSuccessful {
                activity.onBackPressed()
            }

            val authorizeQrCode = TTTUtils.genColdScancodeStep3(WalletManager.model.mProfile.deviceAddress, checkCodeFromStep1!!)

            AndroidUtils.addFragmentArguments(f, TTT.KEY_QR_CODE, authorizeQrCode)
            AndroidUtils.openDialog(activity, f, false)

        } else {
            Utils.toastMsg("Verification failed.")
        }

        //val title = scanResultJson.get("name")?.asString
    }

    private fun compareCheckCode(c1: Int?, c2: Int?): Boolean {
        return c1 != null && c2 != null && c1 == c2
    }

}

