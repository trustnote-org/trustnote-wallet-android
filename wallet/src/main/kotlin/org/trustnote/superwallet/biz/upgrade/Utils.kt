package org.trustnote.superwallet.biz.upgrade

import org.trustnote.superwallet.BuildConfig
import org.trustnote.superwallet.biz.wallet.WalletManager
import org.trustnote.superwallet.network.pojo.WalletNewVersion
import org.trustnote.superwallet.util.Prefs
import org.trustnote.superwallet.util.Utils

fun newVersionFound(asString: String) {

    if (asString.isNotEmpty()) {
        val walletNewVersion = Utils.getGson().fromJson(asString, WalletNewVersion::class.java)

        if (isNewerVersion(walletNewVersion.version)) {
            Prefs.writeUpgradeInfo(asString)
            WalletManager.mUpgradeEventCenter.onNext(true)
        }
    }
}

fun isNewerVersion(version: String): Boolean {
    //TODO: need more code
    return !BuildConfig.VERSION_NAME.startsWith(version)
}
