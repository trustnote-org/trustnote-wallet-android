package org.trustnote.wallet.uiframework

import android.os.Bundle
import android.view.*
import android.widget.TextView
import org.trustnote.wallet.R
import org.trustnote.wallet.widget.ClearableEditText
import org.trustnote.wallet.widget.ErrTextView
import org.trustnote.wallet.widget.MyTextWatcher

class FragmentEditBase : FragmentBase() {

    override fun getLayoutId(): Int {
        return R.layout.f_edit_with_toolbar
    }

    lateinit var mDone: TextView
    lateinit var mTitle: TextView
    var mInitValue = ""
    var mErrInfo = ""

    lateinit var mEditText: ClearableEditText
    lateinit var mErr: ErrTextView
    var checkInputValidation: (String) -> Boolean = { true }
    var doneLogic: (String) -> Unit = {}

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(getLayoutId(), container, false)
        mRootView = view
        view.isClickable = true
        return view
    }

    override fun initFragment(view: View) {

        super.initFragment(view)

        mDone = mToolbar.findViewById(R.id.toolbar_done_right)
        mTitle = mToolbar.findViewById(R.id.toolbar_title_center)
        mEditText = findViewById(R.id.input_text)
        mErr = findViewById(R.id.input_err)

        mEditText.setText(mInitValue)
        mErr.setText(mErrInfo)

        mDone.setOnClickListener {
            val isDone = checkInputValidation.invoke(mEditText.text.toString())
            if (isDone) {
                doneLogic.invoke(mEditText.text.toString())
                onBackPressed()
            } else {

            }
        }

        mEditText.addTextChangedListener(MyTextWatcher(this))

    }

    fun setInitValue(value: String, errInfo: String, checkInputValidation: (String) -> Boolean = {true}, doneLogic: (String) -> Unit = {}) {
        this.mInitValue = value
        this.checkInputValidation = checkInputValidation
        this.doneLogic = doneLogic
        this.mErrInfo = errInfo
    }


    override fun updateUI() {

        super.updateUI()
        val isValid = checkInputValidation.invoke(mEditText.text.toString())
        mErr.visibility = if (isValid) View.INVISIBLE else View.VISIBLE

    }



}

