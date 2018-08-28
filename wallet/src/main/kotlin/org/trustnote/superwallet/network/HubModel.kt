package org.trustnote.superwallet.network

import org.trustnote.superwallet.biz.msgs.MessageModel
import org.trustnote.superwallet.biz.wallet.WalletManager
import org.trustnote.superwallet.network.HubMsgFactory.SUBJECT_LIGHT_HAVE_UPDATES
import org.trustnote.superwallet.network.HubMsgFactory.SUBJECT_HUB_MESSAGE
import org.trustnote.superwallet.network.HubMsgFactory.SUBJECT_JOINT
import org.trustnote.superwallet.network.pojo.HubJustSaying
import org.trustnote.superwallet.network.pojo.HubMsg
import org.trustnote.superwallet.util.TTTUtils
import org.trustnote.superwallet.util.Utils

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
            MessageModel.instance.onMessage(hubMsg)
        }

        if (hubMsg is HubJustSaying && SUBJECT_LIGHT_HAVE_UPDATES == hubMsg.subject) {
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