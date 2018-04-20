package org.trustnote.wallet.pojo

import org.trustnote.db.entity.MyAddresses
import java.util.*


data class TProfile(
        val mnemonic: String,
        val version: String = "1.0",
        val createdOn: Long = Date().time,
        val credentials: MutableList<Credential> = mutableListOf(),
        val xPrivKey: String,
        val tempDeviceKey: String = "",
        val prevTempDeviceKey: String = "",
        val deviceAddress: String,
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
        val account: Int = 0,
        val myAddresses: MutableList<MyAddresses> = mutableListOf()
)

data class PublicKeyRing(
        val xPubKey: String = ""
)