package org.trustnote.superwallet.widget

import android.content.Context
import android.os.Build
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AutoCompleteTextView
import org.trustnote.superwallet.R
import org.trustnote.superwallet.biz.js.JSApi
import org.trustnote.superwallet.uiframework.ActivityBase
import android.view.inputmethod.EditorInfo
import android.widget.TextView

class MnemonicAutoCompleteTextView constructor(context: Context, attrs: AttributeSet? = null)
    : AutoCompleteTextView(context, attrs) {

    init {
        val wordList = JSApi().getBip38WordList().toMutableList()
        val adapter = WordAdapter(context, R.layout.item_mnemonic_autocomplete, wordList)
        this.setAdapter(adapter)

        setRawInputType(InputType.TYPE_CLASS_TEXT)
        setTextIsSelectable(true)

        // Below code will cause UI glitch.
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            showSoftInputOnFocus = false
        }

        this.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                (getContext() as ActivityBase).showKeyboardWithAnimation()
            }
        }

        this.setOnTouchListener { v, event ->
            if (this.isFocused) {
                (getContext() as ActivityBase).showKeyboardWithAnimation()
            }
            false
        }

        this.setOnItemClickListener { parent, view, position, id ->
            focusNextWord()
        }

        this.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                isWordInBip38 = wordList.contains(text.toString())
                if (isWordInBip38) {
                    //focusNextWord()
                }
            }
        })

    }

    var isWordInBip38 = false

    fun focusNextWord() {

        if (nextFocusForwardId != 0 && parent != null) {
            (parent as ViewGroup).findViewById<View>(nextFocusForwardId).requestFocus()
        }
    }

}