package org.trustnote.wallet.network.pojo

import org.trustnote.wallet.biz.wallet.WalletManager
import org.trustnote.wallet.network.HubModel
import org.trustnote.wallet.network.HubMsgFactory
import org.trustnote.wallet.network.HubMsgFactory.CMD_TEMP_PUBKEY

class ReqTempPubkey() : HubRequest(CMD_TEMP_PUBKEY, tag = HubModel.instance.getRandomTag()) {

    //    ["request",{"command":"hub/temp_pubkey",
    // "params":{"temp_pubkey":"A8q7mmQEpS1ogvicDRq5xpZvKYZ94BhOuxpKNu2dUqo7",
    // "pubkey":"Aki0PI8ouQau9uUATpGMwJVCFyZBw+tOkcfw34KioqTS",
    // "signature":"NPUJ6vyEIB0qL38JP6y7z5TXRW6L6G/fUZjhtHKKtVgpr3v1fcIOI4kTcMByuXd38T0uNbdKsz6WD/4h/jKmIQ=="},
    // "tag":"CF7RHeNx5S38tZAwHDeNwCnk/EX6eK7RCOpqw8uEv0g="}]

    //["response",{"tag":"mgdbmKtQBftJFbhtJ1+5DV6HlmK+npCZ3DuJ8u4LDuA=","response":"updated"}]
    override fun handleResponse(): Boolean {
        val resp = getResponse()
        if ("updated" != resp.responseJson.asString) {
            //TODO:
        }
        return true
    }

    init {
        val params = HubMsgFactory.signOneString("temp_pubkey", WalletManager.model.mProfile.tempPubkey)
        setReqParams(params)
    }

}