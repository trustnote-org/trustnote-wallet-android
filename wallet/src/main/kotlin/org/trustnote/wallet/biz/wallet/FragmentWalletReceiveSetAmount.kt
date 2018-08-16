package org.trustnote.wallet.biz.wallet

import android.text.InputFilter
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import org.trustnote.wallet.R
import org.trustnote.wallet.biz.FragmentPageBase
import org.trustnote.wallet.util.AndroidUtils
import org.trustnote.wallet.util.Utils
import org.trustnote.wallet.widget.DecimalDigitsInputFilter
import org.trustnote.wallet.widget.MyTextWatcher
import org.trustnote.wallet.widget.PageHeader

class FragmentWalletReceiveSetAmount : FragmentPageBase() {

    private lateinit var inputAmount: EditText
    private lateinit var btnConfirm: Button
    lateinit var pageHeader: PageHeader
    lateinit var doneAction: (Long) -> Unit

    init {
        useLayoutFromTop = true
    }

    override fun getLayoutId(): Int {
        return R.layout.l_dialog_wallet_receive_set_amount
    }

    override fun initFragment(view: View) {

        super.initFragment(view)


        mRootView.findViewById<PageHeader>(R.id.page_header).closeAction = {
            onBackPressed()
        }

        inputAmount = mRootView.findViewById(R.id.receive_amount_input)
        inputAmount.filters = arrayOf<InputFilter>(DecimalDigitsInputFilter(9, 4))

        btnConfirm = mRootView.findViewById(R.id.receive_set_amount_btn)

        btnConfirm.setOnClickListener {

            hideSystemSoftKeyboard()
            getMyActivity().onBackPressed()
            doneAction.invoke(Utils.mnToNotes(inputAmount.text.toString()))

        }

        pageHeader = findViewById(R.id.page_header)
        pageHeader.hideCloseBtn()
        fixOutmostLayoutPaddingBottom(R.dimen.line_gap_70)

        showSystemSoftKeyboard(inputAmount, activity)

        inputAmount.addTextChangedListener(MyTextWatcher(this))

        inputAmount.requestFocus()
    }

    override fun onResume() {
        super.onResume()
        activity.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING or WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
    }

    override fun onPause() {
        super.onPause()
        activity.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
    }

    override fun updateUI() {

        //TODO: dup code.
        fun checkAmountIsInRange() {
            val dotIndex = inputAmount.text.indexOf('.')
            if (dotIndex >= 0 && inputAmount.text.length >= dotIndex + 6) {
                inputAmount.setText(inputAmount.text.substring(0, dotIndex + 5))
            }

            if (dotIndex >= 10 || (dotIndex < 0 && inputAmount.text.length > 9)) {
                inputAmount.setText(inputAmount.text.substring(1))
            }

        }

        checkAmountIsInRange()

        AndroidUtils.enableBtn(btnConfirm, inputAmount.text.isNotBlank())
    }

}

