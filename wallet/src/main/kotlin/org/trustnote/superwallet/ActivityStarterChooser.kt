package org.trustnote.superwallet

import android.os.Bundle
import org.trustnote.superwallet.biz.pwd.ActivityInputPwd
import org.trustnote.superwallet.biz.startMainActivityWithMenuId
import org.trustnote.superwallet.biz.init.ActivityInit
import org.trustnote.superwallet.biz.init.CreateWalletModel
import org.trustnote.superwallet.biz.startMainActivityAfterLanguageChanged
import org.trustnote.superwallet.uiframework.ActivityBase
import org.trustnote.superwallet.util.AndroidUtils
import org.trustnote.superwallet.util.Prefs
import org.trustnote.superwallet.util.Utils

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

