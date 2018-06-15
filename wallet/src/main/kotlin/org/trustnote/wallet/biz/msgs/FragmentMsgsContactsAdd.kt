package org.trustnote.wallet.biz.msgs

import android.view.View
import android.view.WindowManager
import android.widget.Button
import kotlinx.android.synthetic.main.w_mnemonics_grid.*
import org.trustnote.wallet.R
import org.trustnote.wallet.TApp
import org.trustnote.wallet.biz.TTT
import org.trustnote.wallet.biz.wallet.WalletManager
import org.trustnote.wallet.util.AndroidUtils
import org.trustnote.wallet.util.SCAN_RESULT_TYPE
import org.trustnote.wallet.util.TTTUtils
import org.trustnote.wallet.util.Utils
import org.trustnote.wallet.widget.ScanLayout

class FragmentMsgsContactsAdd : FragmentMsgsBase() {

    lateinit var scanLayout: ScanLayout
    lateinit var btn: Button

    override fun getLayoutId(): Int {
        return R.layout.f_msg_contacts_add
    }

    override fun initFragment(view: View) {
        super.initFragment(view)

        scanLayout = mRootView.findViewById(R.id.contacts_add_scan_layoiut)
        btn = mRootView.findViewById(R.id.contacts_add_btn)

        setupScan(scanLayout.scanIcon) {
            scanLayout.scanResult.setText(it)
            updateUI()
        }

        scanLayout.scanTitle.setText(TApp.getString(R.string.contacts_add_pair_code_label))

        val qrCode = AndroidUtils.getQrcodeFromBundle(arguments)
        scanLayout.scanResult.setText(qrCode)

        btn.setOnClickListener {
            Utils.toastMsg("... ...")
            onBackPressed()
        }

    }

    override fun updateUI() {
        super.updateUI()
        if (isQrCodeValid()) {

            scanLayout.scanErr.visibility = View.INVISIBLE
            AndroidUtils.enableBtn(btn)

        } else {

            val res = scanLayout.scanResult.text.toString()

            if (res.isEmpty()) {
                scanLayout.scanErr.visibility = View.INVISIBLE
                AndroidUtils.disableBtn(btn)
            } else {
                scanLayout.scanErr.visibility = View.VISIBLE
                scanLayout.scanErr.text = getErrInfo()
                AndroidUtils.disableBtn(btn)
            }

        }

    }

    private fun isQrCodeValid(): Boolean {
        val res = scanLayout.scanResult.text.toString()
        val matchRes = TTTUtils.parseQrCodeType(res) == SCAN_RESULT_TYPE.TTT_PAIRID
        return if (matchRes) {
            !res.contains(WalletManager.model.mProfile.pubKeyForPairId)
        } else {
            matchRes
        }
    }

    private fun getErrInfo(): String {
        val res = scanLayout.scanResult.text.toString()
        val matchRes = TTTUtils.parseQrCodeType(res) == SCAN_RESULT_TYPE.TTT_PAIRID
        return if (matchRes) {
            TApp.getString(R.string.contacts_add_err_cannot_add_myself)
        } else {
            TApp.getString(R.string.contacts_add_pairid_format_err)
        }
    }

}

