package org.trustnote.wallet

import android.content.Intent
import android.os.Bundle
import org.trustnote.wallet.biz.pwd.ActivityInputPwd
import org.trustnote.wallet.biz.startMainActivityWithMenuId
import org.trustnote.wallet.biz.init.ActivityInit
import org.trustnote.wallet.biz.init.CreateWalletModel
import org.trustnote.wallet.biz.startMainActivityAfterLanguageChanged
import org.trustnote.wallet.uiframework.ActivityBase
import org.trustnote.wallet.util.AndroidUtils
import org.trustnote.wallet.util.Prefs
import org.trustnote.wallet.util.Utils

class ActivityMyApi : ActivityBase() {

    override fun injectDependencies(graph: TApplicationComponent) {
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        when {
            intent?.action == Intent.ACTION_SEND -> {
                if ("text/plain" == intent.type) {
                    handleSendText(intent) // Handle text being sent
                }
            }

            else -> {
            }
        }

        finish()
    }

    private fun handleSendText(intent: Intent?) {
        intent?.getStringExtra(Intent.EXTRA_TEXT)?.let {
            // Update UI to reflect text being shared
        }
    }
}

