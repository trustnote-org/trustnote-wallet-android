package org.trustnote.wallet.biz.msgs

import io.reactivex.schedulers.Schedulers
import org.trustnote.db.DbHelper
import org.trustnote.db.entity.ChatMessages
import org.trustnote.db.entity.CorrespondentDevices
import org.trustnote.db.entity.Outbox
import org.trustnote.wallet.R
import org.trustnote.wallet.TApp
import org.trustnote.wallet.biz.js.JSApi
import org.trustnote.wallet.util.MyThreadManager
import org.trustnote.wallet.util.TTTUtils
import org.trustnote.wallet.util.Utils
import java.util.concurrent.TimeUnit

class MsgsModel {


    init {
        monitorOutbox()
    }

    private fun monitorOutbox() {
        DbHelper.monitorOutbox().debounce(3, TimeUnit.SECONDS).subscribeOn(Schedulers.io()).subscribe {
            Utils.debugLog("from monitorOutbox")
            if (it.isNotEmpty()) {
                val outbox = it[0]
                sendPreparedMessageToHub(outbox)
            }
        }

    }

    private fun sendPreparedMessageToHub(outbox: Outbox) {
        //TODO:
    }

    companion object {
        val instance = MsgsModel()
        const val MSG_TYPE_SYSTEM = "system"
        const val MSG_TYPE_TEXT = "text"
    }

    var latestHomeList: List<CorrespondentDevices> = listOf()

    fun refreshHomeList() {
        latestHomeList = DbHelper.queryCorrespondetnDevices()
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

        val pubkey = matchRes.groups[1].toString()
        val hubAddress = matchRes.groups[2].toString()
        val secret = matchRes.groups[3].toString()

        MyThreadManager.instance.runInBack {
            addContacts(pubkey, hubAddress, secret, lambda)
        }

        return
    }

    fun addContacts(pubkey: String, hubAddress: String, secret: String, lambda: (String) -> Unit = {}) {

        val correspondentDevices = CorrespondentDevices()
        val api = JSApi()

        correspondentDevices.deviceAddress = api.getDeviceAddressSync(pubkey)

        val isExist = DbHelper.isCorrespondentDeviceExist(correspondentDevices.deviceAddress)

        //TODO: bug, if correspondent device exist, copy old value.
        correspondentDevices.pubkey = pubkey
        correspondentDevices.hub = hubAddress
        correspondentDevices.creationDate = System.currentTimeMillis() / 1000
        correspondentDevices.isConfirmed = 0
        correspondentDevices.name = "new"

        DbHelper.saveCorrespondentDevice(correspondentDevices)

        if (!isExist) {
            val chatMessages = ChatMessages()

            chatMessages.creationDate = System.currentTimeMillis()
            chatMessages.correspondentAddress = correspondentDevices.deviceAddress
            chatMessages.message = TApp.resources.getString(R.string.default_first_msg_for_friend, correspondentDevices.name)
            chatMessages.type = MSG_TYPE_SYSTEM
            chatMessages.isIncoming = 0

            DbHelper.saveChatMessages(chatMessages)
        } else {
            val outbox = composeOutboxPairingMessage(secret, correspondentDevices)
            DbHelper.saveOutbox(outbox)
        }

        refreshHomeList()

        lambda.invoke(correspondentDevices.deviceAddress)

        //TODO: Compose pairing message and save into outbox.
    }

    fun readAllMessages(correspondentAddress: String) {
        DbHelper.readAllMessages(correspondentAddress)
        //TODO: notify listener
    }

    fun findCorrespondentDevice(correspondentAddresses: String): CorrespondentDevices {
        return DbHelper.findCorrespondentDevice(correspondentAddresses)
    }

    fun updateCorrespondentDeviceName(correspondentDevices: CorrespondentDevices) {
        DbHelper.saveCorrespondentDevice(correspondentDevices)
        //TODO: notify listener
    }

    fun clearChatHistory(correspondentAddresses: String) {
        DbHelper.clearChatHistory(correspondentAddresses)
        //TODO: notify listener
    }

    fun removeCorrespondentDevice(correspondentDevices: CorrespondentDevices) {
        DbHelper.removeCorrespondentDevice(correspondentDevices)
        //TODO: notify listener
    }

}

