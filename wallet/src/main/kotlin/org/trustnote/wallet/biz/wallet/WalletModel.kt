package org.trustnote.wallet.biz.wallet

import android.webkit.ValueCallback
import com.google.gson.JsonObject
import io.reactivex.schedulers.Schedulers
import org.trustnote.db.DbHelper
import org.trustnote.db.entity.MyAddresses
import org.trustnote.db.entity.Units
import org.trustnote.wallet.BuildConfig
import org.trustnote.wallet.biz.TTT
import org.trustnote.wallet.biz.init.CreateWalletModel
import org.trustnote.wallet.biz.js.JSApi
import org.trustnote.wallet.biz.tx.TxParser
import org.trustnote.wallet.biz.units.UnitsManager
import org.trustnote.wallet.network.HubModel
import org.trustnote.wallet.network.pojo.*
import org.trustnote.wallet.util.AesCbc
import org.trustnote.wallet.util.MyThreadManager
import org.trustnote.wallet.util.Prefs
import org.trustnote.wallet.util.Utils
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

class WalletModel() {

    val TAG = WalletModel::class.java.simpleName

    lateinit var mProfile: TProfile

    private val refreshingCredentials = CredentialQueue()

    private lateinit var refreshingWorker: ScheduledExecutorService
    var mGetParentRequest: ReqGetParents? = null

    init {

        if (Prefs.profileExist()) {
            mProfile = Prefs.readProfile()
            WalletManager.setCurrentWalletDbTag(mProfile.dbTag)
            startRefreshThread()
            HubModel.init(mProfile.hubIndexForPairId)
        }

    }

    fun prepareForTransfer() {
        MyThreadManager.instance.runWalletModelBg {
            updateLatestGetParentRequest()
        }
    }

    private fun updateLatestGetParentRequest() {

        val witnesses = WitnessManager.getMyWitnesses()
        if (witnesses.isEmpty()) {
            return
        }

        mGetParentRequest = ReqGetParents(witnesses)
        HubModel.instance.sendHubMsg(mGetParentRequest!!)

    }

    fun updateMyTempPrivkey(pub: String, priv: String) {

        mProfile.prevTempPrivkey = mProfile.tempPrivkey
        mProfile.prevTempPubkey = mProfile.tempPubkey

        mProfile.tempPrivkey = priv
        mProfile.tempPubkey = pub

        if (mProfile.prevTempPrivkey.isEmpty()) {
            mProfile.prevTempPrivkey = mProfile.tempPrivkey
            mProfile.prevTempPubkey = mProfile.tempPubkey
        }

        Prefs.writeProfile(mProfile)

    }

    constructor(password: String, mnemonic: String, shouldRemoveMnemonic: Boolean) : this() {

        mProfile = TProfile()
        mProfile.mnemonic = mnemonic
        mProfile.removeMnemonic = shouldRemoveMnemonic

        startRefreshThread()

        fullRefreshing(password)

    }

    fun isRefreshing(): Boolean {
        Utils.debugLog("""${TAG}:::isRefreshing refreshingCredentials.size =  ${refreshingCredentials.size}""")
        return refreshingCredentials.isRefreshing()
    }

    fun destruct() {

        HubModel.instance.clear()
        refreshingWorker.shutdownNow()
        refreshingCredentials.clear()

        Prefs.removeProfile()

        DbHelper.dropWalletDB(mProfile.dbTag)
        //mWalletEventCenter.onComplete()

    }

    fun fullRefreshing(password: String) {

        Prefs.saveUserInFullRestore(true)

        Utils.debugLog("""${TAG}:::fullRefreshing with password""")
        MyThreadManager.instance.runWalletModelBg {
            fullRefreshingInBackground(password)
        }
    }

    fun refreshExistWallet() {
        Utils.debugLog("""${TAG}:::refreshExistWallet""")

        if (CreateWalletModel.getPassphraseInRam().isNotEmpty()) {
            Utils.debugLog("""${TAG}:::continue full sync""")
            fullRefreshing(CreateWalletModel.getPassphraseInRam())
            return
        }

        mProfile.credentials.forEach {
            refreshOneWallet(it)
        }

    }

