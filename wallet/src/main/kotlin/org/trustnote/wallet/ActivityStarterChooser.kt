package org.trustnote.wallet

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

class ActivityStarterChooser : ActivityBase() {

    override fun injectDependencies(graph: TApplicationComponent) {
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val isFromChangeLanguage = intent.getBooleanExtra(AndroidUtils.KEY_FROM_CHANGE_LANGUAGE, false)
        if (isFromChangeLanguage) {
            TApp.userAlreadyInputPwd = true
        }

//        if (!Utils.isDeveloperFeature() && !TApp.userAlreadyInputPwd
        if (!TApp.userAlreadyInputPwd
            && CreateWalletModel.readPwdHash().isNotEmpty()
            && Prefs.readEnablepwdForStartup()) {

            AndroidUtils.startActivity(ActivityInputPwd::class.java)
            finish()
            return
        }

        if (CreateWalletModel.isFinisheCreateOrRestore()) {
            if (isFromChangeLanguage) {
                startMainActivityAfterLanguageChanged()
            } else {
                startMainActivityWithMenuId(R.id.menu_wallet)
            }
        } else {
            AndroidUtils.startActivity(ActivityInit::class.java)
        }

        finish()
    }
}

