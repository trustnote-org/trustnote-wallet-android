package org.trustnote.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import java.lang.Integer;
import java.lang.String;

@Entity(
    tableName = "asset_denominations"
)
public class AssetDenominations extends TBaseEntity {
  @ColumnInfo(
      name = "asset"
  )
  public String asset;

  @ColumnInfo(
      name = "denomination"
  )
  public int denomination;

  @ColumnInfo(
      name = "count_coins"
  )
  public Integer countCoins;

  @ColumnInfo(
      name = "max_issued_serial_number"
  )
  public int maxIssuedSerialNumber;
}
