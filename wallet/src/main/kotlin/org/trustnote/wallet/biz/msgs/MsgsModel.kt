package org.trustnote.wallet.biz.msgs

import org.trustnote.db.entity.ChatMessages
import org.trustnote.db.entity.Friend
import org.trustnote.wallet.biz.wallet.TestData

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

    fun getMsgListForOneFriend(friendId: String): List<ChatMessages> {
        return latestHomeList
    }

    fun findFriendById(friendAddress: String): Friend {
        return TestData.createAFriend()
    }

}

