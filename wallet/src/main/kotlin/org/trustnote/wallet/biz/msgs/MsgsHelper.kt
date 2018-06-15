package org.trustnote.wallet.biz.msgs

import org.trustnote.db.entity.ChatMessages
import org.trustnote.wallet.R
import org.trustnote.wallet.TApp
import org.trustnote.wallet.uiframework.ActivityBase
import org.trustnote.wallet.uiframework.FragmentEditBase
import org.trustnote.wallet.util.AndroidUtils
import org.trustnote.wallet.util.Prefs
import org.trustnote.wallet.util.Utils

fun chatWithFriend(friendId: String, activity: ActivityBase) {

    val f = FragmentMsgsChat()
    AndroidUtils.addFragmentArguments(f, AndroidUtils.KEY_FRIEND_ID, friendId)
    activity.addL2Fragment(f)

}

fun editFriendMemoName(activity: ActivityBase) {
    val f = FragmentEditBase()
    f.buildPage(Prefs.readDeviceName(),
            TApp.getString(R.string.msg_friend_memo),
            {
                it.length <= 10
            },

            {
                //TODO:
            },
            TApp.getString(R.string.title_edit_friend_memo)
    )

    activity.addL2Fragment(f)

}

// 2.时间显示的规则：
//   a.时间按照24小时格式显示；
//   b.消息发生在当天的内容只显示时间，eg:14:32；
//   c.消息发生在前一天显示的内容为昨天+时间，eg:昨天 14:35；
//   d.消息发生昨天以前则显示：日期+时间，eg:4-7 14:32或是2017-4-7 14:32
// 3.当天的消息，以每5分钟为一个跨度的显示时间

val FIVE_MINUTES: Long = 5 * 60
val ONE_DAY: Long = 24 * 3600

fun getChatHistoryForDisplay(original: List<ChatMessages>): List<ChatMessages> {

    val sorted = original.sortedBy { it.creationDate }
    val todayMsgs = sorted.filter { Utils.isToday(it.creationDate * 1000) }
    val beforeTodayMsgs = sorted.filter { !Utils.isToday(it.creationDate * 1000) }

    debounceByDifferentInteger(beforeTodayMsgs,
            {
                it.creationDate / ONE_DAY
            },
            { isNew, data ->
                data.showTimeOrDate = isNew
            })

    debounceByDifferentInteger(todayMsgs,
            {
                it.creationDate
            },
            { isNew, data ->
                data.showTimeOrDate = isNew
            }, FIVE_MINUTES)

    return sorted
}

fun <T> debounceByDifferentInteger(data: List<T>, toInteger: (T) -> Long, updateLogic: (Boolean, T) -> Unit, gap: Long = 1) {

    val length = data.size
    var lastInteger = Long.MIN_VALUE

    for (i in 0 until length) {

        val currentInt = toInteger.invoke(data[i])

        updateLogic(currentInt > (lastInteger + gap - 1), data[i])

        lastInteger = if (currentInt > (lastInteger + gap - 1)) currentInt else lastInteger

    }
}


