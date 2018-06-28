package org.trustnote.wallet.biz.msgs

import org.trustnote.wallet.uiframework.FragmentBase

abstract class FragmentMsgsBase : FragmentBase() {


    val model: MessageModel = MessageModel.instance

    override fun onResume() {
        super.onResume()
        listener(model.mMessagesEventCenter)
    }


}

