package org.trustnote.wallet.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import org.trustnote.wallet.R

open class FieldTextView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val fieldLable: TextView
    private val fieldValue: TextView
    private val fieldUnitValue: TextView
    private val confirmed: View
    private val unconfirmed: View

    init {
        val view = View.inflate(context, R.layout.item_field, null)
        addView(view)
        fieldLable = view.findViewById(R.id.field_label)
        fieldValue = view.findViewById(R.id.field_value)
        fieldUnitValue = view.findViewById(R.id.field_unit_value)

        confirmed = view.findViewById(R.id.tx_confirmed)
        unconfirmed = view.findViewById(R.id.tx_unconfirmed)
    }

    fun setField(labelResId: Int, value: String) {
        fieldLable.setText(labelResId)

        fieldValue.text = value
        fieldValue.visibility = View.VISIBLE
    }

    fun setUnitField(value: String) {
        fieldLable.setText(R.string.tx_unit_id)
        fieldUnitValue.text = value

        fieldUnitValue.visibility = View.VISIBLE
    }

    private fun showStatus(isStable: Boolean) {
        if (isStable) confirmed.visibility = View.VISIBLE
        else unconfirmed.visibility = View.VISIBLE
    }

    fun setStatus(lableResId: Int, isConfirmed: Boolean) {
        fieldLable.setText(lableResId)
        showStatus(isConfirmed)
    }

}


