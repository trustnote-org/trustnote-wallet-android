package org.trustnote.db

import com.google.gson.*
import io.reactivex.Observable
import org.trustnote.db.dao.UnitsDao
import org.trustnote.db.entity.*
import org.trustnote.wallet.TApp
import org.trustnote.wallet.network.hubapi.HubResponse
import org.trustnote.wallet.pojo.Credential
import org.trustnote.wallet.util.Utils

object DbHelper {
    fun saveUnit(hubResponse: HubResponse) = saveUnitInternal(hubResponse)
    fun saveWalletMyAddress(credential: Credential) = saveWalletMyAddressInternal(credential)
    fun saveMyWitnesses(hubResponse: HubResponse) = saveMyWitnessesInternal(hubResponse)
    fun getMyWitnesses(): Array<MyWitnesses> = getMyWitnessesInternal()
    fun getAllWalletAddress(walletId: String): Array<MyAddresses> = getAllWalletAddressInternal(walletId)
    fun getAllWalletAddress(): Array<MyAddresses> = getAllWalletAddressInternal()
    fun monitorAddresses(): Observable<Array<MyAddresses>> = monitorAddressesInternal()
    fun monitorUnits(): Observable<Array<Units>> = monitorUnitsInternal()
    fun monitorOutputs(): Observable<Array<Outputs>> = monitorOutputsInternal()

    fun shouldGenerateMoreAddress(walletId: String): Boolean = shouldGenerateMoreAddressInternal(walletId)
    fun getMaxAddressIndex(walletId: String, isChange: Int): Int = getMaxAddressIndexInternal(walletId, isChange)
    fun shouldGenerateNextWallet(walletId: String): Boolean = shouldGenerateNextWalletInternal(walletId)

    //Balance and tx history
    fun getBanlance(walletId: String): List<Balance> = getBanlanceInternal(walletId)

    fun fixIsSpentFlag() = getDao().fixIsSpentFlag()

    fun getTxs(walletId: String): List<Tx> = getTxsInternal(walletId)

}

