package org.trustnote.wallet.network

import org.trustnote.wallet.network.hubapi.HubMsg
import org.trustnote.wallet.network.hubapi.HubRequest
import org.trustnote.wallet.network.hubapi.MSG_TYPE

class RequestMap {

    private val cacheTag = HashMap<String, MSG_TYPE>()
    private val cacheReq = HashMap<String, HubMsg>()

    @Synchronized
    fun put(hubMsg: HubMsg) {
        if (hubMsg.msgType == MSG_TYPE.request) {
            cacheReq.put((hubMsg as HubRequest).tag, hubMsg)
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

}
