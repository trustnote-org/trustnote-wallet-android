package org.trustnote.wallet.network

import com.google.gson.FieldNamingPolicy
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import java.net.URI
import com.google.gson.GsonBuilder
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
        val gson = GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create()

        val element = gson.fromJson(jointsArray.toString(), JsonElement::class.java)

        val data: List<Joints> = gson.fromJson(element, Array<Joints>::class.java).toList()

        Utils.debugLog("RES size= " + data.size)

        val db = TrustNoteDataBase.getInstance(TApp.context)

        db?.unitsDao()?.insert(data[1].unit)

    }

    companion object {
        const val hubAddress = "wss://raytest.trustnote.org:443"
        @JvmStatic
        val instance = Hub()
    }

    private fun reConnectHub() {
        hubClient = HubClient(URI(hubAddress))
        hubClient.init(subject)
        hubClient.connect()
    }

    fun getHubSubject(): Observable<HubResponse> {
        return subject.filter {
            it.msgType == HubPackageBase.MSG_TYPE.response && it.body != null && it.body.get("response") != null && it.body.get("tag").asString == "bOo0Eeq5jWT8D0fwStljdp6T8JDIqaaKWEpzhQUgOvc="
        }
    }

    fun queryHistoryAndSave() {
        hubClient.send(HubRequest.reqGetHistory())
    }

}

