package org.trustnote.wallet.biz.wallet

import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import org.trustnote.db.DbHelper
import org.trustnote.db.entity.MyAddresses
import org.trustnote.wallet.biz.TTT
import org.trustnote.wallet.biz.init.CreateWalletModel
import org.trustnote.wallet.biz.tx.TxParser
import org.trustnote.wallet.biz.units.UnitsManager
import org.trustnote.wallet.biz.js.JSApi
import org.trustnote.wallet.network.HubManager
import org.trustnote.wallet.network.pojo.HubResponse
import org.trustnote.wallet.network.pojo.ReqGetHistory
import org.trustnote.wallet.util.AesCbc
import org.trustnote.wallet.util.MyThreadManager
import org.trustnote.wallet.util.Prefs
import org.trustnote.wallet.util.Utils
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

class WalletModel() {

    //TODO： DbHelper.dropWalletDB(mProfile.dbTag)

    lateinit var mProfile: TProfile
    var busy = false
    private val refreshingCredentials = LinkedBlockingQueue<Credential>()
    private lateinit var refreshingWorker: ScheduledExecutorService

    init {
        if (Prefs.profileExist()) {
            mProfile = Prefs.readProfile()
            startRefreshThread()
        }
    }

    constructor(password: String, mnemonic: String, shouldRemoveMnemonic: Boolean, privKey: String) : this() {
        mProfile = TProfile()
        mProfile.mnemonic = mnemonic
        mProfile.removeMnemonic = shouldRemoveMnemonic

        mProfile.xPrivKey = privKey
        mProfile.dbTag = privKey.takeLast(5)

        startRefreshThread()

        fullRefreshing(password)
    }

    fun isRefreshing(): Boolean {
        return refreshingCredentials.isNotEmpty() || busy
    }

    fun destruct() {

        HubManager.disconnect(mProfile.dbTag)
        refreshingWorker.shutdownNow()
        refreshingCredentials.clear()
        //mWalletEventCenter.onComplete()

    }

    fun fullRefreshing(password: String) {
        MyThreadManager.instance.runWalletModelBg {
            fullRefreshingInBackground(password)
        }
    }

    fun refreshExistWallet() {

        mProfile.credentials.forEach {
            refreshOneWallet(it)
        }
    }


    fun refreshOneWallet(walletId: String) {
        refreshOneWallet(findWallet(walletId))
    }

    private fun fullRefreshingInBackground(password: String) {

        checkAndGenPrivkey(password)

        WalletManager.setCurrentWalletDbTag(mProfile.dbTag)

        WalletManager.getCurrentWalletDbTag()
        if (mProfile.credentials.isEmpty()) {
            newAutoWallet(password = password)
        }

        refreshAllWallet(password)
    }

    private fun refreshAllWallet(password: String) {

        createNewWalletIfLastWalletHasTransaction(password)

        val ws = getAvaiableWalletsForUser()

        ws.forEach {
            refreshOneWallet(it)
        }

    }

    private fun refreshOneWallet(credential: Credential) {
        if (!refreshingCredentials.contains(credential)) {
            Utils.debugLog("""refreshOneWallet put into queue--$credential""")
            refreshingCredentials.put(credential)
        }
    }

    private fun checkAndGenPrivkey(password: String) {
        if (mProfile.xPrivKey.isEmpty()) {
            val privKey = JSApi().xPrivKeySync(mProfile.mnemonic)
            mProfile.dbTag = privKey.takeLast(5)
            mProfile.deviceAddress = JSApi().deviceAddressSync(privKey)
            mProfile.pubKeyForPairId = JSApi().ecdsaPubkeySync(privKey, "m/1'")
            mProfile.xPrivKey = AesCbc.encode(privKey, password)
        }
    }

    fun profileExist(): Boolean {
        return Prefs.profileExist()
    }

    private fun startRefreshThread() {
        refreshingWorker = MyThreadManager.instance.newSingleThreadExecutor(this.toString())
        refreshingWorker.execute {
            while (true) {

                val credential = refreshingCredentials.take()
                busy = true
                refreshOneWalletImpl(credential)
                busy = false
            }
        }
    }

    private fun refreshOneWalletImpl(credential: Credential) {
        Utils.debugLog("""refreshOneWalletImpl--$credential""")
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


        readDataFromDb(credential)
        //notify for better UI experience.
        profileUpdated()

        val hubResponse = getUnitsFromHub(credential)
        val res = UnitsManager().saveUnitsFromHubResponse(hubResponse)
        if (res.isNotEmpty() && credential == lastLocalWallet()) {

            readDataFromDb(credential)
            createNewWalletIfLastWalletHasTransaction(CreateWalletModel.passphraseInRam)

        }

        profileUpdated()

    }

