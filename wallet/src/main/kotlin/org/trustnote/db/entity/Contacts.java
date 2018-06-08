package org.trustnote.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(
    tableName = "my_addresses"
)
public class Contacts extends TBaseEntity {
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
    if (o == null || !(o instanceof Contacts)) {
      return false;
    } else {
      return address.equals(((Contacts)o).address);
    }
  }

}
