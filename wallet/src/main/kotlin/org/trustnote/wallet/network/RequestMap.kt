package org.trustnote.wallet.network

import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import org.trustnote.wallet.network.hubapi.HubMsg
import org.trustnote.wallet.network.hubapi.HubRequest
import org.trustnote.wallet.network.hubapi.HubResponse
import org.trustnote.wallet.network.hubapi.MSG_TYPE
import org.trustnote.wallet.util.Utils
import java.util.concurrent.TimeUnit


class RequestMap {

    private val cacheTag = HashMap<String, MSG_TYPE>()
    private val cacheReq = HashMap<String, HubMsg>()

    init {
    }

    @Synchronized
    fun put(hubMsg: HubMsg) {
        if (hubMsg.msgType == MSG_TYPE.request) {
            cacheReq.put((hubMsg as HubRequest).tag, hubMsg)
        }
    }

    @Synchronized
    fun responseMsgArrived(hubMsg: HubMsg) {
        if (hubMsg.msgType == MSG_TYPE.response) {
            Utils.debugHub("responseMsgArrived for tag:" + (hubMsg as HubResponse).tag)
            cacheReq.remove((hubMsg as HubResponse).tag)
        }
    }


    @Synchronized
    fun getExpectedResBodyType(tag: String): MSG_TYPE {
        return cacheTag.get(tag)!!
    }

    @Synchronized
    fun getHubRequest(tag: String): HubRequest {
        return cacheReq.get(tag) as HubRequest
    }

    @Synchronized //TODO: return an iterator.
    fun getRetryMap(): HashMap<String, HubMsg>{
        return cacheReq
    }

}
