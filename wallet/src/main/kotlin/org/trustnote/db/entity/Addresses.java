package org.trustnote.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import java.lang.String;

@Entity(
    tableName = "addresses"
)
public class Addresses extends TBaseEntity {
  @ColumnInfo(
      name = "address"
  )
  public String address;

  @ColumnInfo(
      name = "creation_date"
  )
  public long creationDate;
}
