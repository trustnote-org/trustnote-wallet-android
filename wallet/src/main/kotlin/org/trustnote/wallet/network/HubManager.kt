package org.trustnote.wallet.network

import org.trustnote.db.DbHelper
import org.trustnote.wallet.network.hubapi.HubClient
import org.trustnote.wallet.network.hubapi.HubResponse
import org.trustnote.wallet.network.hubapi.HubSocketModel
import org.trustnote.wallet.util.Utils

//TODO: test case when HubManager return empty\strange result.

class HubManager {

    init {
        reConnectHub()
    }

    //TODO: remove.
    lateinit private var currentHub: HubSocketModel

    private fun handleResponse(it: HubResponse) {
        DbHelper.saveUnit(it)
    }

    companion object {
        @JvmStatic
        val instance = HubManager()
    }



    fun reConnectHub(oldHubSocketModel: HubSocketModel) {
        //TODO: move dispose logic to model.
        oldHubSocketModel.dispose()
        reConnectHub()
    }

    fun reConnectHub() {
        val hubSocketModel = HubSocketModel()
        this.currentHub = hubSocketModel
        val hubClient = HubClient(hubSocketModel)
        hubClient.connect()
        hubSocketModel.setupRetryLogic()
    }

    fun getCurrentHub(): HubSocketModel{
        return this.currentHub
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

}

