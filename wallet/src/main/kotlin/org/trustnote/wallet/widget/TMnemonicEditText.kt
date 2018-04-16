package org.trustnote.wallet.widget

import android.widget.EditText
import android.content.Context
import android.util.AttributeSet

class TMnemonicEditText @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : EditText(context, attrs, defStyleAttr) {


//    imageView.layoutParams = ViewGroup.LayoutParams(85, 85)
//    imageView.scaleType = ImageView.ScaleType.CENTER_CROP
//    imageView.setPadding(8, 8, 8, 8)

    var expectedInput: String? = null;
}


