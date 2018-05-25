package org.trustnote.wallet.network

import org.trustnote.wallet.network.pojo.HubMsg
import org.trustnote.wallet.network.pojo.HubRequest
import org.trustnote.wallet.network.pojo.HubResponse
import org.trustnote.wallet.network.pojo.MSG_TYPE
import org.trustnote.wallet.util.Utils
import java.util.*

class RequestMap {

    private val cacheTag = HashMap<String, MSG_TYPE>()
    private val cacheReq = Collections.synchronizedMap(HashMap<String, HubMsg>())

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
    fun remove(tag: String) {
        cacheReq.remove(tag)
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
    fun getRetryMap(): Map<String, HubMsg> {
        return cacheReq
    }

}
