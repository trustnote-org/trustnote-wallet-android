package org.trustnote.db

import android.arch.persistence.room.Query
import io.reactivex.Observable
import org.trustnote.db.dao.UnitsDao
import org.trustnote.db.entity.*
import org.trustnote.wallet.TApp
import org.trustnote.wallet.util.Utils
import java.io.File

@Suppress("UNCHECKED_CAST")
object DbHelper {

    fun saveUnits(units: Array<Units>) {
        getDao().saveUnits(units)
    }

    fun unitsStabled(unitIds: List<String>) {

        val idArrOfArr = unitIds.withIndex().groupBy {
            it.index / 900
        }.map { it.value.map { it.value } }

        for(ids in idArrOfArr) {
            getDao().unitsStabled(ids.toTypedArray())
        }

    }

    fun saveUnits(units: Units) {
        getDao().saveUnits(arrayOf(units))
    }

    fun saveCorrespondentDevice(correspondentDevice: CorrespondentDevices) {
        getDao().insertCorrespondentDevice(correspondentDevice)
    }

    fun isCorrespondentDeviceExist(pubkey: String): Boolean {
        val res = getDao().findCorrespondentDevices(pubkey)
        return res.isNotEmpty()
    }

    fun saveWalletMyAddress(listAddress: List<MyAddresses>) = saveWalletMyAddressInternal(listAddress)
    fun saveMyWitnesses(myWitnesses: List<String>) {
        getDao().saveMyWitnesses(myWitnesses.mapToTypedArray {
            val res = MyWitnesses()
            res.address = it
            res
        })

    }

    fun hasDefinitions(address: String): Boolean = getDao().findDefinitions(address) > 0

    fun getMyWitnesses(): Array<MyWitnesses> = getMyWitnessesInternal()
    fun getAllWalletAddress(walletId: String): Array<MyAddresses> = getAllWalletAddressInternal(walletId)
    fun monitorAddresses(): Observable<Array<MyAddresses>> = monitorAddressesInternal()
    fun monitorUnits(): Observable<Array<Units>> = monitorUnitsInternal()
    fun monitorOutputs(): Observable<Array<Outputs>> = monitorOutputsInternal()

    fun shouldGenerateMoreAddress(walletId: String, isChange: Int): Boolean = shouldGenerateMoreAddressInternal(walletId, isChange)
    fun getMaxAddressIndex(walletId: String, isChange: Int): Int = getMaxAddressIndexInternal(walletId, isChange)
    fun shouldGenerateNextWallet(walletId: String): Boolean = shouldGenerateNextWalletInternal(walletId)

    //Balance and tx history
    fun getBanlance(walletId: String): List<Balance> = getBanlanceInternal(walletId)

    fun fixIsSpentFlag() = getDao().fixIsSpentFlag()

    fun getTxs(walletId: String): List<TxUnits> = getTxsInternal(walletId)

    fun queryAddress(addressList: List<String>) = queryAddressInternal(addressList)

    fun queryAddressByAddresdId(addressId: String) = queryAddressByAddresdIdInternal(addressId)

    fun queryAddressByWalletId(walletId: String) = queryAddressByWalletIdInternal(walletId)
    fun queryOutputAddress(unitId: String, walletId: String): List<TxOutputs> {
        return getDao().queryOutputAddress(unitId, walletId).asList()
    }

    fun queryInputAddresses(unitId: String): Array<String> {
        return getDao().queryInputAddresses(unitId)
    }

    fun queryFundedAddressesByAmount(walletId: String, amount: Long): Array<FundedAddress> {
        return getDao().queryFundedAddressesByAmount(walletId, amount)
    }

    fun queryUtxoByAddress(addressList: List<String>, lastBallMCI: Int): Array<Outputs> {
        return getDao().queryUtxoByAddress(addressList, lastBallMCI)
    }

    fun queryUnusedChangeAddress(walletid: String): Array<MyAddresses> {
        return getDao().queryUnusedChangeAddress(walletid)
    }

    fun dropWalletDB(keyDb: String) {

        Utils.debugLog("${TrustNoteDataBase.TAG}dropWalletDB::$keyDb")

        val path = TApp.context.getDatabasePath("trustnote_$keyDb.db").path
        val dbFile = File(path)
        if (dbFile.exists()) {

            val targetFile = File(TApp.context.getDatabasePath("${Utils.nowTimeAsFileName()}_trustnote_$keyDb.db").path)

            Utils.debugLog("${TrustNoteDataBase.TAG}dropWalletDB::targetFile${targetFile.toString()}")

            dbFile.copyTo(targetFile, true)
            dbFile.delete()
            TrustNoteDataBase.removeDb(keyDb)
        }
    }

