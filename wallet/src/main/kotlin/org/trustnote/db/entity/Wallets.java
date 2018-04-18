package org.trustnote.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import java.lang.Long;
import java.lang.String;

@Entity(
    tableName = "wallets"
)
public class Wallets {
  @ColumnInfo(
      name = "wallet"
  )
  public String wallet;

  @ColumnInfo(
      name = "account"
  )
  public int account;

  @ColumnInfo(
      name = "definition_template"
  )
  public String definitionTemplate;

  @ColumnInfo(
      name = "creation_date"
  )
  public long creationDate;

  @ColumnInfo(
      name = "full_approval_date"
  )
  public Long fullApprovalDate;

  @ColumnInfo(
      name = "ready_date"
  )
  public Long readyDate;
}
