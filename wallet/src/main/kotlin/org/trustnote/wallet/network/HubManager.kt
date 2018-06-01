package org.trustnote.wallet.network

import org.trustnote.wallet.util.Utils

//TODO: test case when HubManager return empty\strange result.

class HubManager {

    init {
        reConnectHub()
    }

    //TODO: remove.
    lateinit private var currentHub: HubSocketModel
    var latestWitnesses: MutableList<String> = mutableListOf()

    companion object {
        @JvmStatic
        val instance = HubManager()

        fun disconnect(dbTag: String) {
            //TODO:
        }
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

    fun getCurrentHub(): HubSocketModel {
        return this.currentHub
    }



    //@SuppressLint("NewApi")
//    private fun monitorResponse(bodyType: HubMsg.BODY_TYPE, txType: Consumer<HubResponse>) {
////        subject.filter { it.subjectType == bodyType }.observeOn(Schedulers.computation()).subscribe { res: HubResponse ->
////            txType.accept(res)
////        }
//    }

    private fun monitorConnection() {
        Utils.debugLog("monitorConnection")
//        subject.filter { it.msgType == HubMsg.MSG_TYPE.CLOSED }.observeOn(Schedulers.computation()).subscribe {
//            //TODO: should monitor network status change event and take txType.
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

