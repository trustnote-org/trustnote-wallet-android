package org.trustnote.wallet.network

import android.annotation.SuppressLint
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import org.trustnote.db.DbHelper
import org.trustnote.wallet.TTT
import org.trustnote.wallet.network.hubapi.HubClient
import org.trustnote.wallet.network.hubapi.HubPackageBase
import org.trustnote.wallet.network.hubapi.HubRequest
import org.trustnote.wallet.network.hubapi.HubResponse
import org.trustnote.wallet.util.Utils
import java.net.URI
import java.util.function.Consumer

//TODO: some request need retry logic. such as get witnesses.

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
        const val hubAddress = TTT.testHubAddress
        @JvmStatic
        val instance = Hub()
    }

    fun reConnectHub() {
        hubClient = HubClient(URI(hubAddress))
        subject = PublishSubject.create()
        hubClient.init(subject)

        hubClient.connect()

        monitorConnection()

        monitorResponse(HubPackageBase.BODY_TYPE.RES_GET_WITNESSES, Consumer<HubResponse>{
            Utils.debugLog("RES_GET_WITNESSES:" + it.body.toString())
        })
    }

    @SuppressLint("NewApi")
    private fun monitorResponse(bodyType: HubPackageBase.BODY_TYPE, action: Consumer<HubResponse>) {
        subject.filter { it.subjectType == bodyType }.observeOn(Schedulers.computation()).subscribe { res: HubResponse ->
            action.accept(res)
        }
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

