package org.trustnote.wallet

import android.os.Bundle
import org.trustnote.wallet.biz.pwd.InputPwdActivityBase
import org.trustnote.wallet.biz.startMainActivityWithMenuId
import org.trustnote.wallet.biz.init.ActivityInit
import org.trustnote.wallet.biz.init.CreateWalletModel
import org.trustnote.wallet.uiframework.ActivityBase
import org.trustnote.wallet.util.AndroidUtils
import org.trustnote.wallet.util.Prefs

class ActivityBaseStarter : ActivityBase() {

    override fun injectDependencies(graph: TApplicationComponent) {
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!TApp.userAlreadyInputPwd
            && CreateWalletModel.readPwd().isNotEmpty()
            && Prefs.readEnablepwdForStartup()) {

            AndroidUtils.startActivity(InputPwdActivityBase::class.java)
            finish()
            return
        }

        if (CreateWalletModel.isFinisheCreateOrRestore()) {
            startMainActivityWithMenuId(R.id.menu_wallet)
        } else {
            AndroidUtils.startActivity(ActivityInit::class.java)
        }

        finish()
    }
}

