package org.trustnote.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import org.trustnote.db.dao.UnitsDao
import org.trustnote.db.entity.*
import org.trustnote.superwallet.biz.wallet.WalletManager
import org.trustnote.superwallet.util.Utils

@Database(entities = arrayOf(
        MyWitnesses::class,
        MyAddresses::class,
        Units::class,
        Messages::class,
        Inputs::class,
        Outputs::class,
        Definitions::class,
        Authentifiers::class,
        CorrespondentDevices::class,
        ChatMessages::class,
        Outbox::class
), version = 1)
abstract class TrustNoteDataBase : RoomDatabase() {

    abstract fun unitsDao(): UnitsDao

    companion object {
        val TAG = TrustNoteDataBase::class.java.simpleName

        private var dbMap = mutableMapOf<String, TrustNoteDataBase>()

        fun getInstance(context: Context): TrustNoteDataBase {
            val dbSuffix = WalletManager.getCurrentWalletDbTag()
            synchronized(TrustNoteDataBase::class) {
                if (!dbMap.containsKey(dbSuffix)) {

                    Utils.debugLog("${TrustNoteDataBase.TAG}databaseBuilder::create db::$dbSuffix")
                    val db = Room.databaseBuilder(context.getApplicationContext(),
                            TrustNoteDataBase::class.java, "trustnote_$dbSuffix.db")
                            .allowMainThreadQueries()
                            .build()

                    dbMap.put(dbSuffix, db)

                    Utils.debugLog("${TrustNoteDataBase.TAG}databaseBuilder::create db successful::$dbSuffix")

                }
            }
            return dbMap[dbSuffix]!!
        }

        fun destroyInstance() {
            dbMap.clear()
        }

        fun removeDb(dbTag: String) {
            dbMap.remove(dbTag)
        }
    }
}