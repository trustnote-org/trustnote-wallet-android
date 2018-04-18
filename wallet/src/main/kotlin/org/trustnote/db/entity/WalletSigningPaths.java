package org.trustnote.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import java.lang.String;

@Entity(
    tableName = "wallet_signing_paths"
)
public class WalletSigningPaths {
  @ColumnInfo(
      name = "wallet"
  )
  public String wallet;

  @ColumnInfo(
      name = "signing_path"
  )
  public String signingPath;

  @ColumnInfo(
      name = "device_address"
  )
  public String deviceAddress;

  @ColumnInfo(
      name = "creation_date"
  )
  public long creationDate;
}
