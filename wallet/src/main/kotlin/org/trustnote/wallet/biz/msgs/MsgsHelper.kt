package org.trustnote.wallet.biz.msgs

import com.google.gson.JsonObject
import org.trustnote.db.DbHelper
import org.trustnote.db.entity.ChatMessages
import org.trustnote.db.entity.CorrespondentDevices
import org.trustnote.db.entity.Outbox
import org.trustnote.wallet.R
import org.trustnote.wallet.TApp
import org.trustnote.wallet.biz.js.JSApi
import org.trustnote.wallet.biz.wallet.WalletManager
import org.trustnote.wallet.network.HubModel
import org.trustnote.wallet.network.pojo.HubJustSaying
import org.trustnote.wallet.network.pojo.ReqDeliver
import org.trustnote.wallet.network.pojo.ReqGetTempPubkey
import org.trustnote.wallet.uiframework.ActivityBase
import org.trustnote.wallet.util.AndroidUtils
import org.trustnote.wallet.util.Prefs
import org.trustnote.wallet.util.Utils

fun chatWithFriend(friendId: String, activity: ActivityBase) {

    val f = FragmentMsgsChat()
    AndroidUtils.addFragmentArguments(f, AndroidUtils.KEY_CORRESPODENT_ADDRESSES, friendId)
    activity.addL2Fragment(f)

}

// 2.时间显示的规则：
//   a.时间按照24小时格式显示；
//   b.消息发生在当天的内容只显示时间，eg:14:32；
//   c.消息发生在前一天显示的内容为昨天+时间，eg:昨天 14:35；
//   d.消息发生昨天以前则显示：日期+时间，eg:4-7 14:32或是2017-4-7 14:32
// 3.当天的消息，以每5分钟为一个跨度的显示时间

val FIVE_MINUTES: Long = 5 * 60
val ONE_DAY: Long = 24 * 3600

fun getChatHistoryForDisplay(original: List<ChatMessages>): List<ChatMessages> {

    val sorted = original.sortedBy { it.creationDate }
    val todayMsgs = sorted.filter { Utils.isToday(it.creationDate * 1000) }
    val beforeTodayMsgs = sorted.filter { !Utils.isToday(it.creationDate * 1000) }

    debounceByDifferentInteger(beforeTodayMsgs,
            {
                it.creationDate / ONE_DAY
            },
            { isNew, data ->
                data.showTimeOrDate = isNew
            })

    debounceByDifferentInteger(todayMsgs,
            {
                it.creationDate
            },
            { isNew, data ->
                data.showTimeOrDate = isNew
            }, FIVE_MINUTES)

    return sorted
}

fun <T> debounceByDifferentInteger(data: List<T>, toInteger: (T) -> Long, updateLogic: (Boolean, T) -> Unit, gap: Long = 1) {

    val length = data.size
    var lastInteger = Long.MIN_VALUE

    for (i in 0 until length) {

        val currentInt = toInteger.invoke(data[i])

        updateLogic(currentInt > (lastInteger + gap - 1), data[i])

        lastInteger = if (currentInt > (lastInteger + gap - 1)) currentInt else lastInteger

    }
}

fun composeOutboxPairingMessage(secret: String, correspondentDevices: CorrespondentDevices): Outbox {

    val jsApi = JSApi()
    val mySecret = JSApi().randomBytesSync(9)
    val myDevicePubkey = WalletManager.model.mProfile.pubKeyForPairId
    val myDeviceAddress = WalletManager.model.mProfile.deviceAddress
    val myHub = HubModel.instance.mDefaultHubAddress

    val body = JsonObject()
    body.addProperty("pairing_secret", secret)
    body.addProperty("device_name", Prefs.readDeviceName())
    body.addProperty("reverse_pairing_secret", mySecret)

    val paringMessage = JsonObject()
    paringMessage.addProperty("from", myDeviceAddress)
    paringMessage.addProperty("device_hub", myHub)
    paringMessage.addProperty("subject", "pairing")
    paringMessage.add("body", body)

    val encryptedParingMessage = jsApi.createEncryptedPackageSync(paringMessage.toString(), correspondentDevices.pubkey)

    val objDeviceMessage = JsonObject()
    objDeviceMessage.addProperty("encrypted_package", encryptedParingMessage)

    val messageString = objDeviceMessage.toString()
    val messageHash = jsApi.getBase64HashSync(messageString)

    val outbox = Outbox()
    outbox.messageHash = messageHash

    //Save clean text for future sending action
    outbox.message = paringMessage.toString()
    //outbox.message = messageString
    outbox.to = correspondentDevices.deviceAddress
    outbox.creationDate = System.currentTimeMillis() / 1000

    return outbox

}