fun getTxsInternal(walletId: String): List<Tx> {
    //TODO: add the asset logic according JS.
    val dao = TrustNoteDataBase.getInstance(TApp.context).unitsDao()
    val txUnits = dao.queryTxUnits(walletId)
    val assocMovements = HashMap<String, HashMap<String, Any>>()
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
            val transaction = Tx(action = TxType.invalid, confirmations = movement["is_stable"] as Int,
                    unit = it.key, fee = movement["fee"] as Long, ts = movement["ts"] as Long,
                    level = movement["level"] as Int, mci = movement["mci"] as Int)
            res.add(transaction)
        } else if (movement["plus"] as Long > 0 && !(movement["has_minus"] as Boolean)) {
            val arrPayerAddresses = dao.queryInputAddresses(unitId)
            (movement["arrMyRecipients"] as MutableList<HashMap<String, Any>>).forEach {
                val objRecipient = it
                val transaction = Tx(action = TxType.received, amount = objRecipient["amount"] as Long, myAddress = objRecipient["my_address"] as String, arrPayerAddresses = arrPayerAddresses, confirmations = movement["is_stable"] as Int, unit = unitId, fee = movement["fee"] as Long, ts = movement["ts"] as Long, level = movement["level"] as Int, mci = movement["mci"] as Int)
                res.add(transaction)
            }
        } else if (movement["has_minus"] as Boolean) {
            val payee_rows = dao.queryOutputAddress(unitId, walletId)
            val txType = if (payee_rows.any { it.isExternal }) TxType.sent else TxType.moved
            payee_rows.forEach {
                if (txType == TxType.sent && !it.isExternal) {
                    //Do nothing
                } else {
                    val transaction = Tx(action = txType, amount = it.amount, addressTo = it.address, confirmations = movement["is_stable"] as Int, unit = unitId, fee = movement["fee"] as Long, ts = movement["ts"] as Long, level = movement["level"] as Int, mci = movement["mci"] as Int)
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


fun getDao(): UnitsDao {
    return TrustNoteDataBase.getInstance(TApp.context).unitsDao()
}

fun getBanlanceInternal(walletId: String): List<Balance> {
    val db = TrustNoteDataBase.getInstance(TApp.context)
    val res = db.unitsDao().queryBalance(walletId)
    return res.toList()
}

fun getMaxAddressIndexInternal(walletId: String, change: Int): Int {
    val db = TrustNoteDataBase.getInstance(TApp.context)
    val max = db.unitsDao().getMaxAddressIndex(walletId, change)
    return if (max > 0) max + 1 else max
}

fun shouldGenerateNextWalletInternal(walletId: String): Boolean {
    val db = TrustNoteDataBase.getInstance(TApp.context)
    return db.unitsDao().shouldGenerateNextWallet(walletId)
}


fun shouldGenerateMoreAddressInternal(walletId: String): Boolean {
    val db = TrustNoteDataBase.getInstance(TApp.context)
    return db.unitsDao().shouldGenerateMoreAddress(walletId)
}

fun getAllWalletAddressInternal(walletId: String): Array<MyAddresses> {
    val db = TrustNoteDataBase.getInstance(TApp.context)
    return db.unitsDao().queryAllWalletAddress(walletId)
}

fun getAllWalletAddressInternal(): Array<MyAddresses> {
    val db = TrustNoteDataBase.getInstance(TApp.context)
    return db.unitsDao().queryAllWalletAddress()
}

fun getMyWitnessesInternal(): Array<MyWitnesses> {
    val db = TrustNoteDataBase.getInstance(TApp.context)
    return db.unitsDao().queryMyWitnesses()
}

fun saveWalletMyAddressInternal(credential: Credential) {
    val db = TrustNoteDataBase.getInstance(TApp.context)
    db.unitsDao().insertMyAddresses(credential.myAddresses.toTypedArray())
}

fun monitorAddressesInternal(): Observable<Array<MyAddresses>> {
    val db = TrustNoteDataBase.getInstance(TApp.context)
    return Utils.throttleDbEvent(db.unitsDao().monitorAddresses().toObservable(), 3L)
}

fun monitorUnitsInternal(): Observable<Array<Units>> {
    val db = TrustNoteDataBase.getInstance(TApp.context)
    return Utils.throttleDbEvent(db.unitsDao().monitorUnits().toObservable(), 3L)
}

fun monitorOutputsInternal(): Observable<Array<Outputs>> {
    val db = TrustNoteDataBase.getInstance(TApp.context)
    return Utils.throttleDbEvent(db.unitsDao().monitorOutputs().toObservable(), 3L)
}

fun saveMyWitnessesInternal(hubResponse: HubResponse) {

    val db = TrustNoteDataBase.getInstance(TApp.context)

    var myWitnesses = parseArray(hubResponse.responseJson as JsonArray, String::class.java.simpleName)
    db.unitsDao().saveMyWitnesses(myWitnesses.mapToTypedArray {
        val res = MyWitnesses()
        res.address = it as String
        res
    })
}


inline fun <T, reified R> List<T>.mapToTypedArray(transform: (T) -> R): Array<R> {
    return when (this) {
        is RandomAccess -> Array(size) { index -> transform(this[index]) }
        else -> with(iterator()) { Array(size) { transform(next()) } }
    }
}

fun parseArray(origJson: JsonArray, clzFullName: String): List<Any> {
    var gson = Utils.getGson()

    val res = List(origJson.size()) { index: Int ->
        if (clzFullName == String::class.java.simpleName) {
            origJson[index].asString
        } else {
            gson.fromJson(origJson[index], Class.forName(clzFullName))
        }
    }

    return res
}

fun parseChild(parentEntity: TBaseEntity, origJson: JsonObject, clzFullName: String, vararg childJsonKey: String): List<Any> {
    var gson = Utils.getGson()

    assert(childJsonKey.isNotEmpty())
    var childrenAsJsonArray: JsonArray

    if (childJsonKey.size == 1) {
        childrenAsJsonArray = origJson.getAsJsonArray(childJsonKey[0])
    } else {
        var json = origJson
        for (index in 0..childJsonKey.size - 2) {
            json = json.getAsJsonObject(childJsonKey[index])
        }
        childrenAsJsonArray = json.getAsJsonArray(childJsonKey[childJsonKey.size - 1])
    }

    val children = List(childrenAsJsonArray.size()) { index: Int ->
        val child = gson.fromJson(childrenAsJsonArray[index], Class.forName(clzFullName)) as TBaseEntity
        child.json = childrenAsJsonArray[index].asJsonObject
        child.parentJson = origJson
        child.parent = parentEntity

        child
    }
    return children
}


fun saveUnitInternal(hubResponse: HubResponse) {
    //TODO: too much tedious work.
    //TODO: save data to table units_authors??
    val response = hubResponse.msgJson!!.getAsJsonObject("response")
    val jointList = parseChild(TBaseEntity.VoidEntity, response, Joints::class.java.canonicalName, "joints") as List<Joints>

    for (joint in jointList) {
        val units = joint.unit
        units.json = joint.json.getAsJsonObject("unit")

        units.sequence = DbConst.UNIT_SEQUENCE_GOOD

        val authentifiersArray = parseChild(units, units.json, Authentifiers::class.java.canonicalName, "authors") as List<Authentifiers>
        authentifiersArray.forEachIndexed { index, authentifier ->
            authentifier.unit = units.unit; authentifier.parsePathAndAuthentifier()
        }
        units.authenfiers = authentifiersArray


        val messageArray = parseChild(units, units.json, Messages::class.java.canonicalName, "messages") as List<Messages>

        messageArray.forEachIndexed { index, messages -> messages.messageIndex = index }
        units.messages = messageArray

        for (message in messageArray) {
            message.parent = units
            message.unit = units.unit
            val inputArray = parseChild(message, message.json, Inputs::class.java.canonicalName, "payload", "inputs") as List<Inputs>


            val outputArray = parseChild(message, message.json, Outputs::class.java.canonicalName, "payload", "outputs") as List<Outputs>


            inputArray.forEachIndexed { index, inputs ->
                inputs.unit = units.unit
                inputs.messageIndex = message.messageIndex
                inputs.inputIndex = index
                //TODO: check JS code.
                inputs.address = units.authenfiers[0].address
            }

            outputArray.forEachIndexed { index, outputs ->
                outputs.unit = units.unit;outputs.messageIndex = message.messageIndex; outputs.outputIndex = index
            }

            message.inputs = inputArray

            message.outputs = outputArray
        }
    }


    val db = TrustNoteDataBase.getInstance(TApp.context)
    db.unitsDao().saveUnits(jointList.mapToTypedArray { it.unit })

}
