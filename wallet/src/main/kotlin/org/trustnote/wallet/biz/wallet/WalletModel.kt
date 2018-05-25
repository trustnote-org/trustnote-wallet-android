package org.trustnote.wallet.biz.wallet

import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import org.trustnote.db.DbHelper
import org.trustnote.db.entity.MyAddresses
import org.trustnote.wallet.TTT
import org.trustnote.wallet.biz.tx.TxParser
import org.trustnote.wallet.biz.units.UnitsManager
import org.trustnote.wallet.js.JSApi
import org.trustnote.wallet.network.HubManager
import org.trustnote.wallet.network.pojo.HubResponse
import org.trustnote.wallet.network.pojo.ReqGetHistory
import org.trustnote.wallet.util.MyThreadManager
import org.trustnote.wallet.util.Prefs
import org.trustnote.wallet.util.Utils
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

class WalletModel() {

    //TODO： DbHelper.dropWalletDB(mProfile.dbTag)

    lateinit var mProfile: TProfile

    //TODO: debounce the events.

    private val refreshingCredentials = LinkedBlockingQueue<Credential>()
    private lateinit var refreshingWorker: ScheduledExecutorService

    init {
        if (Prefs.profileExist()) {
            mProfile = Prefs.readProfile()
            startRefreshThread()
        }
    }

    constructor(mnemonic: String, shouldRemoveMnemonic: Boolean, privKey: String) : this() {
        mProfile = TProfile()
        mProfile.mnemonic = mnemonic
        mProfile.removeMnemonic = shouldRemoveMnemonic

        mProfile.xPrivKey = privKey
        mProfile.dbTag = privKey.takeLast(5)

        startRefreshThread()

        restoreBg()
    }

    fun isRefreshing(): Boolean {
        return refreshingCredentials.isNotEmpty()
    }

    fun destruct() {

        HubManager.disconnect(mProfile.dbTag)
        refreshingWorker.shutdownNow()
        refreshingCredentials.clear()
        //mWalletEventCenter.onComplete()

    }

    fun restoreBg() {
        MyThreadManager.instance.runWalletModelBg {
            restore()
        }
    }

    private fun restore() {

        checkAndGenPrivkey()

        WalletManager.setCurrentWalletDbTag(mProfile.dbTag)

        WalletManager.getCurrentWalletDbTag()
        if (mProfile.credentials.isEmpty()) {
            newAutoWallet()
        }

        refreshAll()
    }

    private fun refreshAll() {

        createNewWalletIfLastWalletHasTransaction()

        mProfile.credentials.forEach {
            refresh(it)
        }

    }

    private fun refresh(credential: Credential) {
        if (!refreshingCredentials.contains(credential)) {
            refreshingCredentials.put(credential)
        }
    }

    private fun checkAndGenPrivkey() {
        if (mProfile.xPrivKey.isEmpty()) {
            mProfile.xPrivKey = JSApi().xPrivKeySync(mProfile.mnemonic)
            mProfile.dbTag = mProfile.xPrivKey.takeLast(5)
        }
    }

    fun profileExist(): Boolean {
        return Prefs.profileExist()
    }

    private fun startRefreshThread() {
        refreshingWorker = MyThreadManager.instance.newSingleThreadExecutor(this.toString())
        refreshingWorker.execute {
            while (true) {
                refreshInternal(refreshingCredentials.take())
            }
        }
    }

    private fun refreshInternal(credential: Credential) {
        if (credential.isRemoved) {
            return
        }

        if (DbHelper.shouldGenerateMoreAddress(credential.walletId)) {
            ModelHelper.generateNewAddressAndSaveDb(credential)
        }

        readAddressFromDb(credential)

        if (credential.myAddresses.isEmpty()) {
            ModelHelper.generateNewAddressAndSaveDb(credential)
        }

        DbHelper.fixIsSpentFlag()

        updateBalance(credential)

        updateTxs(credential)

        //TODO: notify for better UI experience.
        val hubResponse = getUnitsFromHub(credential)
        val res = UnitsManager().saveUnits(hubResponse)
        if (res.isNotEmpty()) {
            createNewWalletIfLastWalletHasTransaction()
        }

        profileUpdated()

    }

//        //TODO: think more.
//        HubManager.instance.reConnectHub()
//
//        monitorWallet()

    private fun profileUpdated() {

        if (mProfile.removeMnemonic) {
            mProfile.mnemonic = ""
        }
        Prefs.writeProfile(mProfile)

        WalletManager.mWalletEventCenter.onNext(true)
    }

    fun removeMnemonicFromProfile() {
        mProfile.removeMnemonic = true
        mProfile.mnemonic = ""
        profileUpdated()
    }

    private fun updateBalance(credential: Credential) {
        val balanceDetails = DbHelper.getBanlance(credential.walletId)
        credential.balanceDetails = balanceDetails
        credential.balance = 0
        credential.balanceDetails.forEach {
            credential.balance += it.amount
        }

        updateTotalBalance()
    }

