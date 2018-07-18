package org.trustnote.wallet.biz.me

import android.view.View
import android.widget.Button
import android.widget.EditText
import org.trustnote.db.entity.TransferAddresses
import org.trustnote.wallet.R
import org.trustnote.wallet.uiframework.FragmentBase
import org.trustnote.wallet.util.AndroidUtils
import org.trustnote.wallet.util.TTTUtils

class FragmentMeAddressesBookAddOrEdit : FragmentBase() {
    override fun getLayoutId(): Int {
        return R.layout.f_me_address_book_add
    }

    lateinit var scan: View
    lateinit var address: EditText
    lateinit var memo: EditText
    lateinit var save: Button
    var isEditMode: Boolean = false
    var oldAddress: String = ""
    var afterSave: () -> Unit = {}

    override fun initFragment(view: View) {

        super.initFragment(view)

        scan = findViewById(R.id.scan_address)
        address = findViewById(R.id.address)
        memo = findViewById(R.id.memo)
        save = findViewById(R.id.save)

        setupScan(scan, {handleScanRes(it)})

        save.setOnClickListener {

            if (isEditMode) {
                val oldTransferAddresses = TransferAddresses()
                oldTransferAddresses.address = oldAddress
                AddressesBookManager.removeAddress(oldTransferAddresses)
            }
            AddressesBookManager.addAddress(address.text.toString(), memo.text.toString())
            afterSave.invoke()
            onBackPressed()

        }

        address.setText(AndroidUtils.getStringFromBundle(arguments, AndroidUtils.KEY_BUNDLE_ADDRESS))
        memo.setText(AndroidUtils.getStringFromBundle(arguments, AndroidUtils.KEY_BUNDLE_MEMO))

        isEditMode = (arguments != null)
        if (isEditMode) {
            oldAddress = AndroidUtils.getStringFromBundle(arguments, AndroidUtils.KEY_BUNDLE_ADDRESS)
        }

    }


    private fun handleScanRes(scanRes: String) {
        val paymentInfo = TTTUtils.parsePaymentFromQRCode(scanRes)
        address.setText(paymentInfo.receiverAddress)
    }

}

