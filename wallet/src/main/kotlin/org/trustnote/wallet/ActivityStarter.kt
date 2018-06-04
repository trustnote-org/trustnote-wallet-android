package org.trustnote.wallet

import android.os.Bundle
import org.trustnote.wallet.biz.pwd.InputPwdActivity
import org.trustnote.wallet.biz.startMainActivityWithMenuId
import org.trustnote.wallet.biz.init.CreateWalletActivity
import org.trustnote.wallet.biz.init.CreateWalletModel
import org.trustnote.wallet.uiframework.BaseActivity
import org.trustnote.wallet.util.AndroidUtils
import org.trustnote.wallet.util.Prefs

class ActivityStarter : BaseActivity() {

    override fun injectDependencies(graph: TApplicationComponent) {
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!TApp.userAlreadyInputPwd
            && CreateWalletModel.readPwd().isNotEmpty()
            && Prefs.readEnablepwdForStartup()) {

            AndroidUtils.startActivity(InputPwdActivity::class.java)
            finish()
            return
        }

        if (CreateWalletModel.isFinisheCreateOrRestore()) {
            startMainActivityWithMenuId(R.id.menu_wallet)
        } else {
            AndroidUtils.startActivity(CreateWalletActivity::class.java)
        }

        finish()
    }
}

