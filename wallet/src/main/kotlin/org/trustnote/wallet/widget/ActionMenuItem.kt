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

open class ActionMenuItem @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val text: TextView
    private val icon: ImageView

    init {
        val view = View.inflate(context, R.layout.item_action_menu, null)
        addView(view)
        text = view.findViewById(R.id.text)
        icon = view.findViewById(R.id.ic)

        val a = context.obtainStyledAttributes(attrs, R.styleable.ActionMenuItem)


        text.text = a.getString(R.styleable.ActionMenuItem_action_text)
        icon.setImageDrawable(a.getDrawable(R.styleable.ActionMenuItem_action_icon))

    }


}


