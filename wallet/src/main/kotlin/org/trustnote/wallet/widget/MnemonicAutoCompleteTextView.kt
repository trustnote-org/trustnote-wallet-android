package org.trustnote.wallet.widget

import android.content.Context
import android.util.AttributeSet
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import org.trustnote.wallet.R
import org.trustnote.wallet.js.JSApi

class MnemonicAutoCompleteTextView constructor(context: Context, attrs: AttributeSet? = null) : AutoCompleteTextView(context, attrs) {

    init {
        val wordList = JSApi().getBip38WordList()
        val adapter = ArrayAdapter<String>(context, R.layout.item_mnemonic_autocomplete, wordList);
        this.setAdapter(adapter)
    }

}