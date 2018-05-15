package org.trustnote.wallet

import android.os.Bundle
import org.trustnote.wallet.biz.wallet.CreateWalletActivity
import org.trustnote.wallet.biz.wallet.CreateWalletModel
import org.trustnote.wallet.uiframework.BaseActivity
import org.trustnote.wallet.util.AndroidUtils

class StarterActivity : BaseActivity() {

    override fun injectDependencies(graph: TApplicationComponent) {
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (CreateWalletModel.isFinisheCreateOrRestore()) {
            startMainActivityWithMenuId()
        } else {
            AndroidUtils.startActivity(CreateWalletActivity::class.java)
        }

        finish()
    }
}