    private fun updateTotalBalance() {
        mProfile.balance = 0
        mProfile.credentials.forEach {
            mProfile.balance += it.balance
        }
    }

    private fun updateTxs(credential: Credential) {
        val txs = TxParser().getTxs(credential.walletId)

        val sortedRes = txs.sortedByDescending {
            it.ts
        }

        credential.txDetails = (sortedRes)
    }

    private fun readAddressFromDb(credential: Credential) {
        val addresses = DbHelper.queryAddressByWalletId(credential.walletId)

        credential.myAddresses = addresses.toSet()

        credential.myReceiveAddresses = credential.myAddresses.filter { it.isChange == 0 }.toSet()

        credential.myChangeAddresses = credential.myAddresses.filter { it.isChange == 1 }.toSet()

    }

    fun getUnitsFromHub(credential: Credential): HubResponse {

        val witnesses = WitnessManager.getMyWitnesses()
        val addresses = DbHelper.queryAddressByWalletId(credential.walletId)

        if (witnesses.isEmpty() || addresses.isEmpty()) {
            return HubResponse()
        }

        val reqId = HubManager.instance.getCurrentHub().getRandomTag()
        val req = ReqGetHistory(reqId, witnesses, addresses)
        HubManager.instance.getCurrentHub().mHubClient.sendHubMsg(req)

        return req.getResponse()

    }

    private fun createNewWalletIfLastWalletHasTransaction() {
        if (mProfile.credentials.isEmpty()) {
            newAutoWallet()
        }

        var lastWallet = mProfile.credentials.last { !it.isObserveOnly }

        if (lastWallet != null && DbHelper.shouldGenerateNextWallet(lastWallet.walletId)) {
            newAutoWallet()
        }
    }

    fun monitorWallet() {
        DbHelper.monitorAddresses().debounce(3, TimeUnit.SECONDS).subscribeOn(Schedulers.io()).subscribe {
            Utils.debugLog("from monitorAddresses")
            //hubRequestCurrentWalletTxHistory()
        }

        DbHelper.monitorUnits().debounce(3, TimeUnit.SECONDS).subscribeOn(Schedulers.io()).subscribe {
            Utils.debugLog("from monitorUnits")
            //TODO: tryToReqMoreUnitsFromHub()
            DbHelper.fixIsSpentFlag()
        }

        DbHelper.monitorOutputs().delay(3L, TimeUnit.SECONDS).subscribeOn(Schedulers.io()).subscribe {
            Utils.debugLog("from monitorOutputs")
            if (mProfile != null) {
                //loadDataFromDbBg()
            }
        }
    }

    private fun createObserveCredential(walletIndex: Int, walletPubKey: String, walletTitle: String = TTT.firstWalletName): Credential {
        val api = JSApi()
        val walletId = Utils.decodeJsStr(api.walletIDSync(walletPubKey))

        val res = Credential()
        res.account = walletIndex
        res.walletId = walletId
        res.walletName = walletTitle
        res.xPubKey = walletPubKey
        res.isObserveOnly = true
        return res
    }

    private fun createNextCredential(profile: TProfile, credentialName: String = TTT.firstWalletName, isAuto: Boolean = true): Credential {
        val api = JSApi()
        val walletIndex = findNextAccount(profile)
        val walletPubKey = api.walletPubKeySync(profile.xPrivKey, walletIndex)
        val walletId = Utils.decodeJsStr(api.walletIDSync(walletPubKey))
        val walletTitle = if (TTT.firstWalletName == credentialName) TTT.firstWalletName + ":" + walletIndex else credentialName

        val res = Credential()
        res.account = walletIndex
        res.walletId = walletId
        res.walletName = walletTitle
        res.xPubKey = walletPubKey
        res.isAuto = isAuto
        return res
    }

    private fun findNextAccount(profile: TProfile): Int {
        var max = -1
        for (one in profile.credentials) {
            if (!one.isObserveOnly && one.account > max) {
                max = one.account
            }
        }
        return max + 1
    }

    @Synchronized
    private fun newAutoWallet(credentialName: String = TTT.firstWalletName, isAuto: Boolean = true) {
        val newCredential = createNextCredential(mProfile, credentialName, isAuto = isAuto)
        mProfile.credentials.add(newCredential)

        refresh(newCredential)

        profileUpdated()
    }

    @Synchronized
    fun newManualWallet(credentialName: String = TTT.firstWalletName) {
        newAutoWallet(credentialName, false)
    }

    @Synchronized
    fun newObserveWallet(walletIndex: Int, walletPubKey: String, walletTitle: String) {
        val newCredential = createObserveCredential(walletIndex, walletPubKey, walletTitle)
        mProfile.credentials.add(newCredential)
        refreshingCredentials.offer(newCredential)
        refresh(newCredential)
        profileUpdated()
    }

    fun findNextUnusedChangeAddress(walletId: String): MyAddresses {
        var res = MyAddresses()
        DbHelper.queryUnusedChangeAddress(walletId).subscribe(
                Consumer {
                    res = it
                }, Consumer {

        })
        return res
    }

}


