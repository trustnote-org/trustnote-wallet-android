package org.trustnote.wallet.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import org.trustnote.wallet.R
import org.trustnote.wallet.TApp

class ScanLayout constructor(context: Context, attrs: AttributeSet? = null) : FrameLayout(context, attrs) {

    lateinit var scanTitle:TextView
    lateinit var  scanResult:ClearableEditText
    lateinit var  scanErr:TextView
    init {
        val view = View.inflate(context, R.layout.l_scan, null)
        addView(view)
        scanTitle = view.findViewById(R.id.scan_what_title)
        scanResult = view.findViewById(R.id.scan_result)
        scanErr = view.findViewById(R.id.scan_err)
    }

}