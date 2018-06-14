package org.trustnote.wallet.biz.msgs

import org.trustnote.db.entity.ChatMessages
import org.trustnote.wallet.biz.wallet.TestData
import org.trustnote.wallet.util.Utils

class MsgsModel {

    companion object {
        val instance = MsgsModel()
    }

    var latestHomeList: List<ChatMessages> = listOf()

    fun refreshHomeList() {
        latestHomeList = TestData.getHomelistForMsgs().sortedByDescending { it.creationDate }
    }

    fun isRefreshing(): Boolean {
        return false
    }

}

