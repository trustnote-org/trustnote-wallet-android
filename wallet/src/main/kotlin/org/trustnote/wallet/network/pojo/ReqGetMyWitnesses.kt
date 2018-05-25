package org.trustnote.wallet.network.pojo

import org.trustnote.wallet.biz.units.UnitsManager
import org.trustnote.wallet.network.HubMsgFactory.CMD_GET_WITNESSES

class ReqGetMyWitnesses(reqId: String): HubRequest(CMD_GET_WITNESSES, reqId) {

    override fun handleResponse(): Boolean {
        return true
    }

}