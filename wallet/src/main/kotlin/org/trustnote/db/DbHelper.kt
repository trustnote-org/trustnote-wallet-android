package org.trustnote.db

import com.google.gson.*
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
}

fun getAllWalletAddressInternal(walletId: String): Array<MyAddresses>{
    val db = TrustNoteDataBase.getInstance(TApp.context)
    return db.unitsDao().queryAllWalletAddress(walletId)
}

fun getMyWitnessesInternal(): Array<MyWitnesses>{
    val db = TrustNoteDataBase.getInstance(TApp.context)
    return db.unitsDao().queryMyWitnesses()
}

fun saveWalletMyAddressInternal(credential: Credential) {
    val db = TrustNoteDataBase.getInstance(TApp.context)
    db.unitsDao().insertMyAddresses(credential.myAddresses.toTypedArray())
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


fun saveUnitInternal(hubResponse: HubResponse) {

    val response = hubResponse.msgJson!!.getAsJsonObject("response")
    val gson = Utils.getGson()
    val jointList = parseChildFromJson(gson, TBaseEntity.VoidEntity, response, Joints::class.java.canonicalName, "joints") as List<Joints>

    for (joint in jointList) {
        val units = joint.unit
        units.originalJson = joint.originalJson.getAsJsonObject("unit")
        val messageArray = parseChildFromJson(gson, units, units.originalJson, Messages::class.java.canonicalName, "messages") as List<Messages>
        units.messages = messageArray
        for (message in messageArray) {
            message.parent = units
            message.unit = units.unit
            val inputArray = parseChildFromJson(gson, message, message.originalJson, Inputs::class.java.canonicalName, "payload", "inputs") as List<Inputs>

            val outputArray = parseChildFromJson(gson, message, message.originalJson, Outputs::class.java.canonicalName, "payload", "outputs") as List<Outputs>

            inputArray.onEach { it.unit = units.unit }
            message.inputs = inputArray

            outputArray.onEach { it.unit = units.unit }
            message.outputs = outputArray
        }
    }


    val db = TrustNoteDataBase.getInstance(TApp.context)
    db.unitsDao().saveUnits(jointList.mapToTypedArray { it.unit })

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

fun parseChildFromJson(gson: Gson, parentEntity: TBaseEntity, origJson: JsonObject, clzFullName: String, vararg childJsonKey: String): List<Any> {

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
        child.originalJson = childrenAsJsonArray[index].asJsonObject
        child.parentJson = origJson
        child.parent = parentEntity

        child
    }
    return children
}

