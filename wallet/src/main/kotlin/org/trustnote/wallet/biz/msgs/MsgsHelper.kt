package org.trustnote.wallet.biz.msgs

import org.trustnote.wallet.R
import org.trustnote.wallet.TApp
import org.trustnote.wallet.uiframework.ActivityBase
import org.trustnote.wallet.uiframework.FragmentEditBase
import org.trustnote.wallet.util.AndroidUtils
import org.trustnote.wallet.util.Prefs

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
