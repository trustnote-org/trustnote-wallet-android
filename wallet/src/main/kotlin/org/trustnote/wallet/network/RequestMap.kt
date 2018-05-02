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
    fun remove(hubMsg: HubMsg) {
        if (hubMsg.msgType == MSG_TYPE.response) {
            Utils.debugHub("remove for tag:" + (hubMsg as HubResponse).tag)
            cacheReq.remove((hubMsg).tag)
        }
    }


    @Synchronized
    fun getExpectedResBodyType(tag: String): MSG_TYPE {
        return cacheTag.get(tag)!!
    }

    //TODO: How about Hub return a wrong tag.
    @Synchronized
    fun getHubRequest(tag: String): HubRequest {
        var res = cacheReq.get(tag)
        if (res is HubRequest) {
            return res
        } else {
            return HubRequest(MSG_TYPE.ERROR)
        }
    }

    @Synchronized //TODO: return an iterator.
    fun getRetryMap(): HashMap<String, HubMsg>{
        return cacheReq
    }

}
