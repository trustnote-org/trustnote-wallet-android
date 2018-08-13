package org.trustnote.wallet.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import org.trustnote.wallet.R

class ScanLayout constructor(context: Context, attrs: AttributeSet? = null) : FrameLayout(context, attrs) {

    var scanTitle: TextView
    var scanResult: ClearableEditText
    var scanErr: TextView
    var scanIcon: ImageView

    init {

        val view = View.inflate(context, R.layout.l_scan, null)

        addView(view)

        scanTitle = view.findViewById(R.id.scan_what_title)
        scanResult = view.findViewById(R.id.scan_result)
        scanErr = view.findViewById(R.id.scan_err)

        scanIcon = view.findViewById(R.id.scan_icon)
        scanIcon.setImageDrawable(context.getDrawable(R.drawable.action_scan))

        scanResult.bindingErr = scanErr

    }

}