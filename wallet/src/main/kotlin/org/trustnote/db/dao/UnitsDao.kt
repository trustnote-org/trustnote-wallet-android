package org.trustnote.db.dao

import android.arch.persistence.room.*
import io.reactivex.Flowable
import org.trustnote.db.entity.*
import org.trustnote.wallet.TTT


@Dao
abstract class UnitsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertUnits(units: Array<Units>)
    @Query("SELECT * FROM units")
    abstract fun queryUnits(): Array<Units>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertInputs(inputs: Array<Inputs>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertOutputs(inputs: Array<Outputs>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertMessages(outputs: Array<Messages>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertAuthentifiers(outputs: Array<Authentifiers>)

    @Query("select unit from inputs where inputs.address in (select my_addresses.address from my_addresses where my_addresses.wallet == :walletId order by my_addresses.address_index desc limit :dataLimit)\n" +
            "    union\n" +
            "    select unit from outputs where outputs.address in (select my_addresses.address from my_addresses where my_addresses.wallet == :walletId order by my_addresses.address_index desc limit :dataLimit)")
    abstract fun queryUnitForLatestWalletAddress(walletId: String, dataLimit:Int = TTT.walletAddressInitSize * 2): Array<String>

    @Query("SELECT * FROM units")
    abstract fun monitorUnits(): Flowable<Array<Units>>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertMyAddresses(myAddresses: Array<MyAddresses>)

    @Query("SELECT * FROM my_addresses WHERE wallet == :walletId")
    abstract fun queryAllWalletAddress(walletId: String): Array<MyAddresses>

    @Query("SELECT max(address_index) FROM my_addresses WHERE wallet == :walletId and is_change = :isChange ")
    abstract fun getMaxAddressIndex(walletId: String, isChange: Int): Int

    @Query("SELECT * FROM my_addresses")
    abstract fun queryAllWalletAddress(): Array<MyAddresses>

    @Query("SELECT * FROM my_addresses")
    abstract fun monitorAddresses(): Flowable<Array<MyAddresses>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertMyWitnesses(myWitnesses: Array<MyWitnesses>)

    @Delete
    abstract fun deleteMyWitnesses(myWitnesses: Array<MyWitnesses>)

    @Query("SELECT * FROM my_witnesses")
    abstract fun queryMyWitnesses(): Array<MyWitnesses>


    @Transaction
    open fun saveMyWitnesses(myWitnesses: Array<MyWitnesses>) {
        val oldWitnesses = queryMyWitnesses()
        deleteMyWitnesses(oldWitnesses)
        insertMyWitnesses(myWitnesses)
    }

    @Transaction
    open fun saveUnits(units: Array<Units>) {
        insertUnits(units)
        for (oneUnit in units) {
            insertAuthentifiers(oneUnit.authenfiers.toTypedArray())
            insertMessages(oneUnit.messages.toTypedArray())
            for (oneMessage in oneUnit.messages) {
                insertInputs(oneMessage.inputs.toTypedArray())
                insertOutputs(oneMessage.outputs.toTypedArray())
            }
        }
    }

    @Transaction
    open fun getUnitxByWalletId(units: Array<Units>) {
        val units = queryUnits()
        for (oneUnit in units) {

            insertMessages(oneUnit.messages.toTypedArray())
            for (oneMessage in oneUnit.messages) {
                insertInputs(oneMessage.inputs.toTypedArray())
                insertOutputs(oneMessage.outputs.toTypedArray())
            }
        }
    }

    open fun shouldGenerateMoreAddress(walletId: String): Boolean {
        val res = queryUnitForLatestWalletAddress(walletId)
        return res.isNotEmpty()
    }

    open fun shouldGenerateNextWallet(walletId: String): Boolean {
        val res = queryUnitForLatestWalletAddress(walletId, Int.MAX_VALUE)
        return res.isNotEmpty()
    }



}