package org.trustnote.wallet.biz.msgs

import android.view.View
import android.widget.Button
import org.trustnote.wallet.R
import org.trustnote.wallet.TApp
import org.trustnote.wallet.biz.wallet.WalletManager
import org.trustnote.wallet.uiframework.ActivityBase
import org.trustnote.wallet.util.AndroidUtils
import org.trustnote.wallet.util.SCAN_RESULT_TYPE
import org.trustnote.wallet.util.TTTUtils
import org.trustnote.wallet.widget.MyTextWatcher
import org.trustnote.wallet.widget.ScanLayout

class FragmentMsgsContactsAdd : FragmentMsgsBase() {

    lateinit var scanLayout: ScanLayout
    lateinit var btn: Button

    lateinit var ownerActivity: ActivityBase

    override fun getLayoutId(): Int {
        return R.layout.f_msg_contacts_add
    }

    override fun initFragment(view: View) {
        super.initFragment(view)

        ownerActivity = activity as ActivityBase

        scanLayout = mRootView.findViewById(R.id.contacts_add_scan_layoiut)
        btn = mRootView.findViewById(R.id.contacts_add_btn)

        setupScan(scanLayout.scanIcon) {
            if (it.isNotEmpty() && it.startsWith("TTT:")) {
                scanLayout.scanResult.setText(it.substring(4))
            } else {
                scanLayout.scanResult.setText(it)
            }
            updateUI()
        }

        scanLayout.scanTitle.setText(activity.getString(R.string.contacts_add_pair_code_label))

        scanLayout.scanResult.setHint(activity.getString(R.string.message_contacts_add_hint))

        val qrCode = AndroidUtils.getQrcodeFromBundle(arguments)
        scanLayout.scanResult.setText(qrCode)

        btn.setOnClickListener {

            if (isQrCodeValid()) {
                onBackPressed()
                MessageModel.instance.addContacts("TTT:" + scanLayout.scanResult.text.toString()) {
                    chatWithFriend(it, ownerActivity)
                }

            } else {
                val res = scanLayout.scanResult.text.toString()

                if (res.isEmpty()) {
                    scanLayout.scanErr.visibility = View.INVISIBLE
                } else {
                    scanLayout.scanErr.visibility = View.VISIBLE
                    scanLayout.scanErr.text = getErrInfo()
                }

            }

        }

        scanLayout.scanResult.addTextChangedListener(MyTextWatcher(this))

    }

    override fun updateUI() {
        super.updateUI()
        AndroidUtils.enableBtn(btn, scanLayout.scanResult.text.isNotEmpty())
        scanLayout.scanErr.visibility = View.INVISIBLE
    }

    private fun isQrCodeValid(): Boolean {

        val res = "TTT:" + scanLayout.scanResult.text.toString()
        val matchRes = TTTUtils.parseQrCodeType(res) == SCAN_RESULT_TYPE.TTT_PAIRID
        return if (matchRes) {
            !res.contains(WalletManager.model.mProfile.pubKeyForPairId)
        } else {
            matchRes
        }

    }

    private fun getErrInfo(): String {

        val res = "TTT:" + scanLayout.scanResult.text.toString()
        val matchRes = TTTUtils.parseQrCodeType(res) == SCAN_RESULT_TYPE.TTT_PAIRID
        return if (matchRes) {
            activity.getString(R.string.contacts_add_err_cannot_add_myself)
        } else {
            activity.getString(R.string.contacts_add_pairid_format_err)
        }

    }

}

