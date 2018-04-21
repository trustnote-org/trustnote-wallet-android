package org.trustnote.wallet.network

import org.trustnote.wallet.network.hubapi.HubMsg
import org.trustnote.wallet.network.hubapi.HubRequest
import org.trustnote.wallet.network.hubapi.MSG_TYPE

class RequestMap {

    private val cacheTag = HashMap<String, MSG_TYPE>()
    private val cacheReq = HashMap<String, HubRequest>()

    @Synchronized fun put(hubRequest: HubRequest) {
        cacheReq.put(hubRequest.tag, hubRequest)
    }

    @Synchronized fun getExpectedResBodyType(tag: String): MSG_TYPE {
        return cacheTag.get(tag)!!
    }

    @Synchronized fun getHubRequest(tag: String): HubRequest {
        return cacheReq.get(tag)!!
    }

}
