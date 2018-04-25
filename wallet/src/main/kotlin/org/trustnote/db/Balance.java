package org.trustnote.db;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.support.annotation.NonNull;

import org.trustnote.db.entity.TBaseEntity;

public class Balance {

    @ColumnInfo(
            name = "asset"
    )
    public String asset;

    @ColumnInfo(
            name = "address"
    )
    public String address;

    @ColumnInfo(
            name = "amount"
    )
    public int amount;

}
