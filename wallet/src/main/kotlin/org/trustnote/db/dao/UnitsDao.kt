package org.trustnote.db.dao

import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Transaction
import org.trustnote.db.entity.Inputs
import org.trustnote.db.entity.Messages
import org.trustnote.db.entity.Outputs
import org.trustnote.db.entity.Units


@Dao
abstract class UnitsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertUnits(units: Array<Units>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertInputs(inputs: Array<Inputs>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertOutputs(outputs: Array<Outputs>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertMessages(outputs: Array<Messages>)

    @Transaction
    open fun saveUnits(units: Array<Units>) {
        insertUnits(units)
        for(oneUnit in units) {
            insertMessages(oneUnit.messages.toTypedArray())
            for(oneMessage in oneUnit.messages) {
                insertInputs(oneMessage.inputs.toTypedArray())
                insertOutputs(oneMessage.outputs.toTypedArray())
            }
        }
    }

}