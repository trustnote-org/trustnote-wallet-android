package org.trustnote.wallet.js

import org.trustnote.wallet.util.Utils


object JsTest {
    fun createFullWallet() = createFullWallet(seed)
    var seed = "theme wall plunge fluid circle organ gloom expire coach patient neck clip"

}

fun createFullWallet(seed: String) {
    Thread {
        createFullWalletInternal(seed)
    }.start()
}

fun createFullWalletInternal(seed: String) {
    val api = JSApi()
    val privKey = api.xPrivKeySync(seed)

    val walletPubKey = api.walletPubKeySync(privKey, 0)
    val walletId = api.walletIDSync(walletPubKey)

    val walletAddresses = List(5, {
        val oneAddress = api.walletAddressSync(walletPubKey, it)
        val onePubKey = api.walletAddressPubkeySync(walletPubKey, it)
    })


    Utils.debugJS("Done")

}