package org.trustnote.wallet.network.hubapi

import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import org.trustnote.wallet.TTT
import org.trustnote.wallet.network.RequestMap
import org.trustnote.wallet.util.Utils
import java.util.concurrent.TimeUnit


class HubSocketModel {

    val mGetWitnessTag = Utils.generateRandomString(30)
    val mHeartbeatTag = Utils.generateRandomString(30)
    val mHubAddress = TTT.testHubAddress
    val mRequestMap = RequestMap()
    val mSubject: Subject<HubMsg> = PublishSubject.create()
    lateinit var mHubClient: HubClient
    lateinit var mHeartBeatTask: HeartBeatTask


    fun setupRetryLogic() {
        Observable.interval(60, TimeUnit.SECONDS).observeOn(Schedulers.computation()).subscribe {
            retry()
        }
    }

    @Synchronized
    private fun retry() {
        for ((tag, hubMsg) in mRequestMap.getRetryMap()) {
            if (hubMsg.shouldRetry()) {
                Utils.debugHub("retry with:" + hubMsg.toHubString())
                mHubClient.sendHubMsg(hubMsg)
            }
        }
    }

}