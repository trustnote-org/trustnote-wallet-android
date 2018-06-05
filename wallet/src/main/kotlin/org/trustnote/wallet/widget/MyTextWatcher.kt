package org.trustnote.wallet.widget

import android.text.Editable
import android.text.TextWatcher
import android.widget.TextView
import org.trustnote.wallet.uiframework.FragmentBase

class MyTextWatcher(val fragment: FragmentBase, val lambda: (TextView) -> Unit = {}) : TextWatcher {
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }

    override fun afterTextChanged(s: Editable?) {
        fragment.updateUI()
    }

}
