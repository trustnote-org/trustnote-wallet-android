package org.trustnote.wallet.biz

import org.trustnote.wallet.BuildConfig

class TTT {

    companion object {
        val instance = TTT()
        const val firstWalletName = "TTT"
        const val walletAddressInitSize = 21
        const val walletAddressIncSteps = 20
        const val addressReceiveType = 0
        const val addressChangeType = 1

        const val HUB_HEARTBEAT_FIRST_DELAY_SEC_MAX = 10
        const val HUB_HEARTBEAT_INTERVAL_SEC = 10
        const val HUB_REQ_RETRY_SECS = 15
        const val HUB_REQ_RETRY_CHECK_SECS = 10L
        const val HUB_WAITING_SECONDS_RECCONNECT = 15L

        const val COUNT_WITNESSES = 12
        const val MAX_WITNESS_LIST_MUTATIONS = 1
        const val TOTAL_WHITEBYTES = 5e14
        val MAJORITY_OF_WITNESSES = if (COUNT_WITNESSES % 2 == 0) (COUNT_WITNESSES / 2 + 1) else COUNT_WITNESSES / 2
        const val COUNT_MC_BALLS_FOR_PAID_WITNESSING = 100

        const val version = "1.0"
        const val alt = "1"
        const val unitMsgTypePayment = "payment"
        const val unitPayloadLoationInline = "inline"

        const val GENESIS_UNIT = "rg1RzwKwnfRHjBojGol3gZaC5w7kR++rOR6O61JRsrQ="
        const val BLACKBYTES_ASSET = "9qQId3BlWRQHvVy+STWyLKFb3lUd0xfQhX6mPVEHC2c="

        const val HASH_LENGTH = 44
        const val PUBKEY_LENGTH = 44
        const val SIG_LENGTH = 88

        // anti-spam limits
        const val MAX_AUTHORS_PER_UNIT = 16
        const val MAX_PARENTS_PER_UNIT = 16
        const val MAX_MESSAGES_PER_UNIT = 128
        const val MAX_SPEND_PROOFS_PER_MESSAGE = 128
        const val MAX_INPUTS_PER_PAYMENT_MESSAGE = 128
        const val MAX_OUTPUTS_PER_PAYMENT_MESSAGE = 128
        const val MAX_CHOICES_PER_POLL = 128
        const val MAX_DENOMINATIONS_PER_ASSET_DEFINITION = 64
        const val MAX_ATTESTORS_PER_ASSET = 64
        const val MAX_DATA_FEED_NAME_LENGTH = 64
        const val MAX_DATA_FEED_VALUE_LENGTH = 64
        const val MAX_AUTHENTIFIER_LENGTH = 4096
        const val MAX_CAP = 9e15
        const val MAX_COMPLEXITY = 100


        const val w_requiredCosigners = 2
        const val w_totalCosigners = 3
        const val w_spendUnconfirmed = false
        const val w_reconnectDelay = 5000
        const val w_idleDurationMin = 4
        const val w_coinunitName = "MN"
        const val w_coinunitValue = 1000000
        const val w_coinunitDecimals = 6
        const val w_coinunitCode = "mega"
        const val w_coinbbUnitName = "blacknotes"
        const val w_coinbbUnitValue = 1
        const val w_coinbbUnitDecimals = 0
        const val w_coinbbUnitCode = "one"
        const val w_coinalternativeName = "US Dollar"
        const val w_coinalternativeIsoCode = "USD"

        const val isTestnet = BuildConfig.FLAVOR == "devnet"

        val hubArrayForTestNet = arrayOf("shawtest.trustnote.org", "raytest.trustnote.org")
        val hubArrayForMainNet = arrayOf("victor.trustnote.org/tn",
                "eason.trustnote.org/tn",
                "lymn.trustnote.org/tn",
                "bob.trustnote.org/tn",
                "curry.trustnote.org/tn",
                "kake.trustnote.org/tn")

        //TODO:
        val hubAddress = if (isTestnet) "raytest.trustnote.org" else "eason.trustnote.org/tn"

        const val TYPICAL_FEE = 1000L
        const val MAX_FEE = 20000L

        // 256 bits (32 bytes) base64: 44 bytes
        const val PLACEHOLDER_TTT: String = "TTT_Test"
        const val PLACEHOLDER_AMOUNT = 111L
        const val PLACEHOLDER_HASH = "--------------------------------------------"
        // 88 bytes
        const val PLACEHOLDER_SIG = "----------------------------------------------------------------------------------------"

        const val HD_DERIVATION_STRATEGY: String = "BIP44"

        const val KEY_TX_INDEX = "TX_INDEX"
        const val KEY_QR_CODE = "KEY_QR_CODE"
        const val KEY_WALLET_ID = "KEY_WALLET_ID"
        const val KEY_TRANSFER_QRCODEW = "KEY_TRANSFER_QRCODEW"

        const val KEY_TTT_QR_TAG = "TTT"

    }
}




