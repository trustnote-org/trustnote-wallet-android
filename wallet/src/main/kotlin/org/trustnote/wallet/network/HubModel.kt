package org.trustnote.wallet.network

import org.trustnote.wallet.biz.msgs.MsgsModel
import org.trustnote.wallet.biz.wallet.WalletManager
import org.trustnote.wallet.network.HubMsgFactory.SUBJECT_HUB_HAVE_UPDATES
import org.trustnote.wallet.network.HubMsgFactory.SUBJECT_HUB_MESSAGE
import org.trustnote.wallet.network.HubMsgFactory.SUBJECT_JOINT
import org.trustnote.wallet.network.pojo.HubJustSaying
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
            instance.mDefaultHubAddress = TTTUtils.getDefaultHubAddressBySeed(hubNuberForPairId)
        }
    }

    fun onMessage(hubMsg: HubMsg) {

        if (hubMsg is HubJustSaying && SUBJECT_HUB_MESSAGE == hubMsg.subject) {
            MsgsModel.instance.onMessage(hubMsg)
        }

        if (hubMsg is HubJustSaying && SUBJECT_HUB_HAVE_UPDATES == hubMsg.subject) {
            WalletManager.model.onMessage(hubMsg)
        }

        if (hubMsg is HubJustSaying && SUBJECT_JOINT == hubMsg.subject) {
            WalletManager.model.onNewJoint(hubMsg)
        }

    }

    fun sendHubMsg(hubMsg: HubMsg) {

        HubManager.instance.sendHubMsg(hubMsg)

    }

    fun clear() {

        HubManager.instance.clear()

    }

}