package org.trustnote.wallet.biz.js

import android.webkit.ValueCallback
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import org.apache.commons.lang3.StringEscapeUtils

class JSApi {


    //TODO: use struct to compose the api command.
    /**
     * 生成助记词
     * @method mnemonic
     * @for Base
     * @param {void}
     * @return {string} 12个助记词
     */
    fun mnemonic(cb: ValueCallback<String>) {
        TWebView.sInstance.callJS("""window.Client.mnemonic();""", cb)
    }

    fun mnemonicSync(): String {
        return TWebView.sInstance.callJSSync("""window.Client.mnemonic();""")
    }

    /**
     * 根据助记词生成根私钥
     * @method xPrivKey
     * @for Base
     * @param {string}  助记词
     * @return {string} 私钥
     */
    fun xPrivKey(mnemonic: String, cb: ValueCallback<String>) {
        TWebView.sInstance.callJS("""window.Client.xPrivKey("$mnemonic");""", cb)
    }

    fun xPrivKeySync(mnemonic: String): String {
        return TWebView.sInstance.callJSSync("""window.Client.xPrivKey("$mnemonic");""")
    }

    /**
     * 生成根公钥
     * @method xPubKey
     * @for Base
     * @param {string}  根私钥
     * @return {string} 根公钥
     */
    fun xPubKey(xPrivKey: String, cb: ValueCallback<String>) {
        TWebView.sInstance.callJS("""window.Client.xPubKey("$xPrivKey");""", cb)
    }

    /**
     * 生成钱包公钥
     * @method walletPubKey
     * @for Base
     * @param {string}  私钥
     * @param {int}     钱包index 0-
     * @return {string} 钱包公钥
     */
    fun walletPubKey(xPrivKey: String, num: Int, cb: ValueCallback<String>) {
        TWebView.sInstance.callJS("""window.Client.walletPubKey("$xPrivKey", $num);""", cb)
    }

    fun walletPubKeySync(xPrivKey: String, num: Int): String {
        return TWebView.sInstance.callJSSync("""window.Client.walletPubKey("$xPrivKey", $num);""")
    }

    /**
     * 生成钱包ID
     * @method walletID
     * @for Base
     * @param {string}  钱包公钥
     * @return {string} 钱包ID
     */
    fun walletID(walletPubKey: String, cb: ValueCallback<String>) {
        TWebView.sInstance.callJS("""window.Client.walletID("$walletPubKey");""", cb)
    }

    fun walletIDSync(walletPubKey: String): String {
        return TWebView.sInstance.callJSSync("""window.Client.walletID("$walletPubKey");""")
    }

    /**
     * 生成设备地址
     * @method deviceAddress
     * @for Base
     * @param {string}  根私钥
     * @return {string} 设备地址
     */
    fun deviceAddress(xPrivKey: String, cb: ValueCallback<String>) {
        TWebView.sInstance.callJS("""window.Client.deviceAddress("$xPrivKey");""", cb)
    }

    fun deviceAddressSync(xPrivKey: String): String {
        return TWebView.sInstance.callJSSync("""window.Client.deviceAddress("$xPrivKey");""")
    }

    /**
     * 生成钱包地址对应的公钥
     * @method walletAddress
     * @for Base
     * @param {string}  钱包公钥
     * @param {int}     收款地址为 0; 找零地址为 1;
     * @param {int}     地址index 0-
     * @return {string} 钱包地址对应的公钥
     */
    fun walletAddressPubkey(walletXPubKey: String, isChange: Int, idx: Int, cb: ValueCallback<String>) {
        TWebView.sInstance.callJS("""window.Client.walletAddressPubkey("$walletXPubKey", $isChange, $idx);""", cb)
    }

    fun walletAddressPubkeySync(walletXPubKey: String, isChange: Int, idx: Int): String {
        return TWebView.sInstance.callJSSync("""window.Client.walletAddressPubkey("$walletXPubKey", $isChange, $idx);""")
    }

