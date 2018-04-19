package org.trustnote.db

import com.google.gson.*
import org.trustnote.db.entity.*
import org.trustnote.wallet.TApp
import org.trustnote.wallet.network.hubapi.HubResponse
import org.trustnote.wallet.network.hubapi.Joints


object DbHelper {
    fun saveUnit(hubResponse: HubResponse) = saveUnitInternal(hubResponse)
}

fun saveUnitInternal(hubResponse: HubResponse) {

    val response = hubResponse.body.getAsJsonObject("response")
    val gson = getGson()
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

fun getGson(): Gson {
    return GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create()
}