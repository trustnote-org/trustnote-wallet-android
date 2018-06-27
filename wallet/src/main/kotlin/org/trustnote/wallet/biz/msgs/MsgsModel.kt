package org.trustnote.wallet.biz.msgs

import com.google.gson.JsonObject
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import org.trustnote.db.DbHelper
import org.trustnote.db.entity.ChatMessages
import org.trustnote.db.entity.CorrespondentDevices
import org.trustnote.db.entity.Outbox
import org.trustnote.wallet.R
import org.trustnote.wallet.TApp
import org.trustnote.wallet.biz.js.JSApi
import org.trustnote.wallet.biz.wallet.WalletManager
import org.trustnote.wallet.network.HubModel
import org.trustnote.wallet.network.pojo.ReqDeliver
import org.trustnote.wallet.network.pojo.ReqGetTempPubkey
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

        val api = JSApi()
        val pubkey = outbox.to
        val correspondentDevices = DbHelper.findCorrespondentDeviceByPubkey(pubkey)

        val req = ReqGetTempPubkey(pubkey)
        req.targetHubAddress = (correspondentDevices.hub)
        HubModel.instance.sendHubMsg(req)

        if (req.getResponse().hasError) {

            //TODO: save error or just ignore?
            return

        } else {

            val tempPubkey = req.getTempPubkey()

            var encryptedParingMessage = api.createEncryptedPackageSync(outbox.message, tempPubkey)
            encryptedParingMessage = encryptedParingMessage.replace("""\""", """"""")
            val objDeviceMessage = JsonObject()
            objDeviceMessage.add("encrypted_package", Utils.stringToJsonElement(encryptedParingMessage))
            objDeviceMessage.addProperty("to", correspondentDevices.deviceAddress)
            objDeviceMessage.addProperty("pubkey", WalletManager.model.mProfile.pubKeyForPairId)

            val hash = api.getDeviceMessageHashToSignSync(objDeviceMessage.toString())
            val signature = api.signSync(hash, WalletManager.model.mProfile.privKeyForPairId, "null")

            objDeviceMessage.addProperty("signature", signature)

            //TODO: "error":"wrong message signature"}

            val reqDeliver = ReqDeliver(objDeviceMessage)
            reqDeliver.targetHubAddress = (correspondentDevices.hub)
            HubModel.instance.sendHubMsg(reqDeliver)
            val deliverRes = reqDeliver.getResponse()
            Utils.debugLog(deliverRes.responseJson.toString())

        }

    }

    companion object {
        val instance = MsgsModel()
        const val MSG_TYPE_SYSTEM = "system"
        const val MSG_TYPE_TEXT = "text"
    }

    val mMessagesEventCenter: Subject<Boolean> = PublishSubject.create()

    var latestHomeList: List<CorrespondentDevices> = listOf()

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
            addContacts(pubkey!!, hubAddress!!, secret!!, lambda)
        }

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

            val outbox = composeOutboxPairingMessage(secret, correspondentDevices)
            DbHelper.saveOutbox(outbox)

        } else {
            //Do nothing.
        }

        refreshHomeList()

        lambda.invoke(correspondentDevices.deviceAddress)
        mMessagesEventCenter.onNext(true)

        //TODO: Compose pairing message and save into outbox.
    }

    fun readAllMessages(correspondentAddress: String) {
        DbHelper.readAllMessages(correspondentAddress)
        updated()
    }

    fun findCorrespondentDevice(correspondentAddresses: String): CorrespondentDevices {
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

}

