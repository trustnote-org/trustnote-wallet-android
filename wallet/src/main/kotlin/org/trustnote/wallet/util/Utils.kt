package org.trustnote.wallet.util

import android.net.NetworkInfo
import android.os.Environment
import android.widget.Toast
import com.github.pwittchen.reactivenetwork.library.rx2.Connectivity
import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork
import com.google.gson.*
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.trustnote.db.entity.MyAddresses
import org.trustnote.db.entity.TBaseEntity

import org.trustnote.wallet.TApp
import org.trustnote.wallet.network.HubClient

import timber.log.Timber
import java.util.*
import java.util.concurrent.TimeUnit
import com.google.gson.JsonObject
import org.trustnote.wallet.BuildConfig
import org.trustnote.wallet.biz.wallet.TProfile
import java.io.File
import java.text.SimpleDateFormat

object Utils {

    val emptyJsonObject = JsonObject()

    fun debugLog(msg: String) {
        Timber.d("TTT" + msg)
    }

    fun logW(msg: String) {
        Timber.w("TTT" + msg)
    }

    fun debugJS(s: String) {
        android.util.Log.d("JSApi", s)
    }

    fun crash(s: String) {
        //TODO:
        android.util.Log.e("CRASH", s)
        throw RuntimeException(s)
    }

    fun debugToast(s: String) {
        Timber.w("TOAST" + s)
        Toast.makeText(TApp.context, s, Toast.LENGTH_SHORT).show()
    }

    fun toastMsg(s: String) {
        Toast.makeText(TApp.context, s, Toast.LENGTH_SHORT).show()
    }

    fun toastMsg(stringResId: Int) {
        Toast.makeText(TApp.context, stringResId, Toast.LENGTH_SHORT).show()
    }

    fun d(clz: Class<*>, msg: String) {
        Timber.d(clz.simpleName + msg)
    }

    fun generateRandomString(length: Int): String {
        //TODO: USE crypto alg.
        return "RANDOM:" + Random().nextInt()
    }

    fun getGson(): Gson {
        return GsonBuilder()
                .setPrettyPrinting()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .disableHtmlEscaping().create()
    }

    fun debugHub(s: String) {
        d(HubClient::class.java, s)
        //android.util.Log.e(HubClient::class.java.simpleName, s)
    }

    fun connectedEvent(): Observable<Connectivity> {
        return ReactiveNetwork.observeNetworkConnectivity(TApp.context)
                .filter {
                    it.state == NetworkInfo.State.CONNECTED
                }.take(1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun <T> throttleDbEvent(orig: Observable<T>, intervalSecs: Long): Observable<T> {
        return orig.throttleFirst(intervalSecs, TimeUnit.SECONDS)
    }

    fun genBip44Path(account: Int, isChange: Int, index: Int): String {
        return """m/44'/0'/$account'/$isChange/$index"""
    }

    fun genBip44Path(myAddresses: MyAddresses): String {
        return genBip44Path(myAddresses.account, myAddresses.isChange, myAddresses.addressIndex)
    }

    fun decodeJsStr(jsString: String): String {
        //TODO: just remove first and last double quota.
        return jsString.filterNot { it == '"' }
    }

    fun encodeJsStr(planeStr: String): String {
        return """"$planeStr""""
    }

    fun parseChild(parentEntity: TBaseEntity, origJson: JsonObject, clzFullName: String, vararg childJsonKey: String): List<Any> {
        var gson = Utils.getGson()

        assert(childJsonKey.isNotEmpty())
        var childrenAsJsonArray: JsonArray

        if (childJsonKey.size == 1) {
            childrenAsJsonArray = origJson.getAsJsonArray(childJsonKey[0])
        } else {
            var json = origJson
            for (index in 0..childJsonKey.size - 2) {
                json = json.getAsJsonObject(childJsonKey[index])
            }
            childrenAsJsonArray = json.getAsJsonArray(childJsonKey[childJsonKey.size - 1])
        }

        val children = List(childrenAsJsonArray.size()) { index: Int ->
            val child = gson.fromJson(childrenAsJsonArray[index], Class.forName(clzFullName)) as TBaseEntity
            child.json = childrenAsJsonArray[index].asJsonObject
            child.parentJson = origJson
            child.parent = parentEntity

            child
        }
        return children
    }

    fun parseJsonArray(jString: String): JsonArray {
        val parser = JsonParser()
        return parser.parse(jString) as JsonArray
    }

    fun genJsonObject(attr: String, value: String): JsonObject {
        val res = JsonObject()
        res.addProperty(attr, value)
        return res
    }

    fun toGsonString(o: Any): String {
        return getGson().toJson(o)
    }

    fun toGsonObject(o: Any): JsonObject {
        return getGson().toJsonTree(o) as JsonObject
    }

    fun write2File(file: File, o: Any) {
        file.bufferedWriter().use {
            it.write(getGson().toJson(o))
        }
    }

    fun readFileAsTProfile(file: File): TProfile {
        if (!file.exists() || file.length() < 10) {
            throw RuntimeException("TProfile file does not exist")
        }
        return getGson().fromJson(file.bufferedReader(), TProfile::class.java)
    }

    fun hash(input: String): String {
        return input
    }

    @Synchronized
    fun getMySdcardDirectory(): File {
        val pInfo = TApp.context.packageManager.getPackageInfo(TApp.context.packageName, 0)
        val versionName = pInfo.versionName
        val res = File(Environment.getExternalStorageDirectory(), "TTT_$versionName")
        if (!res.exists()) {
            res.mkdir()
        }
        return res
    }

    fun isUseTestData(): Boolean {
        return BuildConfig.DEBUG && BuildConfig.FLAVOR == "devnet"
    }

    fun formatAddressWithEllipse(address: String): String {
        return if (address.isBlank()) "" else """${address.substring(0, 5)}…${address.takeLast(3)}"""
    }

    fun formatTxTimestamp(ts: Long): String {
        val date = Date(ts * 1000L)
        val df = SimpleDateFormat("MM-dd  HH:MM")
        return df.format(date)
    }

    fun formatTxTimestampInTxDetail(ts: Long): String {
        val date = Date(ts * 1000L)
        val df = SimpleDateFormat("yyyy/MM/dd HH:MM(详细逻辑)")
        return df.format(date)
    }

    fun stringToJsonObject(str: String): JsonObject {
        if (str.isBlank() || str.length < 4) {
            return emptyJsonObject
        }

        try {
            return Utils.getGson().fromJson(str, JsonObject::class.java)
        } catch (ex: Exception) {
            Utils.logW(ex.localizedMessage)
        }
        return emptyJsonObject

    }

}

