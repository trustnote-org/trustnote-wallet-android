package org.trustnote.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import org.trustnote.db.dao.UnitsDao
import org.trustnote.db.entity.*
import org.trustnote.wallet.biz.wallet.WalletModel

@Database(entities = arrayOf(
        MyWitnesses::class,
        MyAddresses::class,
        Units::class,
        Messages::class,
        Inputs::class,
        Outputs::class,
        Authentifiers::class
), version = 1)
abstract class TrustNoteDataBase : RoomDatabase() {

    abstract fun unitsDao(): UnitsDao

    companion object {
        private var dbMap = mutableMapOf<String, TrustNoteDataBase>()

        fun getInstance(context: Context): TrustNoteDataBase {
            val dbSuffix = WalletModel.instance.getMnemonicAsHash()
            synchronized(TrustNoteDataBase::class) {
                if (!dbMap.containsKey(dbSuffix)) {
                    val db = Room.databaseBuilder(context.getApplicationContext(),
                            TrustNoteDataBase::class.java, "trustnote_$dbSuffix.db")
                            .allowMainThreadQueries()
                            .build()
                    dbMap.put(dbSuffix, db)
                }
            }
            return dbMap[dbSuffix]!!
        }

        fun destroyInstance() {
            dbMap.clear()
        }
    }
}