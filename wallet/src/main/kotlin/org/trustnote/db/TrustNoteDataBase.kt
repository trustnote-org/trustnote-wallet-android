package org.trustnote.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import org.trustnote.db.dao.UnitsDao
import org.trustnote.db.entity.*

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
        var INSTANCE: TrustNoteDataBase? = null

        fun getInstance(context: Context): TrustNoteDataBase {
            if (INSTANCE == null) {
                synchronized(TrustNoteDataBase::class) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            TrustNoteDataBase::class.java, "trustnote.db")
                            .allowMainThreadQueries()
                            .build()
                }
            }
            return INSTANCE!!
        }

        fun destroyInstance() {
            INSTANCE = null
        }
    }
}