    private fun readDataFromDb(credential: Credential) {

        DbHelper.fixIsSpentFlag()

        updateBalance(credential)

        updateTxs(credential)

    }

    private fun lastLocalWallet(): Credential {
        return mProfile.credentials.last { !it.isObserveOnly }
    }

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

        credential.myAddresses = addresses.toList()

        credential.myReceiveAddresses = credential.myAddresses.filter { it.isChange == 0 }.toList()

        credential.myChangeAddresses = credential.myAddresses.filter { it.isChange == 1 }.toList()

    }

    fun getUnitsFromHub(credential: Credential): HubResponse {

        val witnesses = WitnessManager.getMyWitnesses()
        val addresses = DbHelper.queryAddressByWalletId(credential.walletId)

        if (witnesses.isEmpty() || addresses.isEmpty()) {
            return HubResponse()
        }

        val hubModel = HubManager.instance.getCurrentHub()
        val reqId = hubModel.getRandomTag()
        val req = ReqGetHistory(reqId, witnesses, addresses)
        hubModel.mHubClient.sendHubMsg(req)

        return req.getResponse()

    }

    private fun createNewWalletIfLastWalletHasTransaction(password: String) {
        if (mProfile.credentials.isEmpty()) {
            newAutoWallet(password)
        }

        var lastWallet = mProfile.credentials.last { !it.isObserveOnly }

        if (lastWallet != null && DbHelper.shouldGenerateNextWallet(lastWallet.walletId)) {
            newAutoWallet(password)
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

    private fun createNextCredential(password: String, profile: TProfile, credentialName: String = TTT.firstWalletName, isAuto: Boolean = true): Credential {
        val api = JSApi()
        val walletIndex = findNextAccount(profile)
        val privKey = getPrivKey(password)
        val walletPubKey = api.walletPubKeySync(privKey, walletIndex)
        val walletId = api.walletIDSync(walletPubKey)
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
    private fun newAutoWallet(password: String, credentialName: String = TTT.firstWalletName, isAuto: Boolean = true) {
        val newCredential = createNextCredential(password, mProfile, credentialName, isAuto = isAuto)
        mProfile.credentials.add(newCredential)

        refreshOneWallet(newCredential)

        profileUpdated()
    }

    @Synchronized
    fun newManualWallet(password: String, credentialName: String = TTT.firstWalletName) {
        newAutoWallet(password, credentialName, false)
    }

    @Synchronized
    fun newObserveWallet(walletIndex: Int, walletPubKey: String, walletTitle: String) {
        val newCredential = createObserveCredential(walletIndex, walletPubKey, walletTitle)
        mProfile.credentials.add(newCredential)

        refreshOneWallet(newCredential)
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

    fun findWallet(walletId: String): Credential {
        return mProfile.credentials.find { it.walletId == walletId }!!
    }

    fun receiveAddress(walletId: String): String {
        return receiveAddress(findWallet(walletId))
    }

    fun receiveAddress(credential: Credential): String {
        return credential.myReceiveAddresses[0].address
    }

    private fun isAvaiableToUser(it: Credential): Boolean {
        return (!it.isRemoved) && (
                (it.account == 0 && !it.isObserveOnly)
                        || !it.isAuto
                        || it.balance > 0
                        || it.isObserveOnly
                )
    }

    fun getAvaiableWalletsForUser(): List<Credential> {

        return mProfile.credentials.filter { isAvaiableToUser(it) }

    }

    fun canRemove(credential: Credential): Boolean {
        return credential.isObserveOnly || credential.balance == 0L
    }

    fun removeWallet(credential: Credential): Boolean {

        if (!canRemove(credential)) {
            return false
        }

        credential.isRemoved = true
        profileUpdated()
        return true

        //TODO: remove observer wallet from DB in background.

    }

    fun udpateCredentialName(credential: Credential, newName: String) {
        credential.walletName = newName
        profileUpdated()
    }

    fun isMnemonicExist(): Boolean {
        return !mProfile.removeMnemonic
    }

    //TODO: move to msg module.
    // Data sample: TTT:A1woEiM/LdDHLvTYUvlTZpsTI+82AphGZAvHalie5Nbw@shawtest.trustnote.org#xSpGdRdQTv16
    fun generateMyPairId(): String {

        val randomString = JSApi().randomBytesSync(9)
        return """${TTT.KEY_TTT_QR_TAG}:${mProfile.pubKeyForPairId}@${TTT.hubAddress}#$randomString"""

    }

    fun getPrivKey(password: String): String {
        return AesCbc.decode(mProfile.xPrivKey, password)
    }

    fun updatePassword(oldPwd: String, newPwd: String) {
        val privKey = getPrivKey(oldPwd)
        mProfile.xPrivKey = AesCbc.encode(privKey, newPwd)
        Prefs.writeProfile(mProfile)
    }

}


