package org.trustnote.wallet.biz.wallet

import android.webkit.ValueCallback
import com.google.gson.JsonObject
import io.reactivex.schedulers.Schedulers
import org.trustnote.db.DbHelper
import org.trustnote.db.entity.MyAddresses
import org.trustnote.db.entity.Units
import org.trustnote.wallet.biz.TTT
import org.trustnote.wallet.biz.init.CreateWalletModel
import org.trustnote.wallet.biz.js.JSApi
import org.trustnote.wallet.biz.tx.TxParser
import org.trustnote.wallet.biz.units.UnitsManager
import org.trustnote.wallet.network.HubModel
import org.trustnote.wallet.network.pojo.HubJustSaying
import org.trustnote.wallet.network.pojo.HubResponse
import org.trustnote.wallet.network.pojo.ReqGetHistory
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

    init {

        if (Prefs.profileExist()) {
            mProfile = Prefs.readProfile()
            WalletManager.setCurrentWalletDbTag(mProfile.dbTag)
            startRefreshThread()
            HubModel.init(mProfile.hubIndexForPairId)
        }

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

        walletUpdated()

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
        mProfile.credentials.forEach {
            refreshOneWallet(it)
        }
        walletUpdated()
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

                walletUpdated()

                maybeAddMoreWalletIfInFullRestoreProcess()

            }
        }
    }

    private fun maybeAddMoreWalletIfInFullRestoreProcess() {

        if (!Prefs.isUserInFullRestore() || refreshingCredentials.isNotEmpty()) {
            return
        }

        if (CreateWalletModel.getPassphraseInRam().isNotEmpty()) {

            newAutoWallet(CreateWalletModel.getPassphraseInRam())

            //TODO: bug, it only restore one more wallet.
            CreateWalletModel.clearPassphraseInRam()
            Prefs.saveUserInFullRestore(false)

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

        walletUpdated()
    }

    @Synchronized
    fun newManualWallet(password: String, credentialName: String) {
        newAutoWallet(password, credentialName, false)
    }

    @Synchronized
    fun newObserveWallet(walletIndex: Int, walletPubKey: String, walletTitle: String) {
        val newCredential = createObserveCredential(walletIndex, walletPubKey, walletTitle)
        mProfile.credentials.add(newCredential)

        refreshOneWallet(newCredential)
        walletUpdated()
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
        return credential.isObserveOnly || credential.balance == 0L
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

}