    /**
     * 生成钱包的地址
     * @method walletAddress
     * @for Base
     * @param {string}  钱包公钥
     * @param {int}     收款地址为 0; 找零地址为 1;
     * @param {int}     地址index 0-
     * @return {string} 钱包地址
     */
    fun walletAddress(wallet_xPubKey: String, isChange: Int, idx: Int, cb: ValueCallback<String>) {
        TWebView.sInstance.callJS("""window.Client.walletAddress("$wallet_xPubKey", $isChange, $idx);""", cb)
    }

    fun walletAddressSync(wallet_xPubKey: String, isChange: Int, idx: Int): String {
        return TWebView.sInstance.callJSSync("""window.Client.walletAddress("$wallet_xPubKey",$isChange,$idx);""")
    }

    /**
     * 生成ecdsa签名公钥
     * @method ecdsaPubkey
     * @for Base
     * @param {string}  钱包私钥
     * @param {string}  派生路径
     * @return {string} 签名公钥
     */
    fun ecdsaPubkey(xPrivKey: String, path: String, cb: ValueCallback<String>) {
        TWebView.sInstance.callJS("""window.Client.ecdsaPubkey("$xPrivKey", "$path");""", cb)
    }

    fun ecdsaPubkeySync(xPrivKey: String, path: String): String {
        return TWebView.sInstance.callJSSync("""window.Client.ecdsaPubkey("$xPrivKey", "$path");""")
    }


    /**
     * 签名
     * @method sign
     * @for Base
     * @param {string}  base64编码过的hash
     * @param {string}  根私钥
     * @param {string}  派生路径
     * @return {string} 签名结果
     */
    fun sign(b64_hash: String, xPrivKey: String, path: String, cb: ValueCallback<String>) {
        val jsCode = """window.Client.sign("$b64_hash", "$xPrivKey", "$path");"""
        TWebView.sInstance.callJS(jsCode, cb)
    }

    fun signSync(b64_hash: String, xPrivKey: String, path: String): String {
        val jsCode = """window.Client.sign("$b64_hash", "$xPrivKey", "$path");"""
        return TWebView.sInstance.callJSSync(jsCode)
    }

    /**
     * 验证签名
     * @method verify
     * @for Base
     * @param {string}  base64编码过的hash
     * @param {string}  签名信息
     * @param {string}  派生公钥
     * @return {bool}   验签结果
     */
    fun verify(b64_hash: String, sig: String, pub_key: String, cb: ValueCallback<String>) {
        TWebView.sInstance.callJS("window.Client.verify($b64_hash$,$sig$,$pub_key);", cb)
    }

    /**
     * 获得设备消息hash
     * @method getDeviceMessageHashToSign
     * @for Base
     * @param {string}  消息JSON字符串
     * @return {string} base64过的hash
     */
    fun getDeviceMessageHashToSignSync(jsonString: String): String {
        return TWebView.sInstance.callJSSync("""window.Client.getDeviceMessageHashToSign($jsonString);""")
    }

    fun getDeviceMessageHashToSign(jsonString: String, cb: ValueCallback<String>) {
        TWebView.sInstance.callJS("window.Client.getDeviceMessageHashToSign($jsonString);", cb)
    }

    /**
     * 获得交易单元hash
     * @method getUnitHashToSign
     * @for Base
     * @param {string}  单元JSON字符串
     * @return {string} base64过的hash
     */
    fun getUnitHashToSign(unit: String, cb: ValueCallback<String>) {
        TWebView.sInstance.callJS("window.Client.getUnitHashToSign($unit);", cb)
    }

    fun getUnitHashToSignSync(unit: String): String {
        return TWebView.sInstance.callJSSync("window.Client.getUnitHashToSign($unit);")
    }

    fun getBase64HashSync(json: String): String {
        return TWebView.sInstance.callJSSync("window.Client.getBase64Hash($json);")
    }

    fun getBase64HashForStringSync(str: String): String {
        val newStr = StringEscapeUtils.escapeJson(str)
        return TWebView.sInstance.callJSSync("""window.Client.getBase64HashForString("$newStr");""")
    }

