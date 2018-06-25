package org.trustnote.wallet.network.pojo

import org.trustnote.wallet.network.HubMsgFactory

class JustSayingLogin : HubJustSaying {

    //    ["justsaying",{"subject":"hub/login","body":{"challenge":"m0dxaegeZyT/GZI//j2cMK0CdK57iF6dLqEI51gk",
    // "pubkey":"Aki0PI8ouQau9uUATpGMwJVCFyZBw+tOkcfw34KioqTS",
    // "signature":"f3++Cx1+gv4KC2Hf8fWoDZ65nKdhSuOJ0CfACOYfM9p3biURsVWAD9xtUX537AwBRMwksSzuOZBWibCR+W3N6w=="}}]

    constructor(challenge: String) : super(HubMsgFactory.SUBJECT_HUB_LOGIN,
            HubMsgFactory.signOneString("challenge", challenge))

}

