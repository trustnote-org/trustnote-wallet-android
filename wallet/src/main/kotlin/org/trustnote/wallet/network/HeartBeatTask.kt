package org.trustnote.wallet.network

import org.trustnote.wallet.biz.TTT
import org.trustnote.wallet.network.pojo.ReqHeartBeat
import org.trustnote.wallet.util.MyThreadManager
import org.trustnote.wallet.util.Utils
import java.util.*
import java.util.concurrent.TimeUnit

class HeartBeatTask internal constructor(internal var hubClient: HubClient) : Runnable {

    private var mHeartBeatTimer: Timer = Timer(true)

    val mHeartbeatTag = Utils.generateRandomString(30)

    val executor = MyThreadManager.instance.newSingleThreadExecutor(hubClient.uri.toString())

    override fun run() {

        if (hubClient.isOpen) {
            hubClient.sendHubMsg(ReqHeartBeat(mHeartbeatTag))
        } else {
            stop()
        }

    }

    fun start() {

        executor.scheduleAtFixedRate(
                this,
                (Utils.random.nextInt(TTT.HUB_HEARTBEAT_FIRST_DELAY_SEC_MAX)).toLong(),
                (TTT.HUB_HEARTBEAT_INTERVAL_SEC).toLong(),
                TimeUnit.SECONDS)

    }

    fun stop() {

        executor.shutdownNow()

    }
}

