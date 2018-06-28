package org.trustnote.wallet.biz.msgs

import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import org.trustnote.db.DbHelper
import org.trustnote.db.entity.ChatMessages
import org.trustnote.db.entity.CorrespondentDevices
import org.trustnote.db.entity.Outbox
import org.trustnote.wallet.network.HubModel
import org.trustnote.wallet.network.HubMsgFactory.SUBJECT_HUB_DELETE
import org.trustnote.wallet.network.pojo.HubJustSaying
import org.trustnote.wallet.util.MyThreadManager
import org.trustnote.wallet.util.TTTUtils
import org.trustnote.wallet.util.Utils
import java.util.concurrent.TimeUnit

class MessageModel {

    init {
        monitorOutbox()
    }

    companion object {
        val instance = MessageModel()
    }

    val mMessagesEventCenter: Subject<Boolean> = PublishSubject.create()

    var latestHomeList: List<CorrespondentDevices> = listOf()

    private fun monitorOutbox() {
        DbHelper.monitorOutbox().debounce(3, TimeUnit.SECONDS).subscribeOn(Schedulers.io()).subscribe {
            Utils.debugLog("from monitorOutbox")
            if (it.isNotEmpty()) {
                val outbox = it[0]
                sendOutboxMessage(outbox)
            }
        }

    }

    fun refreshHomeList() {
        latestHomeList = DbHelper.queryCorrespondetnDevices()
    }

    fun updated() {
        refreshHomeList()
        mMessagesEventCenter.onNext(true)
    }

    fun isRefreshing(): Boolean {
        return false
    }

    fun queryChatMessages(correspondentAddresses: String): List<ChatMessages> {
        return DbHelper.queryChatMessages(correspondentAddresses)
    }

    //TODO: use waitiing UI.
    fun addContacts(pairIdQrCode: String, lambda: (String) -> Unit = {}) {
        val matchRes = TTTUtils.tttMyPairIdPattern.matchEntire(pairIdQrCode)
        if (matchRes == null) {
            return
        }

        val pubkey = matchRes.groups[1]?.value
        val hubAddress = matchRes.groups[2]?.value
        val secret = matchRes.groups[3]?.value

        MyThreadManager.instance.runInBack {
            addOrUpdateContacts(pubkey!!, "", hubAddress!!, 0, "New", secret!!, lambda)
        }

    }

    fun sendTextMessage(message: String, correspondentDevices: CorrespondentDevices) {
        MyThreadManager.instance.runInBack {
            sendTextMessageBackground(message, correspondentDevices)
        }
    }

    fun sendTextMessageBackground(message: String, correspondentDevices: CorrespondentDevices) {

        val chatMessages = ChatMessages.createOutMessage(message, correspondentDevices.deviceAddress)

        DbHelper.saveChatMessages(chatMessages)

        val outbox = composeOutboxTextMessage(message, correspondentDevices)
        DbHelper.saveOutbox(outbox)

        updated()

    }


    fun readAllMessages(correspondentAddress: String) {
        DbHelper.readAllMessages(correspondentAddress)
        updated()
    }

    fun findCorrespondentDevice(correspondentAddresses: String): CorrespondentDevices? {
        return DbHelper.findCorrespondentDevice(correspondentAddresses)
    }

    fun updateCorrespondentDeviceName(correspondentDevices: CorrespondentDevices) {

        DbHelper.saveCorrespondentDevice(correspondentDevices)
        updated()

    }

    fun clearChatHistory(correspondentAddresses: String) {

        DbHelper.clearChatHistory(correspondentAddresses)
        //TODO: also remove message in outbox
        updated()
    }

    fun removeCorrespondentDevice(correspondentDevices: CorrespondentDevices) {
        DbHelper.removeCorrespondentDevice(correspondentDevices)
        //TODO: also remove message in outbox
        updated()
    }

    fun deviceOfOutboxMessageHasBeenRemoved(outbox: Outbox) {

        DbHelper.removeMsgInOutbox(outbox)
        updated()

    }

    fun outboxMessageSendSuccessful(outbox: Outbox) {

        DbHelper.removeMsgInOutbox(outbox)
        updated()

    }

    fun onMessage(hubJustSaying: HubJustSaying) {
        val messageHash = onMessageCalledByMsgsModel(hubJustSaying)
        if (messageHash.isNotEmpty()) {
            deleteMessageInHubCache(messageHash)
        }
    }

    fun deleteMessageInHubCache(messageHash: String) {
        val hubJustSaying = HubJustSaying(SUBJECT_HUB_DELETE, JsonPrimitive(messageHash))
        HubModel.instance.sendHubMsg(hubJustSaying)
    }

    fun receiveTextMessage(messageJson: JsonObject) {

        val correspondentDevices = findCorrespondentDevice(messageJson.get("from").asString)
                ?: return

        val chatMessages = ChatMessages.createIncomingMessage(messageJson.get("body").asString,
                correspondentDevices.deviceAddress)

        DbHelper.saveChatMessages(chatMessages)
        updated()

    }

    fun receivePairingRequest(messageJson: JsonObject, pubkey: String) {

        val body = messageJson.get("body") as JsonObject

        val deviceAddress = messageJson.get("from").asString
        val hub = messageJson.get("device_hub").asString
        val isConfirmed = 1
        val name = body.get("device_name").asString

        addOrUpdateContacts(pubkey, deviceAddress, hub, isConfirmed, name, "")

        //pong back with device name is necessary.

    }

}

