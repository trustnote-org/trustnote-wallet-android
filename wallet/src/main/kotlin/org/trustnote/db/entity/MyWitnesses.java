package org.trustnote.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.lang.String;

@Entity(
    tableName = "my_witnesses"
)
public class MyWitnesses extends TBaseEntity {
  @ColumnInfo(
      name = "address"
  )
  @PrimaryKey @NonNull
  public String address;
}
