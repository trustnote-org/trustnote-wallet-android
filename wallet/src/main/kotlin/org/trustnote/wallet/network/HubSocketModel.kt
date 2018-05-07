package org.trustnote.wallet.network

import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import org.trustnote.db.DbHelper
import org.trustnote.wallet.TApp
import org.trustnote.wallet.TTT
import org.trustnote.wallet.network.pojo.*
import org.trustnote.wallet.util.Utils
import java.util.concurrent.TimeUnit

class HubSocketModel {

    val mGetWitnessTag = Utils.generateRandomString(30)
    val mHeartbeatTag = Utils.generateRandomString(30)

    //Maybe more than one getHistory cmd.
    val mGetHistoryTag = Utils.generateRandomString(30)
    val mHubAddress = TTT.hubAddress
    val mRequestMap = RequestMap()
    val mSubject: Subject<HubMsg> = PublishSubject.create()
    lateinit var mHubClient: HubClient
    lateinit var mHeartBeatTask: HeartBeatTask
    lateinit var retrySubscription: Disposable

    fun setupRetryLogic() {
        retrySubscription = Observable.interval(60, TimeUnit.SECONDS).observeOn(Schedulers.computation()).subscribe {
            retry()
        }
    }

    fun getRandomTag(): String {
        return Utils.generateRandomString(30)
    }

    @Synchronized
    private fun retry() {
        for ((tag, hubMsg) in mRequestMap.getRetryMap()) {
            if (shouldRetry(hubMsg)) {
                Utils.debugHub("retry with:" + hubMsg.toHubString())
                mHubClient.sendHubMsg(hubMsg)
            }
        }
    }

    private fun shouldRetry(hubMsg: HubMsg): Boolean {
        return (System.currentTimeMillis() - hubMsg.lastSentTime) > TTT.HUB_REQ_RETRY_SECS * 1000
    }

    //TODO: for future copy.
    private var connectivityDisposable: Disposable? = null

    fun onResume() {
        connectivityDisposable = ReactiveNetwork.observeNetworkConnectivity(TApp.context)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { connectivity ->
                    Utils.debugHub(connectivity.toString())
                    val state = connectivity.state
                    val name = connectivity.typeName
                    //TODO:
                    //connectivity_status.text = String.format("state: %s, typeName: %s", state, name)
                }
    }

    fun onPause() {
        safelyDispose(connectivityDisposable)
    }

    private fun safelyDispose(disposable: Disposable?) {
        if (disposable != null && !disposable.isDisposed) {
            disposable.dispose()
        }
    }

    fun dispose() {
        if (!retrySubscription.isDisposed) {
            retrySubscription.dispose()
        }
        mHubClient.dispose()
        //TODO: ("not implemented")
    }

    fun responseArrived(hubResponse: HubResponse): Boolean {
        val originRequset = mRequestMap.getHubRequest(hubResponse.tag)
        if (originRequset == null) {
            Utils.logW("Cannot find request for" + hubResponse.toHubString())
            return true
        }

        var handleResult = true

        when (originRequset.msgType) {
            MSG_TYPE.ERROR -> return false
            MSG_TYPE.request -> handleResonseInternally(originRequset, hubResponse)
        }

        //TODO: ?? do we need this. should remove request.
        if (handleResult) {
            mRequestMap.remove(hubResponse)
        }

        return handleResult


    }

    private fun handleResonseInternally(originRequset: HubRequest, hubResponse: HubResponse): Boolean {

        var handleResult = true
        when (originRequset.command) {
            HubMsgFactory.CMD_GET_WITNESSES -> handleResult = handleMyWitnesses(hubResponse)
            HubMsgFactory.CMD_GET_HISTORY -> handleResult = handleGetHistory(hubResponse)
            HubMsgFactory.CMD_GET_PARENT_FOR_NEW_TX -> handleResult = handleGetParentForNewTx(hubResponse)
        }

        return handleResult
    }

    private fun handleMyWitnesses(hubResponse: HubResponse): Boolean {
        //TODO: remove below logic
        DbHelper.saveMyWitnesses(hubResponse)
        return true
    }

    private fun handleGetHistory(hubResponse: HubResponse): Boolean {
        //TODO: remove below logic
        DbHelper.saveUnit(hubResponse)
        return true
    }

    private fun handleGetParentForNewTx(hubResponse: HubResponse): Boolean {
        val originRequset = mRequestMap.getHubRequest(hubResponse.tag)
        originRequset.hubResponse = hubResponse

        mSubject.onNext(originRequset)
        return true
    }


}