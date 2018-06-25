package org.trustnote.wallet.network.pojo

import org.trustnote.wallet.network.HubMsgFactory

class JustSayingLogin : HubJustSaying {

    constructor(challenge: String, pubkey: String, privKey: String) : super(HubMsgFactory.SUBJECT_HUB_LOGIN, HubMsgFactory.composeLoginBody(challenge, pubkey, privKey)) {

    }

}

