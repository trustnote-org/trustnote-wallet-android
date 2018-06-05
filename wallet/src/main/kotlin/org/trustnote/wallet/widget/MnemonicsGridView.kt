package org.trustnote.wallet.widget

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import org.trustnote.wallet.R

class MnemonicsGridView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    var gridView: GridView
    var gridAdapter: MnemonicAdapter
    var err: TextView
    var onCheckResult = { isAllWordOK: Boolean -> }

    init {
        val view = View.inflate(context, R.layout.w_mnemonics_grid, null)
        addView(view)

        gridView = findViewById<GridView>(R.id.grid_view)
        err = findViewById<TextView>(R.id.err)

        val wordPlaceHolder = List(12) {
            ""
        }
        gridAdapter = MnemonicAdapter(context, wordPlaceHolder)
        gridView.adapter = gridAdapter

        gridAdapter.onCheckResult = {
            onCheckResult(it)
            err.visibility = INVISIBLE
        }

    }

    fun setMnemonic(mnemonic: String, isVerify: Boolean) {

        gridAdapter.verifyEnabled = isVerify
        gridAdapter.mMnemonic = mnemonic.split(" ")

        gridAdapter.notifyDataSetInvalidated()

        err.visibility = View.INVISIBLE
    }

    fun setCheckMnemonic(mnemonic: String) {
        gridAdapter.mMnemonicCheck = mnemonic.split(" ")
    }

    fun getUserInputMnemonic(): String {
        return getAllMnemonicAsString()
    }

    fun showErr() {
        err.visibility = View.VISIBLE
    }

    fun isVerifyOk(): Boolean {
        for (i in 0 .. 11) {
            val cell = gridView.getChildAt(i) as MnemonicAutoCompleteTextView
            if (!cell.isWordInBip38 || cell.text.toString() != gridAdapter.mMnemonicCheck[i]){
                return false
            }
        }
        return true
    }

    fun getAllMnemonicAsString(): String {

        val listOfMnemonic = List<String>(12) {
            val cell = gridView.getChildAt(it) as MnemonicAutoCompleteTextView
            cell.text.toString()
        }

        return listOfMnemonic.joinToString(" ")
    }

}

class MnemonicAdapter(private val context: Context, mnemonic: List<String>) : BaseAdapter() {
    var mMnemonic: List<String> = mnemonic
    var onCheckResult = { isAllWordOK: Boolean -> }
    var verifyEnabled = true
    var mMnemonicCheck: List<String> = mnemonic

    override fun getCount(): Int = mMnemonic.size

    override fun getItem(position: Int): String? = null

    override fun getItemId(position: Int): Long = 0L

    // create a new ImageView for each item referenced by the Adapter
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val editTextView: MnemonicAutoCompleteTextView
        //TODO: why warning.
        when (convertView == null) {
            true -> {
                editTextView = LayoutInflater.from(context).inflate(R.layout.item_mnemonic, parent, false) as MnemonicAutoCompleteTextView

                //setup the focus for next/enter key event.
                val resourceId = context.resources.getIdentifier("mnemonic_$position", "id", context.packageName)
                var nextPosition = position + 1
                if (nextPosition == mMnemonic.size) {
                    nextPosition = 0
                }
                val nextResourceId = context.resources.getIdentifier("mnemonic_$nextPosition", "id", context.packageName)
                editTextView.id = resourceId
                editTextView.nextFocusForwardId = nextResourceId

            }

            false -> {
                editTextView = convertView as MnemonicAutoCompleteTextView
            }
        }



        editTextView.isEnabled = verifyEnabled

        if (mMnemonic[position].isNotEmpty()) {
            editTextView.setText(mMnemonic[position])
        }

        if (verifyEnabled) {
            editTextView.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(p0: Editable?) {
                    if (editTextView.parent == null) {
                        return
                    }
                    checkAllWord(editTextView.parent as GridView)
                }

                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }
            })

            editTextView.setOnKeyListener { v, keyCode, _ ->
                if (keyCode == KeyEvent.KEYCODE_NAVIGATE_NEXT) {
                    editTextView.focusNextWord()
                    true
                } else {
                    false
                }
            }
        }

        return editTextView
    }

    fun checkAllWord(grid: GridView) {

        for (i in 0..11) {
            val cell = grid.getChildAt(i) as MnemonicAutoCompleteTextView
            //if (!cell.isWordInBip38 || cell.text.toString() != mMnemonicCheck[i]){
            if (cell.text.toString().isEmpty()) {
                onCheckResult(false)
                return
            }
        }
        onCheckResult(true)
    }
}