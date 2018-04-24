package org.trustnote.wallet

class TTT {

    companion object {
        val instance = TTT()
        const val firstWalletName = "TTT"
        const val walletAddressInitSize = 20
        const val addressReceiveType = 0
        const val addressChangeType = 1
        const val testHubAddress = "wss://raytest.trustnote.org:443"

        const val HUB_HEARTBEAT_FIRST_DELAY_SEC = 2
        const val HUB_HEARTBEAT_INTERVAL_SEC = 10
        const val HUB_REQ_RETRY_SECS = 10


    }

}