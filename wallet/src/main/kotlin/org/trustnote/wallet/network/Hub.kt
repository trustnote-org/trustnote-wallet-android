package org.trustnote.wallet.network

import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import org.trustnote.wallet.network.hubapi.HubClient
import org.trustnote.wallet.network.hubapi.HubPackageBase
import org.trustnote.wallet.network.hubapi.HubResponse
import java.net.URI

class Hub {

    private var hubClient: HubClient
    private val subject: Subject<HubResponse> = PublishSubject.create()

    init {
        hubClient = HubClient(URI(hubAddress))
        hubClient.init(subject)
        hubClient.connect()
    }

    companion object {
        const val hubAddress = "wss://raytest.trustnote.org:443"
        val instance = Hub()
    }

    private fun reConnectHub() {
        hubClient = HubClient(URI(hubAddress))
        hubClient.init(subject)
        hubClient.connect()
    }

    fun getHubSubject(): Observable<HubResponse> {
        return subject.filter{
            it.msgType == HubPackageBase.MSG_TYPE.request
        }
    }

}

