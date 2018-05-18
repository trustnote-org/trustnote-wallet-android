package org.trustnote.wallet.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import org.trustnote.wallet.R

open class TMnAmount @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {

    init {
        val view = View.inflate(context, R.layout.w_mn_amount, null)
        addView(view)
    }

}


