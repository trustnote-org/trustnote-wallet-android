package org.trustnote.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import java.lang.String;

@Entity(
    tableName = "shared_address_signing_paths"
)
public class SharedAddressSigningPaths extends TBaseEntity {
  @ColumnInfo(
      name = "shared_address"
  )
  public String sharedAddress;

  @ColumnInfo(
      name = "signing_path"
  )
  public String signingPath;

  @ColumnInfo(
      name = "address"
  )
  public String address;

  @ColumnInfo(
      name = "member_signing_path"
  )
  public String memberSigningPath;

  @ColumnInfo(
      name = "device_address"
  )
  public String deviceAddress;

  @ColumnInfo(
      name = "creation_date"
  )
  public long creationDate;
}