    fun refreshOneWallet(walletId: String) {
        refreshOneWallet(findWallet(walletId))
    }

    private fun fullRefreshingInBackground(password: String) {

        checkAndGenPrivkey(password)

        WalletManager.getCurrentWalletDbTag()
        if (mProfile.credentials.isEmpty()) {
            newAutoWallet(password = password)
        }

        refreshAllWallet(password)
    }

    private fun refreshAllWallet(password: String) {

        createNewWalletIfLastWalletHasTransaction(password)

        mProfile.credentials.forEach {
            refreshOneWallet(it)
        }

    }

    private fun refreshOneWallet(credential: Credential) {
        if (!refreshingCredentials.contains(credential)) {
            Utils.debugLog("""${TAG}refreshOneWallet put into queue--$credential""")
            refreshingCredentials.put(credential)
        }
    }

    private fun checkAndGenPrivkey(password: String) {

        if (mProfile.xPrivKey.isEmpty()) {
            val privKey = JSApi().xPrivKeySync(mProfile.mnemonic)
            mProfile.dbTag = privKey.takeLast(5)
            WalletManager.setCurrentWalletDbTag(mProfile.dbTag)
            mProfile.deviceAddress = JSApi().deviceAddressSync(privKey)
            mProfile.pubKeyForPairId = JSApi().ecdsaPubkeySync(privKey, "m/1'")
            mProfile.privKeyForPairId = JSApi().m1PrivKeySync(privKey)
            mProfile.xPrivKey = AesCbc.encode(privKey, password)
            mProfile.hubIndexForPairId = ModelHelper.computeHubNumberForPairId(mProfile.mnemonic)

            walletUpdated()
        }
    }

    fun profileExist(): Boolean {
        return Prefs.profileExist()
    }

    private fun startRefreshThread() {
        refreshingWorker = MyThreadManager.instance.newSingleThreadExecutor(this.toString())
        refreshingWorker.execute {
            while (true) {

                Utils.debugLog("""${TAG} --- startRefreshThread::size == ${refreshingCredentials.size}""")

                val credential = refreshingCredentials.take()

                try {

                    refreshOneWalletImpl(credential)

                } catch (e: Throwable) {
                    //TODO: show err?
                    Utils.logW(e.toString())
                }

                refreshingCredentials.currentCredentialFinished()

                walletUpdated()

                maybeAddMoreWalletIfInFullRestoreProcess()

            }
        }
    }

    private fun maybeAddMoreWalletIfInFullRestoreProcess() {

        if (!Prefs.isUserInFullRestore() || refreshingCredentials.isNotEmpty()) {
            return
        }

        //TODO: regarding network err, how to make sure wallet is empty in hub.
        //Difference two case: hub empty list OR network err when get history.

        //If we already has two empty wallet, stop the process.
        val allEmptyWallets = mProfile.credentials.filter {
            it.txDetails.isEmpty()
        }

        val isLastRefreshFailed = mProfile.credentials.any { !it.isLastRefreshOk }

        if (!isLastRefreshFailed && allEmptyWallets.size >= TTT.MAX_EMPTY_WALLET_COUNT) {

            CreateWalletModel.clearPassphraseInRam()
            Prefs.saveUserInFullRestore(false)
            return
        }

        if (allEmptyWallets.size < TTT.MAX_EMPTY_WALLET_COUNT) {
            if (CreateWalletModel.getPassphraseInRam().isNotEmpty()) {

                //Buggy: cannot handle case: 1 2 (3nil) 4
                newAutoWallet(CreateWalletModel.getPassphraseInRam())
                return
            }
        }


    }

