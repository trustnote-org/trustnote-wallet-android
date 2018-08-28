package org.trustnote.superwallet.biz.msgs

import org.trustnote.superwallet.uiframework.FragmentBase

abstract class FragmentMsgsBase : FragmentBase() {


    val model: MessageModel = MessageModel.instance

    override fun onResume() {
        super.onResume()
        listener(model.mMessagesEventCenter)
    }


}

