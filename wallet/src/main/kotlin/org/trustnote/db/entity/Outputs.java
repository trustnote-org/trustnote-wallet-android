package org.trustnote.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.lang.Integer;
import java.lang.String;

@Entity(
        tableName = "outputs",
        primaryKeys = {"unit", "message_index", "output_index"}
)
public class Outputs extends TBaseEntity {

    @ColumnInfo(
            name = "unit"
    )
    @NonNull
    public transient String unit;

    @ColumnInfo(
            name = "message_index"
    )
    @NonNull
    public transient int messageIndex;

    @ColumnInfo(
            name = "output_index"
    )
    @NonNull
    public transient int outputIndex;

    @ColumnInfo(
            name = "asset"
    )
    public transient String asset;

    @ColumnInfo(
            name = "denomination"
    )
    public transient int denomination;

    @ColumnInfo(
            name = "address"
    )
    public String address;

    @ColumnInfo(
            name = "amount"
    )
    public long amount;

    @ColumnInfo(
            name = "blinding"
    )
    public transient String blinding;

    @ColumnInfo(
            name = "output_hash"
    )
    public transient String outputHash;

    @ColumnInfo(
            name = "is_serial"
    )
    public transient Integer isSerial;

    @ColumnInfo(
            name = "is_spent"
    )
    public transient int isSpent;
}
