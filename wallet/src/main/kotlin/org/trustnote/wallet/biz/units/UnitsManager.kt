package org.trustnote.wallet.biz.units

import com.google.gson.JsonObject
import org.trustnote.db.*
import org.trustnote.db.entity.*
import org.trustnote.wallet.network.pojo.HubResponse
import org.trustnote.wallet.network.pojo.MSG_TYPE
import org.trustnote.wallet.util.Utils

class UnitsManager {
    @Suppress("UNCHECKED_CAST")
    fun saveUnitsFromHubResponse(hubResponse: HubResponse): List<Joints> {

        if (hubResponse.msgType == MSG_TYPE.empty) {
            return listOf()
        }

        //TODO: too much tedious work.
        //TODO: save data to table units_authors??
        val response = hubResponse.msgJson.getAsJsonObject("response")
        if (!response.has("joints")) {
            return listOf()
        }

        val jointList = Utils.parseChild(TBaseEntity.VoidEntity, response, Joints::class.java.canonicalName, "joints") as List<Joints>

        val res = jointList.mapToTypedArray {
            parseUnitFromJson(it.json.getAsJsonObject("unit"))
        }

        DbHelper.saveUnits(res)

        return jointList

    }

    fun parseUnitFromJson(unitJson: JsonObject): Units {

        val units = Utils.getGson().fromJson(unitJson, Units::class.java)
        units.json = unitJson

        //TODO: when to stable?
        units.isStable = 1

        units.unit = units.json.get("unit").asString

        units.sequence = DbConst.UNIT_SEQUENCE_GOOD

        val authentifiersArray = Utils.parseChild(units, units.json, Authentifiers::class.java.canonicalName, "authors") as List<Authentifiers>
        authentifiersArray.forEachIndexed { _, authentifier ->
            authentifier.unit = units.unit; authentifier.parsePathAndAuthentifier()
        }

        units.authenfiers = authentifiersArray

        val messageArray = Utils.parseChild(units, units.json, Messages::class.java.canonicalName, "messages") as List<Messages>

        messageArray.forEachIndexed { index, messages -> messages.messageIndex = index }
        units.messages = messageArray

        for (message in messageArray) {
            message.parent = units
            message.unit = units.unit
            val inputArray = Utils.parseChild(message, message.json, Inputs::class.java.canonicalName, "payload", "inputs") as List<Inputs>

            val outputArray = Utils.parseChild(message, message.json, Outputs::class.java.canonicalName, "payload", "outputs") as List<Outputs>

            val asset = message?.json?.getAsJsonObject("payload")?.get("asset")?.asString

            inputArray.forEachIndexed { index, inputs ->
                inputs.unit = units.unit
                inputs.messageIndex = message.messageIndex
                inputs.inputIndex = index
                inputs.asset = asset

                //TODO: check JS code.
                inputs.address = units.authenfiers[0].address
                //TODO:
                //inputs.type = ""
            }

            outputArray.forEachIndexed { index, outputs ->
                outputs.unit = units.unit;outputs.messageIndex = message.messageIndex; outputs.outputIndex = index
                outputs.asset = asset
            }

            message.payload.inputs = inputArray

            message.payload.outputs = outputArray.sortedBy { it.address }
        }

        return units
    }

}