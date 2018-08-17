package org.trustnote.wallet.biz.units

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import org.trustnote.db.*
import org.trustnote.db.entity.*
import org.trustnote.wallet.biz.TTT
import org.trustnote.wallet.network.pojo.HubResponse
import org.trustnote.wallet.network.pojo.MSG_TYPE
import org.trustnote.wallet.util.TTTUtils
import org.trustnote.wallet.util.Utils

class UnitsManager {
    @Suppress("UNCHECKED_CAST")
    fun saveUnitsFromHubResponse(hubResponse: HubResponse): List<Units> {

        if (hubResponse.msgType != MSG_TYPE.response) {
            throw RuntimeException("Cannot get hub response")
        }

        //TODO: too much tedious work.
        //TODO: save data to table units_authors??
        val response = hubResponse.msgJson.getAsJsonObject("response")
        //DO NOT CHECK NULL. timeout will throw exception.
        if (!response.has("joints")) {
            return listOf()
        }

        justKeepPaymentMessagesInHubResponse(response)

        val jointList = Utils.parseChild(TBaseEntity.VoidEntity, response, Joints::class.java.canonicalName, "joints") as List<Joints>

        val proofChainBalls = Utils.parseChild(TBaseEntity.VoidEntity, response, Balls::class.java.canonicalName, "proofchain_balls") as List<Balls>

        val finalBadUnits = proofChainBalls.filter { it.isNonserial }.map { it.unit }

        val res = jointList.mapToTypedArray {
            parseUnitFromJson(it.json.getAsJsonObject("unit"), finalBadUnits)
        }

        DbHelper.saveUnits(res)

        val stableUnitIds = proofChainBalls.map { it.unit }

        DbHelper.unitsStabled(stableUnitIds)

        return res.toList()

    }

    fun justKeepPaymentMessagesInHubResponse(response: JsonObject) {
        val joints = response.getAsJsonArray("joints")
        if (joints == null) {
            return
        }
        for(oneUnit in joints) {
            if (oneUnit !is JsonObject) {
                continue
            }
            val oneUnitJson = oneUnit.getAsJsonObject("unit")
            justKeepPaymentMessages(oneUnitJson)
        }

    }

    fun justKeepPaymentMessages(unitJson: JsonObject) {
        val msgs = unitJson.getAsJsonArray("messages")

        if (msgs.size() > 1) {
            Utils.debugHub(msgs.toString())
        }

        unitJson.remove("messages")
        val allPaymentsJson = JsonArray()
        for( one in msgs) {
            if(one is JsonObject && TTT.unitMsgTypePayment == one.getAsJsonPrimitive("app")?.asString){
                allPaymentsJson.add(one)
            }
        }
        unitJson.add("messages", allPaymentsJson)
    }

    fun parseUnitFromJson(unitJson: JsonObject, finalBadUnits: List<String>): Units {

        justKeepPaymentMessages(unitJson)

        val units = Utils.getGson().fromJson(unitJson, Units::class.java)

        units.json = unitJson

        units.unit = units.json.get("unit").asString

        if (finalBadUnits.contains(units.unit)) {
            units.sequence = DbConst.UNIT_SEQUENCE_FINAL_BADG
        } else {
            units.sequence = DbConst.UNIT_SEQUENCE_GOOD
        }

        val authentifiersArray = Utils.parseChild(units, units.json, Authentifiers::class.java.canonicalName, "authors") as List<Authentifiers>
        authentifiersArray.forEachIndexed { _, authentifier ->
            authentifier.unit = units.unit; authentifier.parsePathAndAuthentifier()
        }
        units.authenfiers = authentifiersArray

        for (one in authentifiersArray) {
            if (one.path.isEmpty()) {
                Utils.logW(units.unit)
            }
        }

        val definitionsList = mutableListOf<Definitions>()
        for (authentifier in authentifiersArray) {
            if (!authentifier.json.has("definition")) {
                continue
            }
            val definitions = authentifier.json.getAsJsonArray("definition")
            for (definition in definitions) {
                if (definition is JsonObject && definition.has("pubkey")) {

                    val pubkey = TTTUtils.genDefinitions(definition.get("pubkey").asString)
                    val definitionEntity = Definitions()
                    definitionEntity.definition = pubkey
                    //TODO: why address use cas chash and why 0?
                    definitionEntity.definitionChash = authentifier.address
                    definitionEntity.hasReferences = 0
                    definitionsList.add(definitionEntity)
                }
            }
        }

        units.definitions = definitionsList

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
                //TODO: Check DB for input address
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