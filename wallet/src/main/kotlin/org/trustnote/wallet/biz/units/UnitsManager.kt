package org.trustnote.wallet.biz.units

import org.trustnote.db.*
import org.trustnote.db.entity.*
import org.trustnote.wallet.TApp
import org.trustnote.wallet.network.pojo.HubResponse
import org.trustnote.wallet.util.Utils

class UnitsManager {
    @Suppress("UNCHECKED_CAST")
    fun saveUnits(hubResponse: HubResponse) {
        //TODO: too much tedious work.
        //TODO: save data to table units_authors??
        val response = hubResponse.msgJson.getAsJsonObject("response")
        val jointList = Utils.parseChild(TBaseEntity.VoidEntity, response, Joints::class.java.canonicalName, "joints") as List<Joints>

        for (joint in jointList) {
            val units = joint.unit
            units.json = joint.json.getAsJsonObject("unit")

            //TODO: when to stable?
            units.isStable = 1

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

        DbHelper.saveUnits(jointList.mapToTypedArray { it.unit })

    }
}