fun composeOutboxTextMessage(body: String, correspondentDevices: CorrespondentDevices): Outbox {

    val tMessage = TMessage(TMessageSubject.text.name, body)

    val jsApi = JSApi()
    val encryptedParingMessage = jsApi.createEncryptedPackageSync(tMessage.toJsonString(),
            correspondentDevices.pubkey ?: WalletManager.model.mProfile.pubKeyForPairId)

    val objDeviceMessage = JsonObject()
    objDeviceMessage.addProperty("encrypted_package", encryptedParingMessage)

    val messageString = objDeviceMessage.toString()
    val messageHash = jsApi.getBase64HashSync(messageString)

    val outbox = Outbox()
    outbox.messageHash = messageHash
    outbox.message = tMessage.toJsonString()
    outbox.to = correspondentDevices.deviceAddress
    outbox.creationDate = System.currentTimeMillis() / 1000

    return outbox

}

fun onMessageCalledByMsgsModel(hubJustSaying: HubJustSaying): String {

    if (hubJustSaying.bodyJson !is JsonObject) {
        Utils.logW("Unknown incoming message${hubJustSaying.toHubString()}")
        return ""
    }

    Utils.debugLog("${MessageModel.instance.TAG}::onMessageCalledByMsgsModel::${hubJustSaying.bodyJson.toString()}")

    val deviceMessage = (hubJustSaying.bodyJson as JsonObject).get("message") as JsonObject

    val messageHash = (hubJustSaying.bodyJson as JsonObject).get("message_hash").asString

    val deviceMessageObj = Utils.getGson().fromJson(deviceMessage, TDeviceMessage::class.java)

    val api = JSApi()
    val myProfile = WalletManager.model.mProfile

    var decryptPackage = api.decryptPackage(deviceMessageObj.encryptedPackage.toString(),
            myProfile.tempPrivkey,
            myProfile.tempPrivkey, myProfile.privKeyForPairId)

    //In case we cannot decrypt package.
    if (decryptPackage.isEmpty() || decryptPackage.length < 3) {
        return messageHash
    }

    decryptPackage = decryptPackage.replace("""\""", "")

    val messageJson = Utils.stringToJsonElement(decryptPackage)

    if (messageJson !is JsonObject || !messageJson.has("subject")) {
        Utils.logW("Unknown incoming message${hubJustSaying.toHubString()}")
        return ""
    }

    val messageSubject = messageJson.get("subject").asString

    if ("pairing" == messageSubject) {

        MessageModel.instance.receivePairingRequest(messageJson, deviceMessageObj.pubkey)

    }

    if ("text" == messageSubject) {
        MessageModel.instance.receiveTextMessage(messageJson)
    }

    Utils.debugLog(decryptPackage)

    return messageHash

}

fun getTempPubkey(correspondentDevices: CorrespondentDevices): String {

    val req = ReqGetTempPubkey(correspondentDevices.pubkey, correspondentDevices.hub)
    HubModel.instance.sendHubMsg(req)

    if (req.getResponse().hasErrorFromHub) {

        //TODO: save error or just ignore?
        return ""
    }

    return req.getTempPubkey()

}

fun sendOutboxMessage(outbox: Outbox) {

    Utils.debugLog("${MessageModel.instance.TAG}::start::sendOutboxMessage::${outbox.message}")

    val api = JSApi()
    val deviceAddress = outbox.to

    val correspondentDevices = DbHelper.findCorrespondentDevice(deviceAddress)

    if (correspondentDevices == null) {
        MessageModel.instance.deviceOfOutboxMessageHasBeenRemoved(outbox)
        return
    }

    val tempPubkey = getTempPubkey(correspondentDevices)

    if (tempPubkey.isEmpty()) {
        return
    }

    Utils.debugLog("${MessageModel.instance.TAG}::sendOutboxMessage::${outbox.message}")

    var encryptedMessage = api.createEncryptedPackageSync(outbox.message, tempPubkey)
    var encryptedMessageJson = Utils.stringToJsonElement(encryptedMessage)

    val deviceMessage = TDeviceMessage(encryptedMessageJson,
            correspondentDevices.deviceAddress,
            WalletManager.model.mProfile.pubKeyForPairId)


    val hash = api.getDeviceMessageHashToSignSync(deviceMessage.toJsonString())

    deviceMessage.signature = api.signSync(hash, WalletManager.model.mProfile.privKeyForPairId, "null")

    val reqDeliver = ReqDeliver(Utils.toGsonObject(deviceMessage))
    reqDeliver.targetHubAddress = (correspondentDevices.hub)
    HubModel.instance.sendHubMsg(reqDeliver)

    if (reqDeliver.isAccepted()) {
        Utils.debugLog("${MessageModel.instance.TAG}::sendOutboxMessage::successful::${outbox.message}")
        MessageModel.instance.outboxMessageSendSuccessful(outbox)
    } else if (reqDeliver.getResponse().hasErrorFromHub) {
        Utils.debugLog("${MessageModel.instance.TAG}::sendOutboxMessage::fail::${outbox.message}")
        MessageModel.instance.outboxMessageSendSuccessful(outbox)
        //TODO: save error msg in outbox ?When to retry?
    }

}

fun addOrUpdateContacts(pubkey: String, deviceAddressP: String, hubAddress: String, isConfirmed: Int, name: String, secret: String, lambda: (String) -> Unit = {}) {

    Utils.debugLog("${MessageModel.instance.TAG}::addOrUpdateContacts")
    val api = JSApi()

    var deviceAddress = deviceAddressP
    if (pubkey.isNotEmpty()) {
        deviceAddress = api.getDeviceAddressSync(pubkey)
    }

    var correspondentDevices = DbHelper.findCorrespondentDevice(deviceAddress)
    val isExist = (correspondentDevices != null)
    if (!isExist) {

        correspondentDevices = CorrespondentDevices()
        correspondentDevices.deviceAddress = deviceAddress
        correspondentDevices.pubkey = pubkey
        correspondentDevices.hub = hubAddress
        correspondentDevices.creationDate = System.currentTimeMillis() / 1000
        correspondentDevices.updateDate = System.currentTimeMillis() / 1000
        correspondentDevices.name = name

        val message = TApp.resources.getString(R.string.default_first_msg_for_friend, correspondentDevices.name);
        val chatMessages = ChatMessages.createSystemMessage(message, correspondentDevices.deviceAddress)
        DbHelper.saveChatMessages(chatMessages)

    } else {

        correspondentDevices?.deviceAddress = deviceAddress
        correspondentDevices?.hub = hubAddress
        correspondentDevices?.name = name

    }

    if (correspondentDevices?.isConfirmed == 1 || isConfirmed == 1) {
        correspondentDevices?.isConfirmed = 1
    }

    DbHelper.saveCorrespondentDevice(correspondentDevices!!)

    if (!isExist) {
        val outbox = composeOutboxPairingMessage(secret, correspondentDevices)
        DbHelper.saveOutbox(outbox)
    }

    MessageModel.instance.refreshHomeList()

    lambda.invoke(correspondentDevices.deviceAddress)
    MessageModel.instance.mMessagesEventCenter.onNext(true)

}


