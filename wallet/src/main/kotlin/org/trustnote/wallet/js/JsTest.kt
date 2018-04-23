package org.trustnote.wallet.js

import org.trustnote.wallet.TTT
import org.trustnote.wallet.util.Utils
import org.trustnote.wallet.walletadmin.TestData
import org.trustnote.wallet.walletadmin.WalletModel
import timber.log.Timber


object JsTest {
    fun createFullWallet() = createFullWallet(seed)
    fun findVanityAddress(target: String) = findVanityAddressBg(target)
    var seed = TestData.mnemonic
}

fun createFullWallet(seed: String) {
    Thread {
        createFullWalletInternal(seed)
    }.start()
}

fun findVanityAddressBg(target: String) {
    Thread {
        findVanityAddressInternal(target)
    }.start()
}

fun findVanityAddressInternal(target: String) {
    val api = JSApi()
    var mnemonic = api.mnemonicSync()
    var isFound = false
    while (!isFound) {
        val mnemonic = api.mnemonicSync()
        val privKey = api.xPrivKeySync(mnemonic)

        val walletPubKey = api.walletPubKeySync(privKey, 0)
        val walletId = api.walletIDSync(walletPubKey)

        val walletAddresses = List(20, {
            val oneAddress = api.walletAddressSync(walletPubKey, TTT.addressReceiveType, it)
            Utils.debugJS(oneAddress)

            isFound = isVanity(target, oneAddress)

            if (isFound) {
                Timber.d(mnemonic)
                Timber.d(oneAddress)
            }
            //val onePubKey = api.walletAddressPubkeySync(walletPubKey, it)
        })
    }

    Utils.debugJS("$mnemonic")
    Utils.debugJS("Done")
}

fun isVanity(target: String, oneAddress: String): Boolean {
    return oneAddress.startsWith("\"" + target)
}

fun createFullWalletInternal(seed: String) {

    WalletModel.instance.createProfileFromMnenonic(seed)

    Utils.debugJS("Done")

}