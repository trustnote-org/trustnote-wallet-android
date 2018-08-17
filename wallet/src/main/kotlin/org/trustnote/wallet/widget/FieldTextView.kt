package org.trustnote.wallet.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import org.trustnote.db.TxType
import org.trustnote.wallet.R
import org.trustnote.wallet.util.TTTUtils

open class FieldTextView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val fieldLable: TextView
    private val fieldValue: TextView
    val fieldUnitValue: TextView
    private val statusLayout: View
    private val statusImageView: ImageView
    private val statusTextView: TextView

    init {
        val view = View.inflate(context, R.layout.item_field, null)
        addView(view)
        fieldLable = view.findViewById(R.id.field_label)
        fieldValue = view.findViewById(R.id.field_value)
        fieldUnitValue = view.findViewById(R.id.field_unit_value)

        statusImageView = view.findViewById(R.id.tx_status_img)
        statusTextView = view.findViewById(R.id.tx_status_text)
        statusLayout = view.findViewById(R.id.tx_status_layout)
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

    private fun showStatus(isStable: Boolean, txType: TxType) {
        statusLayout.visibility = View.VISIBLE
        statusImageView.setImageResource(TTTUtils.getTxStatusDrawable(txType, isStable))
        statusTextView.setText(TTTUtils.getTxStatusTextRes(txType, isStable))
        statusTextView.setTextColor(TTTUtils.getTxStatusTextColor(txType, isStable))
    }

    fun setStatus(lableResId: Int, isConfirmed: Boolean, txType: TxType) {
        fieldLable.setText(lableResId)
        showStatus(isConfirmed, txType)
    }

}


