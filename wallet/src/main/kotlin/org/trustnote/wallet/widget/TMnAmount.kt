package org.trustnote.wallet.widget

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import org.trustnote.wallet.R

open class TMnAmount @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {

    private val amountView: TextView
    private val decimalView: TextView
    var amount: Long = 0
    private var showAsSmall = false

    init {
        val view = View.inflate(context, R.layout.w_mn_amount, null)
        addView(view)
        amountView = view.findViewById(R.id.amount)
        decimalView = view.findViewById(R.id.decimal)

    }

    fun setupStyle(showAsSmall: Boolean) {
        this.showAsSmall = showAsSmall
        if (showAsSmall) {
            amountView.setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimensionPixelSize(R.dimen.mn_amount_s_i).toFloat())
            decimalView.setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimensionPixelSize(R.dimen.mn_amount_s_d).toFloat())
        } else {
            amountView.setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimensionPixelSize(R.dimen.mn_amount_b_i).toFloat())
            decimalView.setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimensionPixelSize(R.dimen.mn_amount_b_d).toFloat())
        }
    }

    fun setMnAmount(i: Long) {
        amount = i
        updateUI()
    }

    private fun updateUI() {
        amountView.text = "${(amount / 1000000).toString()}."
        decimalView.text = (amount % 1000000).toString().padEnd(4, '0')

    }

}


