package org.trustnote.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import java.lang.String;

@Entity(
    tableName = "my_addresses"
)
public class MyAddresses {
  @ColumnInfo(
      name = "address"
  )
  public String address;

  @ColumnInfo(
      name = "wallet"
  )
  public String wallet;

  @ColumnInfo(
      name = "is_change"
  )
  public int isChange;

  @ColumnInfo(
      name = "address_index"
  )
  public int addressIndex;

  @ColumnInfo(
      name = "definition"
  )
  public String definition;

  @ColumnInfo(
      name = "creation_date"
  )
  public long creationDate;
}
