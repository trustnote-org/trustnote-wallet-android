package org.trustnote.wallet

import org.trustnote.wallet.js.JsTest

class TTT {

    companion object {
        val instance = TTT()
        const val firstWalletName = "TTT"
        const val walletAddressInitSize = 20
        const val addressReceiveType = 0
        const val addressChangeType = 1
    }

    fun createFromSeed(seed: String) {
        JsTest
    }
}