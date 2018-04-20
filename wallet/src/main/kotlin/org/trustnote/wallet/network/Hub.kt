package org.trustnote.wallet.network

import com.google.gson.FieldNamingPolicy
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import java.net.URI
import com.google.gson.GsonBuilder
import org.trustnote.wallet.util.Utils
import com.google.gson.JsonElement
import io.reactivex.schedulers.Schedulers
import org.trustnote.db.DbHelper
import org.trustnote.db.TrustNoteDataBase
import org.trustnote.wallet.TApp
import org.trustnote.wallet.network.hubapi.*


class Hub {

    private var hubClient: HubClient = HubClient(URI(hubAddress))
    private var subject: Subject<HubResponse> = PublishSubject.create()

    init {
        reConnectHub()
    }

    private fun handleResponse(it: HubResponse) {
        DbHelper.saveUnit(it)
    }

    companion object {
        const val hubAddress = "wss://raytest.trustnote.org:443"
        @JvmStatic
        val instance = Hub()
    }

    fun reConnectHub() {
        hubClient = HubClient(URI(hubAddress))
        subject = PublishSubject.create()
        hubClient.init(subject)

        hubClient.connect()

        monitorConnection()
    }

    private fun monitorConnection() {
        Utils.debugLog("monitorConnection")
        subject.filter { it.msgType == HubPackageBase.MSG_TYPE.CLOSED }.observeOn(Schedulers.computation()).subscribe {
            //TODO: should monitor network status change event and take action.
            Thread.sleep(10000)
            reConnectHub()
        }
    }

//    fun getHubSubject(): Observable<HubResponse> {
//        return subject.filter {
//            it.msgType == HubPackageBase.MSG_TYPE.response && it.body != null && it.body.get("response") != null && it.body.get("tag").asString == "bOo0Eeq5jWT8D0fwStljdp6T8JDIqaaKWEpzhQUgOvc="
//        }
//    }

    fun queryHistoryAndSave() {
        hubClient.send(HubRequest.reqGetHistory())
    }


}

