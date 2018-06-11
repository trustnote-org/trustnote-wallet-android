package org.trustnote.wallet.util

import android.net.NetworkInfo
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
import org.trustnote.wallet.biz.TTT
import org.trustnote.wallet.biz.init.FragmentInit
import org.trustnote.wallet.biz.wallet.TProfile
import java.io.File
import java.text.SimpleDateFormat

object Utils {


    val emptyJsonObject = JsonObject()
    val random = Random()

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

    //TODO: move to AndroidUtils.
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
        //TODO: USE crypto alg from JSApi.
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

    fun writeJson2File(file: File, o: Any) {
        file.bufferedWriter().use {
            it.write(getGson().toJson(o))
        }
    }

    fun readJsonFileAsObject(file: File, clz: Class<out Object>): Object {
        if (!file.exists() || file.length() < 10) {
            return clz.newInstance()
        }
        return getGson().fromJson(file.bufferedReader(), clz)
    }

    fun hash(input: String): String {
        return input
    }

    fun isTesterFeature(): Boolean {
        return BuildConfig.DEBUG && BuildConfig.FLAVOR == "devnet"
    }

    fun isDeveloperFeature(): Boolean {
        return BuildConfig.DEVELOPER_MODE
    }


    fun formatAddressWithEllipse(address: String): String {
        return if (address.isBlank()) "" else """${address.substring(0, 5)}…${address.takeLast(3)}"""
    }

    fun formatWalletIdEllipse(address: String): String {
        if (address.length <= 10) {
            return address
        }
        return """${address.substring(0, 5)}…${address.takeLast(5)}"""
    }

    fun nowTimeAsFileName(): String {
        val date = Date()
        val df = SimpleDateFormat("yyyy_MM_dd_HH_mm")
        return df.format(date)
    }

    fun formatTxTimestamp(ts: Long): String {
        val date = Date(ts * 1000L)
        val df = SimpleDateFormat("MM-dd  HH:mm")
        return df.format(date)
    }

    fun formatTxTimestampInTxDetail(ts: Long): String {
        val date = Date(ts * 1000L)
        val timeAgo = Utils.getTimeAgoForCn(ts)
        val df = SimpleDateFormat("""yyyy/MM/dd HH:mm""")
        return """${df.format(date)}($timeAgo)"""
    }


    fun getFeeAsString(fee: Long): String {
        return String.format("%.6f", fee.toFloat() / TTT.w_coinunitValue)
    }

    val emptyLambda = {}
    val emptyString = ""

    private val SECOND_MILLIS = 1000
    private val MINUTE_MILLIS = 60 * SECOND_MILLIS
    private val HOUR_MILLIS = 60 * MINUTE_MILLIS
    private val DAY_MILLIS = 24 * HOUR_MILLIS

    fun getTimeAgo(time: Long): String {
        var time = time
        if (time < 1000000000000L) {
            // if timestamp given in seconds, convert to millis
            time *= 1000
        }

        val now = System.currentTimeMillis()
        if (time > now || time <= 0) {
            return "ERR"
        }

        // TODO: localize
        val diff = now - time
        return if (diff < MINUTE_MILLIS) {
            "just now"
        } else if (diff < 2 * MINUTE_MILLIS) {
            "a minute ago"
        } else if (diff < 50 * MINUTE_MILLIS) {
            (diff / MINUTE_MILLIS).toString() + " minutes ago"
        } else if (diff < 90 * MINUTE_MILLIS) {
            "an hour ago"
        } else if (diff < 24 * HOUR_MILLIS) {
            (diff / HOUR_MILLIS).toString() + " hours ago"
        } else if (diff < 48 * HOUR_MILLIS) {
            "yesterday"
        } else {
            (diff / DAY_MILLIS).toString() + " days ago"
        }
    }

    fun getTimeAgoForCn(time: Long): String {
        var time = time
        if (time < 1000000000000L) {
            // if timestamp given in seconds, convert to millis
            time *= 1000
        }

        val now = System.currentTimeMillis()
        if (time > now || time <= 0) {
            return "ERR"
        }

        // TODO: localize
        val diff = now - time
        return if (diff < MINUTE_MILLIS) {
            "刚刚"
        } else if (diff < 2 * MINUTE_MILLIS) {
            "1分钟前"
        } else if (diff < 50 * MINUTE_MILLIS) {
            (diff / MINUTE_MILLIS).toString() + "分钟前"
        } else if (diff < 90 * MINUTE_MILLIS) {
            "1小时前"
        } else if (diff < 24 * HOUR_MILLIS) {
            (diff / HOUR_MILLIS).toString() + " 小时前"
        } else if (diff < 48 * HOUR_MILLIS) {
            "昨天"
        } else {
            (diff / DAY_MILLIS).toString() + " 天前"
        }
    }

    fun mnToNotes(inputAmount: String): Long {
        return (inputAmount.toFloat() * TTT.w_coinunitValue).toLong()
    }
}

