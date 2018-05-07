package org.trustnote.db

import android.arch.persistence.room.ColumnInfo

data class Tx(
        val action: TxType = TxType.invalid,
        val confirmations: Int = 0,
        val unit: String,
        val fee: Long = 0,
        val level: Int,
        val mci: Int,
        val amount: Long = 0,
        var myAddress: String = "",
        val arrPayerAddresses: Array<String> = arrayOf<String>(),
        val ts: Long = 0,
        val addressTo: String = ""
) {
    override fun toString(): String {
        return "Type: $action \n\rAmount: $amount\n\rMe: $myAddress\n\rTo: $addressTo\n\r"
    }
}


class TxUnits {
    @ColumnInfo(name = "unit")
    var unit: String = ""
    @ColumnInfo(name = "level")
    var level: Int = 0
    @ColumnInfo(name = "is_stable")
    var iisStable: Int = 0
    @ColumnInfo(name = "sequence")
    var sequence: String = ""
    @ColumnInfo(name = "address")
    var address: String = ""
    @ColumnInfo(name = "ts")
    var ts: Long = 0
    @ColumnInfo(name = "fee")
    var fee: Long = 0
    @ColumnInfo(name = "amount")
    var amount: Long = 0
    @ColumnInfo(name = "to_address")
    var toAddress: String = ""
    @ColumnInfo(name = "from_address")
    var fromAddress: String = ""
    @ColumnInfo(name = "mci")
    var mci: Int = 0
}

class TxOutputs {
    @ColumnInfo(name = "address")
    var address: String = ""
    @ColumnInfo(name = "amount")
    var amount: Long = 0
    @ColumnInfo(name = "is_external")
    var isExternal: Boolean = false
}

class FundedAddress {
    @ColumnInfo(name = "address")
    var address: String = ""
    @ColumnInfo(name = "total")
    var total: Long = 0
}

enum class TxType {
    invalid,
    received,
    sent,
    moved,
}
