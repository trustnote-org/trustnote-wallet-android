package org.trustnote.wallet.network

import org.trustnote.wallet.network.pojo.HubMsg
import org.trustnote.wallet.util.TTTUtils
import org.trustnote.wallet.util.Utils

class HubModel {

    lateinit var mDefaultHubAddress: String

    fun getRandomTag(): String {
        return Utils.generateRandomString(30)
    }

    companion object {
        val instance = HubModel()
        fun init(hubNuberForPairId: Int) {
            instance.mDefaultHubAddress = "wss://${TTTUtils.getDefaultHubAddressBySeed(hubNuberForPairId)}"
        }
    }

    fun onMessage(hubMsg: HubMsg) {

    }

    fun sendHubMsg(hubMsg: HubMsg) {

        HubManager.instance.sendHubMsg(hubMsg)

    }

    fun clear() {

        HubManager.instance.clear()

    }

}