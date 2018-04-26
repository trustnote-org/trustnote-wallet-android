package org.trustnote.wallet.pojo

import org.trustnote.db.Balance
import org.trustnote.db.Tx
import org.trustnote.db.entity.MyAddresses
import org.trustnote.db.entity.Outputs
import java.math.BigInteger
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
        val ecdsaPubkey: String = "",
        var currentWalletIndex: Int = 0
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
        val myAddresses: MutableSet<MyAddresses> = mutableSetOf(),
        var balance: Long = 0, //TODO: should BigInteger
        var balanceDetails: List<Balance> = listOf(),
        var txDetails: List<Tx> = listOf()
) {
    override fun toString(): String {
        var res = "WalletName: $walletName\n\rBalance: $balance\n\rTransactions: \n\r------------------------\n\r"
        txDetails.forEach {
            res += it.toString() + "------------------------\n\r"
        }
        return res
    }
}

data class PublicKeyRing(
        val xPubKey: String = ""
)