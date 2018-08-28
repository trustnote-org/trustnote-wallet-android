//package org.trustnote.org.trustnote.superwallet.db
//
//import android.arch.persistence.room.Room
//import org.junit.After
//import org.junit.Before
//import org.trustnote.db.TrustNoteDataBase
//
//public class DbTest {
//
//    lateinit var mDatabase : TrustNoteDataBase
//    @Before
//    fun initDb() {
//        mDatabase = Room.inMemoryDatabaseBuilder(
//                InstrumentationRegistry.getContext(),
//                TrustNoteDataBase::class)
//                // allowing main thread queries, just for testing
//                .allowMainThreadQueries()
//                .build()
//    }
//
//    @After
//    fun closeDb() {
//        mDatabase.close();
//    }
//
//
//}