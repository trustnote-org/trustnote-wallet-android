package org.trustnote.db

import com.google.gson.*
import io.reactivex.Observable
import org.trustnote.db.dao.UnitsDao
import org.trustnote.db.entity.*
import org.trustnote.wallet.TApp
import org.trustnote.wallet.TTT
import org.trustnote.wallet.network.pojo.HubResponse
import org.trustnote.wallet.pojo.Credential
import org.trustnote.wallet.pojo.InputOfPayment
import org.trustnote.wallet.pojo.SendPaymentInfo
import org.trustnote.wallet.util.Utils

@Suppress("UNCHECKED_CAST")
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

    fun getTxs(walletId: String): List<TxUnits> = getTxsInternal(walletId)

    fun findInputsForPayment(sendPaymentInfo: SendPaymentInfo) = findInputsForPaymentInternal(sendPaymentInfo)

    fun queryAddress(addressList: List<String>) = queryAddressInternal(addressList)

    fun queryAddressByAddresdId(addressId: String) = queryAddressByAddresdIdInternal(addressId)

    fun queryAddressByWalletId(walletId: String) = queryAddressByWalletIdInternal(walletId)
    fun queryOutputAddress(unitId: String, walletId: String): List<TxOutputs> {
        return getDao().queryOutputAddress(unitId, walletId).asList()
    }

    fun queryInputAddresses(unitId: String): Array<String> {
        return getDao().queryInputAddresses(unitId)
    }

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

@Suppress("UNCHECKED_CAST")
fun saveUnitInternal(hubResponse: HubResponse) {
    //TODO: too much tedious work.
    //TODO: save data to table units_authors??
    val response = hubResponse.msgJson.getAsJsonObject("response")
    val jointList = parseChild(TBaseEntity.VoidEntity, response, Joints::class.java.canonicalName, "joints") as List<Joints>

    for (joint in jointList) {
        val units = joint.unit
        units.json = joint.json.getAsJsonObject("unit")

        //TODO: when to stable?
        units.isStable = 1

        units.sequence = DbConst.UNIT_SEQUENCE_GOOD

        val authentifiersArray = parseChild(units, units.json, Authentifiers::class.java.canonicalName, "authors") as List<Authentifiers>
        authentifiersArray.forEachIndexed { _, authentifier ->
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

fun filterMostFundedAddresses(rows: Array<FundedAddress>, estimatedAmount: Long): List<FundedAddress> {
    if (estimatedAmount <= 0) {
        return rows.asList()
    }
    val res = mutableListOf<FundedAddress>()
    var accumulatedAmount = 0L

    rows.forEach {
        res.add(it)
        accumulatedAmount += it.total
        if (accumulatedAmount > estimatedAmount + TTT.MAX_FEE) {
            return res
        }
    }
    return res
}

fun findInputsForPaymentInternal(sendPaymentInfo: SendPaymentInfo): List<InputOfPayment> {
    val res = mutableListOf<InputOfPayment>()

    val fundedAddress = getDao().queryFundedAddressesByAmount(sendPaymentInfo.walletId, sendPaymentInfo.amount)
    val filterFundedAddress = filterMostFundedAddresses(fundedAddress, sendPaymentInfo.amount)
    val addresses = mutableListOf<String>()
    filterFundedAddress.forEach {addresses.add(it.address)}

    val outputs = getDao().queryUtxoByAddress(addresses, sendPaymentInfo.lastBallMCI)
    outputs.forEach {
        res.add(InputOfPayment(
                unit = it.unit,
                messageIndex = it.messageIndex,
                outputIndex = it.outputIndex,
                amount = it.amount,
                address = it.address
        ))
    }

    return res
}

fun queryAddressInternal(addressList: List<String>): Array<MyAddresses> {
    return getDao().queryAddress(addressList)
}

fun queryAddressByWalletIdInternal(walletId: String): Array<MyAddresses> {
    return getDao().queryAddressByWalletId(walletId)
}


fun queryAddressByAddresdIdInternal(addressId: String): MyAddresses {

    //How about query with no result.
    val res = getDao().queryAddress(listOf<String>(addressId))
    return res[0]
}

private fun getTxsInternal(walletId: String): List<TxUnits> {
    val dao = TrustNoteDataBase.getInstance(TApp.context).unitsDao()
    return dao.queryTxUnits(walletId).asList()
}