    fun saveChatMessages(chatMessages: ChatMessages) {
        getDao().insertChatMessages(chatMessages)
        getDao().updateLastMessage(chatMessages.correspondentAddress, chatMessages.message, chatMessages.creationDate)
        updateUnreadMessageCounter(chatMessages.correspondentAddress)
    }

    fun updateUnreadMessageCounter(correspondentAddress: String) {
        getDao().updateUnreadMessageCounter(correspondentAddress)
    }

    fun queryCorrespondetnDevices(): List<CorrespondentDevices>{
        return getDao().queryCorrespondetnDevices().asList()
    }

    fun queryChatMessages(correspondentAddress: String): List<ChatMessages> {
        return getDao().queryChatMessages(correspondentAddress).asList()
    }

    fun readAllMessages(correspondentAddress: String) {
        getDao().readAllMessages(correspondentAddress)
        updateUnreadMessageCounter(correspondentAddress)
    }

    fun findCorrespondentDevice(correspondentAddress: String): CorrespondentDevices {
        val res = getDao().findCorrespondentDevices(correspondentAddress)
        return res[0]
    }

    fun clearChatHistory(correspondentAddresses: String) {
        getDao().clearChatHistory(correspondentAddresses)
    }

    fun removeCorrespondentDevice(correspondentDevice: CorrespondentDevices) {
        getDao().removeCorrespondentDevice(correspondentDevice)
        getDao().clearChatHistory(correspondentDevice.deviceAddress)
    }

}

fun getDao(): UnitsDao {
    return TrustNoteDataBase.getInstance(TApp.context).unitsDao()
}

fun getBanlanceInternal(walletId: String): List<Balance> {
    return getDao().queryBalance(walletId).toList()
}

fun getMaxAddressIndexInternal(walletId: String, change: Int): Int {
    val max = getDao().getMaxAddressIndex(walletId, change)
    return if (max > 0) max + 1 else max
}

fun shouldGenerateNextWalletInternal(walletId: String): Boolean {
    return getDao().shouldGenerateNextWallet(walletId)
}

fun shouldGenerateMoreAddressInternal(walletId: String, isChange: Int): Boolean {
    return getDao().shouldGenerateMoreAddress(walletId, isChange)
}

fun getAllWalletAddressInternal(walletId: String): Array<MyAddresses> {
    return getDao().queryAllWalletAddress(walletId)
}

fun getMyWitnessesInternal(): Array<MyWitnesses> {
    return getDao().queryMyWitnesses()
}

fun saveWalletMyAddressInternal(listAddress: List<MyAddresses>) {
    getDao().insertMyAddresses(listAddress.toTypedArray())
}

fun monitorAddressesInternal(): Observable<Array<MyAddresses>> {
    return Utils.throttleDbEvent(getDao().monitorAddresses().toObservable(), 3L)
}

fun monitorUnitsInternal(): Observable<Array<Units>> {
    return Utils.throttleDbEvent(getDao().monitorUnits().toObservable(), 3L)
}

fun monitorOutputsInternal(): Observable<Array<Outputs>> {
    return Utils.throttleDbEvent(getDao().monitorOutputs().toObservable(), 3L)
}

inline fun <T, reified R> List<T>.mapToTypedArray(transform: (T) -> R): Array<R> {
    return when (this) {
        is RandomAccess -> Array(size) { index -> transform(this[index]) }
        else -> with(iterator()) { Array(size) { transform(next()) } }
    }
}

fun queryAddressInternal(addressList: List<String>): Array<MyAddresses> {
    return getDao().queryAddress(addressList)
}

fun queryAddressByWalletIdInternal(walletId: String): Array<MyAddresses> {
    return getDao().queryAddressByWalletId(walletId)
}

fun queryAddressByAddresdIdInternal(addressId: String): MyAddresses {

    //How about query with no result.
    val res = getDao().queryAddress(listOf<String>(addressId))
    return res[0]
}

private fun getTxsInternal(walletId: String): List<TxUnits> {
    return getDao().queryTxUnits(walletId).asList()
}
