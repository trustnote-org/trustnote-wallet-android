package org.trustnote.wallet.widget

import android.content.Context
import android.util.AttributeSet
import android.widget.AutoCompleteTextView
import org.trustnote.wallet.R
import org.trustnote.wallet.js.JSApi
import org.trustnote.wallet.uiframework.BaseActivity

class MnemonicAutoCompleteTextView constructor(context: Context, attrs: AttributeSet? = null) : AutoCompleteTextView(context, attrs) {

    init {
        val wordList = JSApi().getBip38WordList()
        val adapter = WordAdapter(context, R.layout.item_mnemonic_autocomplete, wordList)
        this.setAdapter(adapter)

        //inputType = InputType.TYPE_NULL
        isCursorVisible = true

        this.setOnTouchListener { v, event ->
            v.requestFocus()
            (getContext() as BaseActivity).showKeyboardWithAnimation()
            true
        }

        //        setOnFocusChangeListener { v, hasFocus ->
        //            if (hasFocus) {
        //                val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        //                imm.hideSoftInputFromWindow(getWindowToken(), 0)
        //            }
        //        }

    }
}