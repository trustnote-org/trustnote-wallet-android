package org.trustnote.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(
    tableName = "transfer_addresses"
)
public class TransferAddresses extends TBaseEntity {
  @ColumnInfo(
      name = "address"
  )
  @PrimaryKey @NonNull
  public String address;

  @ColumnInfo(
          name = "name"
  )
  public String name;


  @Override
  public boolean equals(@NonNull Object o) {
    if (o == null || !(o instanceof TransferAddresses)) {
      return false;
    } else {
      return address.equals(((TransferAddresses)o).address);
    }
  }



}
