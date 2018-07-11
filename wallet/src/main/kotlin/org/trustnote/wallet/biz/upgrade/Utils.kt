package org.trustnote.wallet.biz.upgrade

import org.trustnote.wallet.BuildConfig
import org.trustnote.wallet.biz.wallet.WalletManager
import org.trustnote.wallet.network.pojo.WalletNewVersion
import org.trustnote.wallet.util.Prefs
import org.trustnote.wallet.util.Utils

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
