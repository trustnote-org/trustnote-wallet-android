package org.trustnote.wallet.network.hubapi

import java.util.TimerTask

class HeartBeatTask internal constructor(internal var walletClient: HubClient) : TimerTask() {

    override fun run() {
        if (walletClient.isOpen) {
            //System.out.println("HeartBeat");
            walletClient.send(HubRequest.reqHeartBeat())
        }
    }


}

