package org.trustnote.wallet.network

import org.trustnote.wallet.biz.TTT
import org.trustnote.wallet.util.MyThreadManager
import org.trustnote.wallet.util.Utils

//TODO: test case when HubManager return empty\strange result.

class HubManager {

    init {
        reConnectHubWithDelay()
    }

    //TODO: remove.
    lateinit private var currentHub: HubModel
    var latestWitnesses: MutableList<String> = mutableListOf()

    companion object {
        @JvmStatic
        val instance = HubManager()

        fun disconnect(dbTag: String) {
            instance.reConnectHubWithDelay()
        }
    }

    fun reConnectHubWithDelay(oldHubSocketModel: HubModel) {

        MyThreadManager.instance.runDealyed(TTT.HUB_WAITING_SECONDS_RECCONNECT) {

            reConnectHub(oldHubSocketModel)

        }
    }

    private fun reConnectHub(oldHubSocketModel: HubModel) {
        oldHubSocketModel.dispose()
        val hubSocketModel = HubModel()
        this.currentHub = hubSocketModel
        val hubClient = HubClient(hubSocketModel)
        hubClient.connect()
        hubSocketModel.setupRetryLogic()
    }

    fun getCurrentHub(): HubModel {
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
//            reConnectHubWithDelay()
//        }
    }

    fun hubClosed(mHubAddress: String) {
        
    }

    fun hubOpened(hubClient: HubClient) {

    }

    fun onMessageArrived(hubAddress: String, message: String) {
        val hubMsg = HubMsgFactory.parseMsg(hubAddress, message)
        //TODO: hubResponse.onMessageArrived
    }

//    fun getHubSubject(): Observable<HubResponse> {
//        return subject.filter {
//            it.msgType == HubMsg.MSG_TYPE.response && it.msgJson != null && it.msgJson.get("response") != null && it.msgJson.get("tag").asString == "bOo0Eeq5jWT8D0fwStljdp6T8JDIqaaKWEpzhQUgOvc="
//        }
//    }

}

