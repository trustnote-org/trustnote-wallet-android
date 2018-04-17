package org.trustnote.wallet.network

import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import org.json.JSONArray
import org.trustnote.db.entity.Units
import java.net.URI
import com.google.gson.Gson
import org.trustnote.wallet.util.Utils
import com.google.gson.JsonElement
import org.trustnote.db.TrustNoteDataBase
import org.trustnote.wallet.TApp
import org.trustnote.wallet.network.hubapi.*


class Hub {

    private var hubClient: HubClient
    private val subject: Subject<HubResponse> = PublishSubject.create()

    init {
        hubClient = HubClient(URI(hubAddress))
        hubClient.init(subject)
        hubClient.connect()

        getHubSubject().subscribe() {
            handleResponse(it!!)
        }
    }

    private fun handleResponse(it: HubResponse) {
        val jointsArray = it.body.getJSONObject("response").getJSONArray("joints")
        val gson = Gson()

        val element = gson.fromJson(jointsArray.toString(), JsonElement::class.java)


        val data: List<GsonGetHistory> = gson.fromJson(element, Array<GsonGetHistory>::class.java).toList()

        Utils.debugLog("RES size= " + data.size)

        val db = TrustNoteDataBase.getInstance(TApp.context)

        db?.unitsDao()?.insert(data[1].unit)

    }

    companion object {
        const val hubAddress = "wss://raytest.trustnote.org:443"
        @JvmStatic val instance = Hub()
    }

    private fun reConnectHub() {
        hubClient = HubClient(URI(hubAddress))
        hubClient.init(subject)
        hubClient.connect()
    }

    fun getHubSubject(): Observable<HubResponse> {
        return subject.filter{
            it.msgType == HubPackageBase.MSG_TYPE.response && it.body != null && it.body.optJSONObject("response") != null && it.body.optString("tag") == "bOo0Eeq5jWT8D0fwStljdp6T8JDIqaaKWEpzhQUgOvc="
        }
    }

    fun queryHistoryAndSave() {
        hubClient.send(HubRequest.reqGetHistory())
    }

}

