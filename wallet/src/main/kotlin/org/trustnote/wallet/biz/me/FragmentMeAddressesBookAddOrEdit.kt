package org.trustnote.wallet.biz.me

import android.view.View
import android.widget.Button
import android.widget.TextView
import org.trustnote.db.entity.TransferAddresses
import org.trustnote.wallet.R
import org.trustnote.wallet.uiframework.FragmentBase
import org.trustnote.wallet.util.AndroidUtils
import org.trustnote.wallet.util.TTTUtils
import org.trustnote.wallet.widget.ClearableEditText
import org.trustnote.wallet.widget.ErrTextView
import org.trustnote.wallet.widget.MyTextWatcher

class FragmentMeAddressesBookAddOrEdit : FragmentBase() {
    override fun getLayoutId(): Int {
        return R.layout.f_me_address_book_add
    }

    var isNewAddress: Boolean = true
    lateinit var scan: View
    lateinit var address: ClearableEditText
    lateinit var memo: ClearableEditText
    lateinit var addressErr: ErrTextView
    lateinit var memoErr: ErrTextView
    lateinit var title: TextView

    lateinit var save: Button
    var isEditMode: Boolean = false
    var oldAddress: String = ""
    var afterSave: () -> Unit = {}

    override fun initFragment(view: View) {

        super.initFragment(view)

        title = findViewById(R.id.title)
        if (!isNewAddress) {
            title.setText(R.string.address_book_edit_title)
        }
        scan = findViewById(R.id.scan_address)
        address = findViewById(R.id.address)
        memo = findViewById(R.id.memo)
        save = findViewById(R.id.save)


        addressErr = findViewById(R.id.address_err)
        memoErr = findViewById(R.id.memo_err)

        address.setOnFocusChangeListener { _, hasFocus -> if (hasFocus) addressErr.visibility = View.INVISIBLE }
        memo.setOnFocusChangeListener { _, hasFocus -> if (hasFocus) memoErr.visibility = View.INVISIBLE }

        address.addTextChangedListener(MyTextWatcher(this))
        memo.addTextChangedListener(MyTextWatcher(this))

        setupScan(scan, { handleScanRes(it) })

        save.setOnClickListener {

            if (validInput()) {

                if (isEditMode) {
                    val oldTransferAddresses = TransferAddresses()
                    oldTransferAddresses.address = oldAddress
                    AddressesBookManager.removeAddress(oldTransferAddresses)
                }
                AddressesBookManager.addAddress(address.text.toString(), memo.text.toString())
                afterSave.invoke()
                onBackPressed()

            }
        }

        address.setText(AndroidUtils.getStringFromBundle(arguments, AndroidUtils.KEY_BUNDLE_ADDRESS))
        memo.setText(AndroidUtils.getStringFromBundle(arguments, AndroidUtils.KEY_BUNDLE_MEMO))

        isEditMode = (arguments != null)
        if (isEditMode) {
            oldAddress = AndroidUtils.getStringFromBundle(arguments, AndroidUtils.KEY_BUNDLE_ADDRESS)
        }

    }

    private fun validInput(): Boolean {
        var hasErr = false
        if (memo.text.isEmpty() || memo.text.length > 10) {
            memoErr.visibility = View.VISIBLE
            hasErr = true
        }
        if (address.text.length != 32) {
            addressErr.visibility = View.VISIBLE
            hasErr = true
        }

        return !hasErr
    }

    private fun handleScanRes(scanRes: String) {
        val paymentInfo = TTTUtils.parsePaymentFromQRCode(scanRes)
        address.setText(paymentInfo.receiverAddress)
    }

    override fun updateUI() {
        super.updateUI()
        val isEnable = address.text.isNotEmpty() && memo.text.isNotEmpty()
        AndroidUtils.enableBtn(save, isEnable)
    }

}

