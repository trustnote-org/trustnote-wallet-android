package org.trustnote.wallet.network.hubapi;

import java.util.TimerTask;

public class HeartBeatTask extends TimerTask {
    HubClient walletClient;
    HeartBeatTask(HubClient c) {
        this.walletClient = c;
    }

    @Override
    public void run() {
        if (walletClient.isOpen()) {
            //System.out.println("HeartBeat");
            walletClient.send(HubRequest.reqHeartBeat());
        }
    }


}

