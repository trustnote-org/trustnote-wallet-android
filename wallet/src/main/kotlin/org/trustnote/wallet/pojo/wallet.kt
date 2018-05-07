package org.trustnote.wallet.pojo

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import org.trustnote.db.Balance
import org.trustnote.db.Tx
import org.trustnote.db.entity.MyAddresses
import org.trustnote.db.entity.Outputs
import org.trustnote.wallet.util.Utils
import java.math.BigInteger
import java.util.*
import com.google.gson.JsonParser


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
        var txDetails: List<Tx> = listOf(),
        val isLocal: Boolean = false
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

data class SendPaymentInfo(
        val walletId: String,
        val receiverAddress: String,
        val amount: Long,
        //TODO: remove below field.
        var lastBallMCI: Int = 0
)

data class InputOfPayment(
        val unit: String,
        val messageIndex: Int,
        val outputIndex: Int,
        val amount: Long,
        val address: String
) {
    fun toJsonObject(): JsonObject {
        val res = JsonObject()
        res.addProperty("unit", unit)
        res.addProperty("message_index", messageIndex)
        res.addProperty("output_index", outputIndex)
        return res
    }
}

data class Author(
        //just support single signature address.
        val address: String,
        val authentifiers: String,
        val definitionOfPubkey: String

) {
    fun toJsonObject(): JsonObject {
        val res = JsonObject()
        res.addProperty("address", address)

        val jsonAuthentifiers = JsonObject()
        jsonAuthentifiers.addProperty("r", authentifiers)
        res.add("authentifiers", jsonAuthentifiers)

        val parser = JsonParser()
        val jsonArrayDefinition = parser.parse(definitionOfPubkey) as JsonArray

        res.add("definition", jsonArrayDefinition)

        return res
    }
}