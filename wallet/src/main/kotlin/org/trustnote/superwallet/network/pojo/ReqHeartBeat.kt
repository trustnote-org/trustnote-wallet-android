package org.trustnote.superwallet.network.pojo

import org.trustnote.superwallet.network.HubMsgFactory.CMD_HEARTBEAT

class ReqHeartBeat(reqId: String): HubRequest(CMD_HEARTBEAT, reqId) {

    override fun handleResponse(): Boolean {
        return true
    }

}