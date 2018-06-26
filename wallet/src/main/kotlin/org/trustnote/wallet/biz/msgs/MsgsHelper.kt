package org.trustnote.wallet.biz.msgs

import com.google.gson.JsonObject
import org.trustnote.db.entity.ChatMessages
import org.trustnote.db.entity.CorrespondentDevices
import org.trustnote.db.entity.Outbox
import org.trustnote.wallet.biz.js.JSApi
import org.trustnote.wallet.biz.wallet.WalletManager
import org.trustnote.wallet.network.HubModel
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


//一. 加好友
//
//1. 扫码得到对方
//pubkey
//hub
//secret
//
//2. 用对方的pubkey得到对方的device_address,
//然后把信息存到 correspondent_devices 表里
//name为new
//is_confirmed = 0
//
//3. 得到自己
//secret,
//device_pubkey,
//device_address,
//hub
//
//4. 组成消息body内容
//body = {
//    pairing_secret: 对方的secret,
//    device_name: 自己的device_name,
//    reverse_pairing_secret: 自己的secret         //（看步骤12）主动发送配对的时候有值，被添加时发送配对为空
//}
//
//封装消息内容，设置消息类型
//json = {
//    from: 我的设备地址,
//    device_hub: 我的hub,
//    subject: "pairing",
//    body: body
//};
//
//用对方公钥加密json包
//var objDeviceMessage = {
//    encrypted_package: 已加密的Json包
//};
//对 objDeviceMessage 做 getBase64Hash 作为 message_hash
//然后把message_hash , 对方设备地址 , objDeviceMessage转为字符串，放入outbox表里

//{"from":"0YUFBVGJOD64MUC2BFL2LR65FOVKJGTVI","device_hub":"shawtest.trustnote.org","subject":"pairing","body":{"pairing_secret":"ePCLeojvdvf6","device_name":"Nexus 6P","reverse_pairing_secret":"o4zCp/fpczeY"}}
//06-25 18:26:35.106 30270 30270 I chromium: [INFO:CONSOLE(48)] "will encrypt and send to 0CREQS2362HYCHNKVCU4ZBSNHAVVLASKM: {"from":"0YUFBVGJOD64MUC2BFL2LR65FOVKJGTVI","device_hub":"shawtest.trustnote.org","subject":"pairing","body":{"pairing_secret":"ePCLeojvdvf6","device_name":"Nexus 6P","reverse_pairing_secret":"o4zCp/fpczeY"}}

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
    outbox.message = messageString
    outbox.to = correspondentDevices.pubkey
    outbox.creationDate = System.currentTimeMillis()/1000

    return outbox

}
