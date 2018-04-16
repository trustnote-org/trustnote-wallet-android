package org.trustnote.wallet.widget

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.GridView
import org.trustnote.wallet.R


class MnemonicGridView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : GridView(context, attrs, defStyleAttr) {

    lateinit var mMnemonic: List<String>
    var onWordCheckResult = { isAllWordOK: Boolean -> }
    set(value) {
        (adapter as MnemonicAdapter).onCheckResult = value
    }

    fun init(currentMnemonic: List<String>) {
        mMnemonic = ArrayList(currentMnemonic)
        adapter = MnemonicAdapter(context, mMnemonic)
    }
}

class MnemonicAdapter(private val context: Context, mnemonic: List<String>) : BaseAdapter() {
    val mMnemonic: List<String> = mnemonic
    var onCheckResult = { isAllWordOK: Boolean -> }
    val editTextCache = HashMap<Int, TMnemonicEditText >()

    override fun getCount(): Int = mMnemonic.size

    override fun getItem(position: Int): String? = null

    override fun getItemId(position: Int): Long = 0L

    // create a new ImageView for each item referenced by the Adapter
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val editTextView: TMnemonicEditText
        //TODO: why warning.
        when (convertView == null) {
            true -> {
                editTextView = LayoutInflater.from(context).inflate(R.layout.item_mnemonic, parent, false) as TMnemonicEditText
            }

            false -> {
                editTextView = convertView as TMnemonicEditText
            }
        }

        editTextView.setText(mMnemonic[position])
        //editTextView.setHint(mMnemonic[position])
        editTextView.expectedInput = mMnemonic[position]
        editTextCache[position] = editTextView

        //setup the focus for next/enter key event.
        val resourceId = context.getResources().getIdentifier("mnemonic_" + position, "id", context.getPackageName())
        var nextPosition = position + 1
        if (nextPosition == mMnemonic.size) {
            nextPosition = 0
        }
        val nextResourceId = context.getResources().getIdentifier("mnemonic_" + nextPosition, "id", context.getPackageName())
        editTextView.setId(resourceId)
        editTextView.setNextFocusForwardId(nextResourceId)

        editTextView.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                checkAllWord()
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })

        return editTextView
    }

    fun checkAllWord() {
        for (entry in editTextCache) {
            //TODO: entry.key != 0 -> bug, the first edittext always return ""
            if (entry.key != 0 && entry.value.text.toString().length < 3) {
                onCheckResult(false)
                return
            }
        }
        onCheckResult(true)
    }
}