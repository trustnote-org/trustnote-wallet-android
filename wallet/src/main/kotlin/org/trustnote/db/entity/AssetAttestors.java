package org.trustnote.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import java.lang.String;

@Entity(
    tableName = "`asset_attestors`"
)
public class AssetAttestors {
  @ColumnInfo(
      name = "`unit`"
  )
  public String unit;

  @ColumnInfo(
      name = "`message_index`"
  )
  public int messageIndex;

  @ColumnInfo(
      name = "`asset`"
  )
  public String asset;

  @ColumnInfo(
      name = "`attestor_address`"
  )
  public String attestorAddress;
}
