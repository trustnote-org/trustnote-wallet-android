package org.trustnote.wallet.biz.me

import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import org.trustnote.wallet.R
import org.trustnote.wallet.TTT
import org.trustnote.wallet.biz.FragmentDialogBase
import org.trustnote.wallet.util.AndroidUtils
import org.trustnote.wallet.util.TTTUtils

class FragmentDialogScanSignResult(val checkCode: Int, val confirmLogic: (String) -> Unit = {}) : FragmentDialogBase(R.layout.l_dialog_create_wallet_observer_finish, confirmLogic) {


    override fun initFragment(view: View) {

//        <string name="wallet_transfer_sign_scan_title">读入签名数据</string>
//        <string name="wallet_transfer_sign_scan_label">签名数据</string>
//        <string name="wallet_transfer_sign_scan_hint">扫描冷钱包签名数据二维码</string>
//        <string name="wallet_transfer_sign_scan_btn">发送交易</string>


        val title = view.findViewById<TextView>(R.id.create_wallet_observer_qr_finish_title)
        val msg = view.findViewById<TextView>(R.id.create_wallet_observer_qr_finish_msg)
        val lable = view.findViewById<TextView>(R.id.create_wallet_observer_qr_finish_import)
        val qrImageView = view.findViewById<ImageView>(R.id.create_wallet_observer_qr_finish_scan)
        val scanRestText = view.findViewById<TextView>(R.id.create_wallet_observer_qr_finish_scan_res)
        val scanErrInfo = view.findViewById<TextView>(R.id.create_wallet_observer_err)
        val btn = view.findViewById<Button>(R.id.create_wallet_observer_qr_finish_btn)

        title.setText(R.string.wallet_transfer_sign_scan_title)
        msg.visibility = View.GONE
        lable.setText(R.string.wallet_transfer_sign_scan_label)
        scanRestText.setText(R.string.wallet_transfer_sign_scan_hint)
        scanErrInfo.setText(R.string.wallet_transfer_sign_scan_invalid)

        AndroidUtils.disableBtn(btn)
        btn.setText(R.string.wallet_transfer_sign_scan_btn)
        btn.setOnClickListener {
            dismiss()
            confirmLogic.invoke(scanRestText.text.toString())
        }

        setupScan(qrImageView){

            val signature = TTTUtils.checkAndParseSignature(it, checkCode)
            if (signature.isNullOrBlank()) {
                scanErrInfo.visibility = View.VISIBLE
            } else {
                AndroidUtils.enableBtn(btn)
                scanRestText.text = signature
            }
        }

    }

}