package org.trustnote.wallet.biz.wallet

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import org.trustnote.db.Balance
import org.trustnote.db.Tx
import org.trustnote.db.entity.MyAddresses
import java.util.*
import com.google.gson.JsonParser
import com.google.gson.annotations.Expose
import org.trustnote.wallet.biz.TTT
import org.trustnote.wallet.util.TTTUtils

class TProfile {

    var mnemonic: String = ""
    var version: String = "1.0"
    var createdOn: Long = Date().time
    var credentials: MutableList<Credential> = mutableListOf()
    var xPrivKey: String = ""
    var deviceAddress: String = ""
    var currentWalletIndex: Int = 0
    var mnemonicType: MNEMONIC_TYPE = MNEMONIC_TYPE.UNKNOWN
    var dbTag: String = "db"
    var balance: Long = 0
    var removeMnemonic: Boolean = false
    var pubKeyForPairId: String = ""
    var privKeyForPairId: String = ""
    var hubIndexForPairId: Int = 0

    var tempPubkey: String = ""
    var prevTempPubkey: String = ""

    var tempPrivkey: String = ""
    var prevTempPrivkey: String = ""

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
    var isObserveOnly: Boolean = false
    var isAuto: Boolean = false
    var isLastRefreshOk = false
    var isRemoved: Boolean = false
    var deviceAddressFromObserved: String = ""
    var defaultReceiveAddress: String = ""

    @Expose(serialize = false, deserialize = false)
    @Transient
    var txDetails: List<Tx> = listOf()

    @Expose(serialize = false, deserialize = false)
    @Transient  //TODO: Balance is not utxo
    var balanceDetails: List<Balance> = listOf()
    @Expose(serialize = false, deserialize = false)
    @Transient
    var myAddresses = listOf<MyAddresses>()
    @Expose(serialize = false, deserialize = false)
    @Transient
    var myReceiveAddresses = listOf<MyAddresses>()
    @Expose(serialize = false, deserialize = false)
    @Transient
    var myChangeAddresses = listOf<MyAddresses>()


    override fun toString(): String {
        var res = "WalletName: $walletName\n\rBalance: $balance\n\rTransactions: \n\r------------------------\n\r"
        txDetails.forEach {
            res += it.toString() + "------------------------\n\r"
        }
        return res
    }

    override fun hashCode(): Int {
        return walletId.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        return (other is Credential && walletId == other.walletId)
    }
}

data class PublicKeyRing(
        val xPubKey: String = ""
)

data class PaymentInfo(
        var walletId: String = "",
        var receiverAddress: String = "",
        var amount: Long = 0,
        //TODO: remove below field.
        var lastBallMCI: Int = 0,
        var textMessage: String = ""
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