    /**
     * 获得交易单元unit hash
     * @method getUnitHash
     * @for Base
     * @param {string}  消息JSON字符串
     * @return {string} base64过的hash
     */
    fun getUnitHashSync(unit: String): String {
        return TWebView.sInstance.callJSSync("window.Client.getUnitHash($unit);")
    }


    /**
     * 获得消息头字节数
     * @method getHeadersSize
     * @for Base
     * @param {string}  单元JSON字符串
     * @return {int}    字节数
     */
    fun getHeadersSizeSync(unit: String): String {
        return TWebView.sInstance.callJSSync("window.Client.getHeadersSize($unit);")
    }

    /**
     * 获得payload字节数
     * @method getTotalPayloadSize
     * @for Base
     * @param {string}  单元JSON字符串
     * @return {int}    字节数
     */
    fun getTotalPayloadSizeSync(unit: String): String {
        return TWebView.sInstance.callJSSync("window.Client.getTotalPayloadSize($unit);")
    }

    //TODO: Cache the result.
    fun getBip38WordList(): List<String> {
        return BIP38_WORD_LIST_EN
    }

    /**
     * 生成随机字节数
     * @method randomBytes
     * @for Base
     * @param {int}     字节数
     * @return {string} 随机数的base64
     */
    fun randomBytesSync(num: Int): String {
        return TWebView.sInstance.callJSSync("window.Client.randomBytes($num);")
    }

    fun randomBytes(num: Int, cb: ValueCallback<String>) {
        return TWebView.sInstance.callJS("window.Client.randomBytes($num);", cb)
    }

    /**
     * 生成m/1'私钥
     * @method m1PrivKey
     * @for Base
     * @param {string}  根私钥
     * @return {string} base64编码的私钥
     */
    fun m1PrivKeySync(xPrivKey: String): String {
        return TWebView.sInstance.callJSSync("""window.Client.m1PrivKey("$xPrivKey");""")
    }

    fun m1PrivKey(xPrivKey: String, cb: ValueCallback<String>) {
        return TWebView.sInstance.callJS("""window.Client.m1PrivKey("$xPrivKey");""", cb)
    }

    /**
     * 生成临时私钥
     * @method genPrivKey
     * @for Base
     * @param {void}
     * @return {string} base64编码的私钥
     */
    fun genPrivKeySync(): String {
        return TWebView.sInstance.callJSSync("""window.Client.genPrivKey();""")
    }

    /**
     * 根据临时私钥生成临时公钥
     * @method genPubKey
     * @for Base
     * @param {string}  临时私钥
     * @return {string} 临时公钥
     */
    fun genPubKeySync(privKey: String): String {
        return TWebView.sInstance.callJSSync("""window.Client.genPubKey("$privKey");""")
    }


    /**
     * 根据公钥生成设备地址
     * @method getDeviceAddress
     * @for Base
     * @param {string}  m/1 公钥
     * @return {string} 设备地址
     */
    fun getDeviceAddressSync(pubkey: String): String {
        return TWebView.sInstance.callJSSync("""window.Client.getDeviceAddress("$pubkey");""")
    }

    /**
     * 加密消息
     * @method createEncryptedPackage
     * @for Base
     * @param {string}  待加密json字符串
     * @param {string}  公钥
     * @return {string} 密文
     */
    fun createEncryptedPackageSync(jsonString: String, pubkey: String): String {
        var res = TWebView.sInstance.callJSSync("""window.Client.createEncryptedPackage($jsonString, "$pubkey");""")
        res = res.replace("""\""", "")
        return res
    }

    /**
     * 解密消息
     * @method decryptPackage
     * @for Base
     * @param {string}  待解密字符串
     * @param {string}  临时私钥
     * @param {string}  上一个临时私钥
     * @param {string}  m/1私钥
     * @return {string} 明文字符串
     */
    fun decryptPackage(objEncryptedPackage: String, privKey: String, prePrivKey: String, m1PrivKey: String): String {
        return TWebView.sInstance.callJSSync("""window.Client.decryptPackage($objEncryptedPackage, "$privKey", "$prePrivKey", "$m1PrivKey");""")
    }

}

var BIP38_WORD_LIST_EN: List<String> = mutableListOf()

class JSResult {
    var result: String = ""
}


