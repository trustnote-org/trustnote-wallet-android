package org.trustnote.wallet.biz.wallet

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import org.trustnote.db.Balance
import org.trustnote.db.Tx
import org.trustnote.db.entity.MyAddresses
import java.util.*
import com.google.gson.JsonParser
import com.google.gson.annotations.Expose
import org.trustnote.wallet.TTT

class TProfile() {

    var mnemonic: String = ""
    var version: String = "1.0"
    var createdOn: Long = Date().time
    var credentials: MutableList<Credential> = mutableListOf()
    var xPrivKey: String = ""
    var tempDeviceKey: String = ""
    var prevTempDeviceKey: String = ""
    var deviceAddress: String = ""
    var ecdsaPubkey: String = ""
    var currentWalletIndex: Int = 0
    var mnemonicType: MNEMONIC_TYPE = MNEMONIC_TYPE.UNKNOWN
    var keyDb: String = "db"
    var balance: Long = 0

}

class Credential {

    var walletId: String = ""
    val network: String = "TestNet"//TODO:
    var xPubKey: String = ""
    val publicKeyRing: List<PublicKeyRing> = ArrayList()
    var walletName: String = ""
    val m: Int = 0
    val n: Int = 0
    val derivationStrategy: String = TTT.HD_DERIVATION_STRATEGY
    var account: Int = 0
    var balance: Long = 0
    var isLocal: Boolean = false

    @Expose(serialize = false, deserialize = false)
    @Transient
    var txDetails: MutableList<Tx> = mutableListOf()
    @Expose(serialize = false, deserialize = false)
    @Transient
    var balanceDetails: List<Balance> = listOf()
    @Expose(serialize = false, deserialize = false)
    @Transient
    val myAddresses: MutableSet<MyAddresses> = mutableSetOf()
    @Expose(serialize = false, deserialize = false)
    @Transient
    val myReceiveAddresses: MutableSet<MyAddresses> = mutableSetOf()
    @Expose(serialize = false, deserialize = false)
    @Transient
    val myChangeAddresses: MutableSet<MyAddresses> = mutableSetOf()

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

enum class MNEMONIC_TYPE {
    RANDOM_GEN,
    RESTORE,
    UNKNOWN
}

enum class CREATE_WALLET_STATUS {
    DIDNOT_AGREE,
    DEVICE_NAME_FINISHED,
    SELECT_CREATE_OR_RESTORE,
    PASSWORD_READY,
    FINISHED
}
