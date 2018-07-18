package org.trustnote.wallet.biz

import android.view.View
import android.webkit.ValueCallback
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import org.trustnote.wallet.R
import org.trustnote.wallet.TApp
import org.trustnote.wallet.biz.FragmentPageBase
import org.trustnote.wallet.biz.wallet.WalletManager
import org.trustnote.wallet.util.AndroidUtils
import org.trustnote.wallet.util.MyThreadManager
import org.trustnote.wallet.util.TTTUtils
import org.trustnote.wallet.util.Utils
import org.trustnote.wallet.widget.PageHeader
import org.w3c.dom.Text

class FragmentProgressBlocking : FragmentPageBase() {

    override fun getLayoutId(): Int {
        return R.layout.l_dialog_blocking
    }

    var isDone = false
    var afterWaitingLogic: () -> Unit = {}
    lateinit var waitingText: TextView

    override fun initFragment(view: View) {
        super.initFragment(view)
        waitingText = findViewById(R.id.waiting_msg)
        mRootView.postDelayed({
            isDone = true
            onBackPressed()
            afterWaitingLogic.invoke()
        }, 7000)

        if (arguments != null && arguments.containsKey(AndroidUtils.KEY_WAITING_MSG_RES_ID)) {
            waitingText.setText(arguments.getInt(AndroidUtils.KEY_WAITING_MSG_RES_ID))
        }
    }

    override fun updateUI() {
        super.updateUI()
    }

    override fun setupToolbar() {

        super.setupToolbar()
        mToolbar.visibility = View.INVISIBLE

    }


}

