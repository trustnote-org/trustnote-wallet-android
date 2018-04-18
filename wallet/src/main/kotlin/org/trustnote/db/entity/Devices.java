package org.trustnote.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import java.lang.String;

@Entity(
    tableName = "devices"
)
public class Devices extends TBaseEntity {
  @ColumnInfo(
      name = "device_address"
  )
  public String deviceAddress;

  @ColumnInfo(
      name = "pubkey"
  )
  public String pubkey;

  @ColumnInfo(
      name = "temp_pubkey_package"
  )
  public String tempPubkeyPackage;

  @ColumnInfo(
      name = "creation_date"
  )
  public long creationDate;
}
