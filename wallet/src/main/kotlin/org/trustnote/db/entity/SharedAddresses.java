package org.trustnote.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import java.lang.String;

@Entity(
    tableName = "shared_addresses"
)
public class SharedAddresses extends TBaseEntity {
  @ColumnInfo(
      name = "shared_address"
  )
  public String sharedAddress;

  @ColumnInfo(
      name = "definition"
  )
  public String definition;

  @ColumnInfo(
      name = "creation_date"
  )
  public long creationDate;
}
