package org.trustnote.wallet.network

import org.trustnote.wallet.network.pojo.HubMsg
import org.trustnote.wallet.network.pojo.HubRequest
import org.trustnote.wallet.network.pojo.HubResponse
import org.trustnote.wallet.network.pojo.MSG_TYPE
import org.trustnote.wallet.util.Utils
import java.util.*

class RequestMap {

    //private val cacheTag = HashMap<String, MSG_TYPE>()
    private val cacheReq = Collections.synchronizedMap(HashMap<String, HubRequest>())

    init {
    }

    //Bug: when put to cache, if the same tag already exist, the older request will never finished.
    @Synchronized
    fun put(hubMsg: HubMsg) {
        if (hubMsg.msgType == MSG_TYPE.request) {
            cacheReq[(hubMsg as HubRequest).tag] = hubMsg
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

    //TODO: How about Hub return a wrong tag.
    @Synchronized
    fun getHubRequest(tag: String): HubRequest? {
        return cacheReq[tag]
    }

    @Synchronized //TODO: return an iterator.
    fun getRetryMap(): Map<String, HubMsg> {
        return cacheReq
    }

    fun clear() {
        cacheReq.clear()
        //cacheTag.clear()
    }

}
