package org.trustnote.wallet.network.pojo

import org.trustnote.wallet.network.HubMsgFactory.CMD_HEARTBEAT

class ReqHeartBeat(reqId: String): HubRequest(CMD_HEARTBEAT, reqId) {

    override fun handleResponse(): Boolean {
        return true
    }

}