    private fun refreshOneWalletImpl(credential: Credential) {
        Utils.debugLog("""${TAG}::refreshOneWalletImpl --$credential""")
        if (credential.isRemoved) {
            return
        }

        readAddressFromDb(credential)

        checkIsAddressesIsEnoughAndGenerateMore(credential)

        //Just for better UE.
        readDataFromDb(credential)

        var needRefreshedAddresses = credential.myAddresses.toTypedArray()

        while (needRefreshedAddresses.isNotEmpty()) {

            val hubResponse = getUnitsFromHub(needRefreshedAddresses)

            //TODO: err handle.

            UnitsManager().saveUnitsFromHubResponse(hubResponse)

            readDataFromDb(credential)

            needRefreshedAddresses = checkIsAddressesIsEnoughAndGenerateMore(credential).toTypedArray()

        }

        updateIsAutoFlag()

        credential.isLastRefreshOk = true

    }

    private fun updateIsAutoFlag() {
        val reversed = mProfile.credentials.reversed()
        var foundNotEmptyWallet = false
        for (oneCredential in reversed) {
            if (foundNotEmptyWallet) {
                oneCredential.isAuto = false
                continue
            }

            if (oneCredential.txDetails.isNotEmpty()) {
                foundNotEmptyWallet = true
            }
        }
    }

    private fun checkIsAddressesIsEnoughAndGenerateMore(credential: Credential): List<MyAddresses> {
        val res = mutableListOf<MyAddresses>()

        if (credential.myReceiveAddresses.isEmpty()
                || DbHelper.shouldGenerateMoreAddress(credential.walletId, TTT.addressReceiveType)) {
            res.addAll(ModelHelper.generateNewAddressAndSaveDb(credential, TTT.addressReceiveType))
        }

        if (credential.myChangeAddresses.isEmpty()
                || DbHelper.shouldGenerateMoreAddress(credential.walletId, TTT.addressChangeType)) {
            res.addAll(ModelHelper.generateNewAddressAndSaveDb(credential, TTT.addressChangeType))
        }

        //TODO: remove
        if (res.isNotEmpty()) {
            readAddressFromDb(credential)
            walletUpdated()
        }

        return res
    }

    private fun readDataFromDb(credential: Credential) {

        DbHelper.fixIsSpentFlag()

        updateBalance(credential)

        updateTxs(credential)

        walletUpdated()

    }

    private fun lastLocalWallet(): Credential {
        return mProfile.credentials.last { !it.isObserveOnly }
    }

    private fun walletUpdated() {
        Utils.debugLog("""${TAG} --- walletUpdated""")

        if (mProfile.removeMnemonic) {
            mProfile.mnemonic = ""
        }

        Prefs.writeProfile(mProfile)

        WalletManager.mWalletEventCenter.onNext(true)

        HubModel.init(mProfile.hubIndexForPairId)

    }

