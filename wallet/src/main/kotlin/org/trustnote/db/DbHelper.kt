package org.trustnote.db

import com.google.gson.*
import org.trustnote.db.entity.Messages
import org.trustnote.db.entity.Units
import org.trustnote.wallet.TApp
import org.trustnote.wallet.network.hubapi.Joints
import org.trustnote.wallet.network.hubapi.HubResponse

fun saveUnit(hubResponse: HubResponse) {

    val joints = hubResponse.body.getAsJsonObject("response").getAsJsonArray("joints")

    val gson = getGson()

    val units: Array<Units> = Array(joints.size()) { index:Int ->
        val joint: Joints = gson.fromJson(joints[index], Joints::class.java)

        val unitJson = joints[index].asJsonObject.getAsJsonObject("unit")
        val msgsJson = unitJson.getAsJsonArray("messages")
        val msgEntities = Array(msgsJson.size()) {
            val message = gson.fromJson(joints[index], Messages::class.java)

            message
        }

        joint.unit
    }

    val db = TrustNoteDataBase.getInstance(TApp.context)

    //db?.unitsDao()?.insert(data[1].unit)

}

    fun <T> parseChildFromJson(origJson: JsonObject, clz: Class<T>, childJsonKey:String) {
        val children = origJson.getAsJsonArray(childJsonKey)

        val joint: Joints = gson.fromJson(joints[index], clz)

    }

fun getGson(): Gson {
    return GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create()
}