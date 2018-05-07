package org.trustnote.wallet.tx

import org.trustnote.db.*
import org.trustnote.wallet.TApp

class TxParser {

    @Suppress("UNCHECKED_CAST")
    fun getTxs(walletId: String): List<Tx> {
        //TODO: add the asset logic according JS.
        val assocMovements = HashMap<String, HashMap<String, Any>>()
        val txUnits = DbHelper.getTxs(walletId)
        for (txUnit in txUnits) {
            if (!assocMovements.containsKey(txUnit.unit)) {
                val initMap = HashMap<String, Any>()
                initMap["plus"] = 0L
                initMap["has_minus"] = false
                initMap["ts"] = txUnit.ts
                initMap["level"] = txUnit.level
                initMap["is_stable"] = txUnit.iisStable
                initMap["sequence"] = txUnit.sequence
                initMap["fee"] = txUnit.fee
                initMap["mci"] = txUnit.mci
                assocMovements[txUnit.unit] = initMap
            }
            val currentAssocMovement = assocMovements[txUnit.unit]!!
            if (txUnit.toAddress.isNotEmpty()) {
                currentAssocMovement["plus"] = (currentAssocMovement["plus"] as Long) + txUnit.amount
                if (!currentAssocMovement.containsKey("arrMyRecipients")) {
                    currentAssocMovement["arrMyRecipients"] = mutableListOf<HashMap<String, Any>>()
                }

                val myRecipients = HashMap<String, Any>()
                myRecipients["my_address"] = txUnit.toAddress
                myRecipients["amount"] = txUnit.amount

                (currentAssocMovement["arrMyRecipients"] as MutableList<HashMap<String, Any>>).add(myRecipients)
            }

            if (txUnit.fromAddress.isNotEmpty()) {
                currentAssocMovement["has_minus"] = true
            }
        }

        val res = mutableListOf<Tx>()

        assocMovements.forEach {
            val movement = it.value
            val unitId = it.key
            if (DbConst.UNIT_SEQUENCE_GOOD != it.value["sequence"]) {
                val transaction = Tx(action = TxType.invalid,
                        confirmations = movement["is_stable"] as Int,
                        unit = it.key,
                        fee = movement["fee"] as Long,
                        ts = movement["ts"] as Long,
                        level = movement["level"] as Int,
                        mci = movement["mci"] as Int)
                res.add(transaction)
            } else if (movement["plus"] as Long > 0 && !(movement["has_minus"] as Boolean)) {
                val arrPayerAddresses = DbHelper.queryInputAddresses(unitId)
                (movement["arrMyRecipients"] as MutableList<HashMap<String, Any>>).forEach {
                    val objRecipient = it
                    val transaction = Tx(action = TxType.received,
                            amount = objRecipient["amount"] as Long,
                            myAddress = objRecipient["my_address"] as String,
                            arrPayerAddresses = arrPayerAddresses,
                            confirmations = movement["is_stable"] as Int,
                            unit = unitId,
                            fee = movement["fee"] as Long,
                            ts = movement["ts"] as Long,
                            level = movement["level"] as Int,
                            mci = movement["mci"] as Int)
                    res.add(transaction)
                }
            } else if (movement["has_minus"] as Boolean) {

                val payee_rows = DbHelper.queryOutputAddress(unitId, walletId)
                val txType = if (payee_rows.any { it.isExternal }) TxType.sent else TxType.moved
                payee_rows.forEach {
                    if (txType == TxType.sent && !it.isExternal) {
                        //Do nothing
                    } else {
                        val transaction = Tx(action = txType,
                                amount = it.amount,
                                addressTo = it.address,
                                confirmations = movement["is_stable"] as Int,
                                unit = unitId,
                                fee = movement["fee"] as Long,
                                ts = movement["ts"] as Long,
                                level = movement["level"] as Int,
                                mci = movement["mci"] as Int)
                        if (txType == TxType.moved) {
                            transaction.myAddress = it.address
                        }
                        res.add(transaction)
                    }
                }
            }
        }

        return res
    }
}