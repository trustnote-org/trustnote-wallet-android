package org.trustnote.wallet.network.hubapi

import org.trustnote.wallet.TTT
import java.util.*

class HeartBeatTask internal constructor(internal var walletClient: HubClient) : TimerTask() {

    private var mHeartBeatTimer: Timer = Timer(true)

    override fun run() {
        if (walletClient.isOpen) {
            walletClient.sendHubMsg(HubMsgFactory.walletHeartBeat(walletClient.mHubSocketModel))
        }
    }

    fun start() {
        mHeartBeatTimer.scheduleAtFixedRate(this, (TTT.HUB_HEARTBEAT_FIRST_DELAY_SEC * 1000).toLong(), (TTT.HUB_HEARTBEAT_INTERVAL_SEC * 1000).toLong())
    }

    fun stop() {
        cancel()
        mHeartBeatTimer.cancel()
    }
}

