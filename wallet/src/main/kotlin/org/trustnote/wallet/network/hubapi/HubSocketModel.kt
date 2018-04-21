package org.trustnote.wallet.network.hubapi

import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import org.trustnote.wallet.TTT
import org.trustnote.wallet.network.RequestMap
import org.trustnote.wallet.util.Utils


class HubSocketModel {

    val mHeartbeatTag = Utils.generateRandomString(30)
    val mHubAddress = TTT.testHubAddress
    val mRequestMap = RequestMap()
    val mSubject: Subject<HubMsg> = PublishSubject.create()
    lateinit var mHubClient: HubClient
    lateinit var mHeartBeatTask: HeartBeatTask
}