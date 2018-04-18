package org.trustnote.db.dao

import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Transaction
import org.trustnote.db.entity.Inputs
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

    @Transaction
    open fun saveUnits(units: Array<Units>, inputs: Array<Inputs>, outputs: Array<Outputs>) {
        insertUnits(units)
        insertInputs(inputs)
        insertOutputs(outputs)
    }

}