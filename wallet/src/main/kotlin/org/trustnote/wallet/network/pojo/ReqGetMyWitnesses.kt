package org.trustnote.wallet.network.pojo

import org.trustnote.wallet.biz.units.UnitsManager
import org.trustnote.wallet.network.HubModel
import org.trustnote.wallet.network.HubMsgFactory.CMD_GET_WITNESSES

class ReqGetMyWitnesses(): HubRequest(CMD_GET_WITNESSES, tag = HubModel.instance.getRandomTag()) {

    override fun handleResponse(): Boolean {
        return true
    }

}