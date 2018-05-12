package org.trustnote.wallet.widget

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.widget.TextView
import org.trustnote.wallet.TApp

class ErrTextView  constructor(context: Context, attrs: AttributeSet? = null) : TextView(context, attrs) {

    override fun setCompoundDrawables(left: Drawable?, top: Drawable?, right: Drawable?, bottom: Drawable?) {
        super.setCompoundDrawables(TApp.smallIconError, top, right, bottom)
    }

}