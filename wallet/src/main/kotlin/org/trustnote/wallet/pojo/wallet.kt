package org.trustnote.wallet.pojo

import java.util.*


data class TProfile(
        val mnemonic: String,
        val version: String = "1.0",
        val createdOn: Long = Date().time,
        val credentials: ArrayList<Credential>,
        val xPrivKey: String,
        val tempDeviceKey: String = "",
        val prevTempDeviceKey: String = "",
        val my_device_address: String,
        val ecdsaPubkey: String = ""
)

data class Credential(
        val walletId: String,
        val network: String = "TestNet",//TODO:
        val xPubKey: String,
        val publicKeyRing: List<PublicKeyRing> = ArrayList(),
        val walletName: String,
        val m: Int = 0,
        val n: Int = 0,
        val derivationStrategy: String = "BIP44",
        val account: Int = 0
)

data class PublicKeyRing(
        val xPubKey: String = ""
)