package org.trustnote.wallet.biz.wallet

import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import org.trustnote.db.DbHelper
import org.trustnote.db.entity.MyAddresses
import org.trustnote.wallet.TTT
import org.trustnote.wallet.biz.tx.TxParser
import org.trustnote.wallet.js.JSApi
import org.trustnote.wallet.network.HubManager
import org.trustnote.wallet.network.HubMsgFactory
import org.trustnote.wallet.util.MyThreadManager
import org.trustnote.wallet.util.Prefs
import org.trustnote.wallet.util.Utils
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

class WalletModel() {

    lateinit var mProfile: TProfile

    //TODO: debounce the events.

    private val refreshingCredentials = LinkedBlockingQueue<Credential>()
    private lateinit var refreshingWorker: ScheduledExecutorService

    init {
        if (Prefs.profileExist()) {
            mProfile = Prefs.readProfile()
            startRefreshThread()
            refreshAll()
        }
    }

    constructor(mnemonic: String, shouldRemoveMnemonic: Boolean, privKey: String) : this() {
        mProfile = TProfile()
        mProfile.mnemonic = mnemonic
        mProfile.removeMnemonic = shouldRemoveMnemonic

        mProfile.xPrivKey = privKey
        mProfile.dbTag = privKey.takeLast(5)

        profileUpdated()

        restoreBg()
    }

    fun destruct() {

        HubManager.disconnect(mProfile.dbTag)
        refreshingWorker.shutdownNow()
        refreshingCredentials.clear()
        //mWalletEventCenter.onComplete()

    }

    private fun restoreBg() {
        MyThreadManager.instance.runWalletModelBg {
            restore()
        }
    }

    private fun restore() {
        checkAndGenPrivkey()
        DbHelper.dropWalletDB(mProfile.dbTag)
        if (mProfile.credentials.isEmpty()) {
            newAutoWallet()
        }
        refreshAll()
    }

    private fun refreshAll() {
        mProfile.credentials.forEach {
            refresh(it)
        }
    }

    private fun refresh(credential: Credential) {
        if (!refreshingCredentials.contains(credential)) {
            refreshingCredentials.offer(credential)
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

        readAddressFromDb(credential)

        if (credential.myAddresses.isEmpty()) {
            val newAddresses = generateNewAddress(credential)
            DbHelper.saveWalletMyAddress(newAddresses)
            readAddressFromDb(credential)
        }

        updateBalance(credential)

        updateTxs(credential)

        //TODO: notify for better UI experience.
        profileUpdated()

        updateUnitsFromHub(credential)

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
        credential.txDetails.clear()
        val txs = TxParser().getTxs(credential.walletId)

        val sortedRes = txs.sortedByDescending {
            it.ts
        }

        credential.txDetails.addAll(sortedRes)
    }

    private fun readAddressFromDb(credential: Credential) {
        val addresses = DbHelper.queryAddressByWalletId(credential.walletId)

        credential.myAddresses = addresses.toSet()

        credential.myReceiveAddresses = credential.myAddresses.filter { it.isChange == 0 }.toSet()

        credential.myChangeAddresses = credential.myAddresses.filter { it.isChange == 1 }.toSet()

    }


    fun updateUnitsFromHub(credential: Credential) {

        val witnesses = DbHelper.getMyWitnesses()
        val addresses = DbHelper.queryAddressByWalletId(credential.walletId)

        if (witnesses.isEmpty() || addresses.isEmpty()) {
            return
        }

        val req = HubMsgFactory.getHistory(HubManager.instance.getCurrentHub(), witnesses, addresses)
        HubManager.instance.getCurrentHub().mHubClient.sendHubMsg(req)

    }

//

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

//    private fun tryToReqMoreUnitsFromHub() {
//        mProfile.credentials.forEachIndexed { index, oneWallet ->
//            val isMore = DbHelper.shouldGenerateMoreAddress(oneWallet.walletId)
//            if (isMore) {
//                generateMoreAddressAndSave(oneWallet)
//            }
//        }
//
//        //TODO: java.util.NoSuchElementException: List is empty.
//        var lastWallet = mProfile.credentials.last()
//
//        if (lastWallet != null) {
//            val isMore = DbHelper.shouldGenerateNextWallet(lastWallet.walletId)
//            if (isMore) {
//                newWallet()
//            }
//        }
//
//    }

    private fun createObserveCredential(walletIndex: Int, walletPubKey: String, walletTitle: String = TTT.firstWalletName): Credential {
        val api = JSApi()
        val walletId = Utils.decodeJsStr(api.walletIDSync(walletPubKey))

        val res = Credential()
        res.account = walletIndex
        res.walletId = walletId
        res.walletName = walletTitle
        res.xPubKey = walletPubKey
        res.isLocal = true
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

    //TODO: get the next accout from DB. regarding use can remove wallet.
    private fun findNextAccount(profile: TProfile): Int {
        var max = -1
        for (one in profile.credentials) {
            if (one.account > max) {
                max = one.account
            }
        }
        return max + 1
    }

    private fun generateNewAddresses(credential: Credential, isChange: Int): List<MyAddresses> {
        val api = JSApi()
        val currentMaxAddress = DbHelper.getMaxAddressIndex(credential.walletId, isChange)
        val newAddressSize = if (currentMaxAddress == 0) TTT.walletAddressInitSize else TTT.walletAddressIncSteps
        val res = List(newAddressSize, {
            val myAddress = MyAddresses()
            myAddress.account = credential.account
            myAddress.wallet = credential.walletId

            myAddress.isChange = isChange
            myAddress.addressIndex = it + currentMaxAddress
            myAddress.address = Utils.decodeJsStr(api.walletAddressSync(credential.xPubKey, isChange, myAddress.addressIndex))
            val addressPubkey = Utils.decodeJsStr(api.walletAddressPubkeySync(credential.xPubKey, isChange, myAddress.addressIndex))
            myAddress.definition = """["sig",{"pubkey":"$addressPubkey"}]"""
            //TODO: check above logic from JS code.
            myAddress
        })

        return res

    }

    @Synchronized
    private fun newAutoWallet(credentialName: String = TTT.firstWalletName, isAuto:Boolean = true) {
        val newCredential = createNextCredential(mProfile, credentialName, isAuto = isAuto)
        mProfile.credentials.add(newCredential)

        refreshingCredentials.offer(newCredential)

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
        profileUpdated()
    }

    private fun generateNewAddress(newCredential: Credential): List<MyAddresses> {
        val receiveAddresses = generateNewAddresses(newCredential, TTT.addressReceiveType)
        val changeAddresses = generateNewAddresses(newCredential, TTT.addressReceiveType)

        val res = mutableListOf<MyAddresses>()
        res.addAll(receiveAddresses)
        res.addAll(changeAddresses)
        return res.toList()
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


