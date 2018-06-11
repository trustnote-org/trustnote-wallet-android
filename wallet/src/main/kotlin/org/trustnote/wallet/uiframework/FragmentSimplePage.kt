package org.trustnote.wallet.uiframework

import android.widget.TextView
import org.trustnote.wallet.R
import org.trustnote.wallet.util.AndroidUtils

class FragmentSimplePage : FragmentBase() {

    override fun getLayoutId(): Int {
        return R.layout.f_simple_info
    }

    override fun updateUI() {
        super.updateUI()

        val title = mRootView.findViewById<TextView>(R.id.title)
        title.text = (AndroidUtils.getTitleFromBundle(arguments))

        val msg = mRootView.findViewById<TextView>(R.id.msg)
        msg.text = (AndroidUtils.getMsgFromBundle(arguments))
    }

}

