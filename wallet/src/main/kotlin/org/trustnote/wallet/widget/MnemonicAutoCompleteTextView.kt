package org.trustnote.wallet.widget

import android.content.Context
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import org.trustnote.wallet.R
import org.trustnote.wallet.biz.js.JSApi
import org.trustnote.wallet.uiframework.BaseActivity

class MnemonicAutoCompleteTextView constructor(context: Context, attrs: AttributeSet? = null) : AutoCompleteTextView(context, attrs) {

    init {
        val wordList = JSApi().getBip38WordList().toMutableList()
        val adapter = WordAdapter(context, R.layout.item_mnemonic_autocomplete, wordList)
        this.setAdapter(adapter)

        setRawInputType(InputType.TYPE_CLASS_TEXT)
        setTextIsSelectable(true)

        this.setOnTouchListener { v, _ ->
            v.requestFocus()
            (getContext() as BaseActivity).showKeyboardWithAnimation()
            false
        }
        this.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                isWordInBip38 = wordList.contains(text.toString())
                if (isWordInBip38) {
                    focusNextWord()
                }
            }

        })

        //        setOnFocusChangeListener { v, hasFocus ->
        //            if (hasFocus) {
        //                val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        //                imm.hideSoftInputFromWindow(getWindowToken(), 0)
        //            }
        //        }
    }

    var isWordInBip38 = false


    fun focusNextWord() {

        if (nextFocusForwardId != 0 && parent != null) {
            (parent as ViewGroup).findViewById<View>(nextFocusForwardId).requestFocus()
        }
    }

}