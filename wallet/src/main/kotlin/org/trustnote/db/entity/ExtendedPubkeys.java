package org.trustnote.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import java.lang.Long;
import java.lang.String;

@Entity(
    tableName = "extended_pubkeys"
)
public class ExtendedPubkeys extends TBaseEntity {
  @ColumnInfo(
      name = "wallet"
  )
  public String wallet;

  @ColumnInfo(
      name = "extended_pubkey"
  )
  public String extendedPubkey;

  @ColumnInfo(
      name = "device_address"
  )
  public String deviceAddress;

  @ColumnInfo(
      name = "creation_date"
  )
  public long creationDate;

  @ColumnInfo(
      name = "approval_date"
  )
  public Long approvalDate;

  @ColumnInfo(
      name = "member_ready_date"
  )
  public Long memberReadyDate;
}
