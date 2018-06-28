package org.trustnote.wallet.biz.msgs

import com.google.gson.JsonElement
import org.trustnote.wallet.biz.wallet.WalletManager
import org.trustnote.wallet.network.HubModel
import org.trustnote.wallet.util.Utils

class TMessage() {

    var from: String = ""
    var deviceHub: String = ""
    var subject: String = ""
    var body: Any = Unit

    constructor(subject: String, body: Any) : this() {
        from = WalletManager.model.mProfile.deviceAddress
        deviceHub = HubModel.instance.mDefaultHubAddress
        this.subject = subject
        this.body = body
    }

    override fun toString(): String {
        return Utils.toGsonObject(this).toString()
    }

    fun toJsonString(): String {
        return toString()
    }

}

class TDeviceMessage() {

    var encryptedPackage: JsonElement = Utils.emptyJsonObject
    var to: String = ""
    var pubkey: String = ""
    var signature: String = ""

    constructor(encryptedPackage: JsonElement, deviceAddress: String, pubkey: String) : this() {
        this.encryptedPackage = encryptedPackage
        this.to = deviceAddress
        this.pubkey = pubkey
    }

    override fun toString(): String {
        return Utils.toGsonObject(this).toString()
    }

    fun toJsonString(): String {
        return toString()
    }

}

enum class TMessageType {
    system,
    text
}

enum class TMessageSubject {
    pairing,
    text
}

//try {
//    myVar = MyEnum.valueOf("Qux")
//} catch(e: IllegalArgumentException) {
//    Log.d(TAG, "INVALID MyEnum value: 'Qux' | $e")
//}