    fun removeMnemonicFromProfile() {
        mProfile.removeMnemonic = true
        mProfile.mnemonic = ""
        walletUpdated()
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

    fun getUnitsFromHub(addresses: Array<MyAddresses>): HubResponse {

        val witnesses = WitnessManager.getMyWitnesses()

        if (witnesses.isEmpty() || addresses.isEmpty()) {
            return HubResponse()
        }

        val req = ReqGetHistory(witnesses, addresses)
        HubModel.instance.sendHubMsg(req)

        return req.getResponse()

    }

    private fun createNewWalletIfLastWalletHasTransaction(password: String) {
        if (mProfile.credentials.isEmpty()) {
            newAutoWallet(password)
        }

        var lastWallet = mProfile.credentials.lastOrNull { !it.isObserveOnly }

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
        val walletDisplayIndex = walletIndex + 1
        val walletIndexInName = if (walletDisplayIndex < 10) "0$walletDisplayIndex" else walletDisplayIndex.toString()
        val walletTitle = if (TTT.firstWalletName == credentialName) TTT.firstWalletName + walletIndexInName else credentialName

        val res = Credential()
        res.account = walletIndex
        res.walletId = walletId
        res.walletName = walletTitle
        res.xPubKey = walletPubKey

        if (walletIndex == 0) {
            res.isAuto = false
        } else {
            res.isAuto = isAuto
        }

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

        if (!isAuto) {

            val firstAutoEmptyWallet = mProfile.credentials.firstOrNull {
                it.isAuto && !it.isObserveOnly && it.txDetails.isEmpty()
            }

            if (firstAutoEmptyWallet != null) {
                updateWalletName(firstAutoEmptyWallet, credentialName)
                return
            }

        }

        val newCredential = createNextCredential(password, mProfile, credentialName, isAuto = isAuto)
        mProfile.credentials.add(newCredential)

        walletUpdated()

        refreshOneWallet(newCredential)

    }

    @Synchronized
    fun newManualWallet(password: String, credentialName: String) {
        newAutoWallet(password, credentialName, false)
    }

    @Synchronized
    fun newObserveWallet(walletIndex: Int, walletPubKey: String, walletTitle: String) {

        val newCredential = createObserveCredential(walletIndex, walletPubKey, walletTitle)
        mProfile.credentials.add(newCredential)

        walletUpdated()

        refreshOneWallet(newCredential)
    }

    fun findNextUnusedChangeAddress(walletId: String): MyAddresses {
        val res = DbHelper.queryUnusedChangeAddress(walletId)
        return if (res.isEmpty()) {
            val newChangeAddresses = ModelHelper.generateNewAddresses(findWallet(walletId), TTT.addressChangeType)
            DbHelper.saveWalletMyAddress(newChangeAddresses)
            newChangeAddresses[0]
        } else {
            res[0]
        }
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
        return credential.isObserveOnly || credential.balance < TTT.w_balance_limit_remove
    }

    fun removeWallet(credential: Credential): Boolean {

        if (!canRemove(credential)) {
            return false
        }

        credential.isRemoved = true
        walletUpdated()
        return true

        //TODO: remove observer wallet from DB in background.

    }

    fun udpateCredentialName(credential: Credential, newName: String) {
        credential.walletName = newName
        walletUpdated()
    }

    fun isMnemonicExist(): Boolean {
        return !mProfile.removeMnemonic
    }

    // Data sample: TTT:A1woEiM/LdDHLvTYUvlTZpsTI+82AphGZAvHalie5Nbw@shawtest.trustnote.org#xSpGdRdQTv16
    fun generateMyPairId(cb: ValueCallback<String>) {

        JSApi().randomBytes(9, ValueCallback {
            cb.onReceiveValue("""${TTT.KEY_TTT_QR_TAG}:${mProfile.pubKeyForPairId}@${HubModel.instance.mDefaultHubAddress}#$it""")
        })

    }

    fun getPrivKey(password: String): String {
        return AesCbc.decode(mProfile.xPrivKey, password)
    }

    fun updatePassword(oldPwd: String, newPwd: String) {
        val privKey = getPrivKey(oldPwd)
        mProfile.xPrivKey = AesCbc.encode(privKey, newPwd)
        Prefs.writeProfile(mProfile)
    }

    fun newUnitAcceptedByHub(unit: Units, walletId: String = "") {

        DbHelper.saveUnits(unit)

        if (walletId.isNotEmpty()) {
            readDataFromDb(findWallet(walletId))
        } else {
            mProfile.credentials.forEach {
                readDataFromDb(it)
            }
        }

        walletUpdated()

    }

    fun onMessage(hubMsg: HubJustSaying) {

        Utils.debugLog("WalletModel::onMessage")
        refreshExistWallet()

    }

    fun onNewJoint(hubMsg: HubJustSaying) {

        Utils.debugLog("WalletModel::onNewJoint")

        val unitJson = (hubMsg.bodyJson as JsonObject).get("unit") as JsonObject
        val units = UnitsManager().parseUnitFromJson(unitJson, listOf())
        newUnitAcceptedByHub(units)
    }

    fun updateWalletName(credential: Credential, newName: String) {
        val wallet = findWallet(credential.walletId)
        wallet.walletName = newName
        wallet.isAuto = false
        refreshOneWallet(credential.walletId)
        walletUpdated()
    }

    fun getDefaultWallet(): Credential? {
        return mProfile.credentials.firstOrNull{
            it.balance > 0
        }
    }

}


