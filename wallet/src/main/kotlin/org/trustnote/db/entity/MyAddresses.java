package org.trustnote.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.lang.String;

@Entity(
    tableName = "my_addresses"
)
public class MyAddresses extends TBaseEntity {
  @ColumnInfo(
      name = "address"
  )
  @PrimaryKey @NonNull
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

  @Override
  public boolean equals(@NonNull Object o) {
    if (o == null || !(o instanceof MyAddresses)) {
      return false;
    } else {
      return address.equals(((MyAddresses)o).address);
    }
  }

}
