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

object Utils {

    val emptyJsonObject = JsonObject()

    fun debugLog(msg: String) {
        Timber.d("TTT" + msg)
    }

    fun logW(msg: String) {
        Timber.w("TTT" + msg)
    }

    fun debugJS(s: String) {
        android.util.Log.e("JSApi", s)
    }

    fun crash(s: String) {
        //TODO:
        android.util.Log.e("CRASH", s)
        throw RuntimeException(s)
    }

    fun debugToast(s: String) {
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

    //TODO: Bug?? Thread Manager
    fun computeThread(action: () -> Any) {
        Thread {
            action
        }.start()
    }

    //TODO:
    fun runInbackground(runnable: Runnable) {
        Thread { runnable.run() }.start()
    }

    fun generateRandomString(length: Int): String {
        //TODO: USE crypto alg.
        return "RANDOM:" + Random().nextInt()
    }


    fun getGson(): Gson {
        return GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create()
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

    fun genJsBip44Path(account: Int, isChange: Int, index: Int): String {
        //Sample = "m/44'/0'/0'/1/2"
        return """"m/44'/0'/$account'/$isChange/$index""""
    }

    fun genJsBip44Path(myAddresses: MyAddresses): String {
        //Sample = "m/44'/0'/0'/1/2"
        return genJsBip44Path(myAddresses.account, myAddresses.isChange, myAddresses.addressIndex)
    }

    fun jsStr2NormalStr(jsString: String): String {
        return jsString.filterNot { it == '"' }
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

}

