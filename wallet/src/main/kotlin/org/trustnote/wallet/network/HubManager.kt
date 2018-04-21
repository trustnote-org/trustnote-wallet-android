package org.trustnote.wallet.network

import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import org.trustnote.db.DbHelper
import org.trustnote.wallet.TTT
import org.trustnote.wallet.network.hubapi.HubClient
import org.trustnote.wallet.network.hubapi.HubResponse
import org.trustnote.wallet.network.hubapi.HubSocketModel
import org.trustnote.wallet.util.Utils
import java.net.URI

//TODO: some request need retry logic. such as get witnesses.
//TODO: test case when HubManager return empty\strange result.

class HubManager {

    init {
        reConnectHub()
    }

    private fun handleResponse(it: HubResponse) {
        DbHelper.saveUnit(it)
    }

    companion object {
        @JvmStatic
        val instance = HubManager()
    }

    fun reConnectHub() {
        val hubSocketModel = HubSocketModel()
        val hubClient = HubClient(hubSocketModel)
        hubClient.connect()

//        subject = PublishSubject.create()
//        hubClient.init(subject)
//
//        hubClient.connect()
//
//        monitorConnection()

//        monitorResponse(HubMsg.BODY_TYPE.RES_GET_WITNESSES, Consumer<HubResponse>{
//            Utils.debugLog("RES_GET_WITNESSES:" + it.msgJson.toString())
//        })
    }

    //@SuppressLint("NewApi")
//    private fun monitorResponse(bodyType: HubMsg.BODY_TYPE, action: Consumer<HubResponse>) {
////        subject.filter { it.subjectType == bodyType }.observeOn(Schedulers.computation()).subscribe { res: HubResponse ->
////            action.accept(res)
////        }
//    }

    private fun monitorConnection() {
        Utils.debugLog("monitorConnection")
//        subject.filter { it.msgType == HubMsg.MSG_TYPE.CLOSED }.observeOn(Schedulers.computation()).subscribe {
//            //TODO: should monitor network status change event and take action.
//            Thread.sleep(10000)
//            reConnectHub()
//        }
    }

//    fun getHubSubject(): Observable<HubResponse> {
//        return subject.filter {
//            it.msgType == HubMsg.MSG_TYPE.response && it.msgJson != null && it.msgJson.get("response") != null && it.msgJson.get("tag").asString == "bOo0Eeq5jWT8D0fwStljdp6T8JDIqaaKWEpzhQUgOvc="
//        }
//    }

    fun queryHistoryAndSave() {
        //hubClient.send(HubRequest.reqGetHistory())
    }


}

