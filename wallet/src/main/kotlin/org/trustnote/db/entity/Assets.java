package org.trustnote.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import java.lang.Integer;
import java.lang.String;

@Entity(
    tableName = "assets"
)
public class Assets extends TBaseEntity {
  @ColumnInfo(
      name = "unit"
  )
  public String unit;

  @ColumnInfo(
      name = "message_index"
  )
  public int messageIndex;

  @ColumnInfo(
      name = "cap"
  )
  public Integer cap;

  @ColumnInfo(
      name = "is_private"
  )
  public int isPrivate;

  @ColumnInfo(
      name = "is_transferrable"
  )
  public int isTransferrable;

  @ColumnInfo(
      name = "auto_destroy"
  )
  public int autoDestroy;

  @ColumnInfo(
      name = "fixed_denominations"
  )
  public int fixedDenominations;

  @ColumnInfo(
      name = "issued_by_definer_only"
  )
  public int issuedByDefinerOnly;

  @ColumnInfo(
      name = "cosigned_by_definer"
  )
  public int cosignedByDefiner;

  @ColumnInfo(
      name = "spender_attested"
  )
  public int spenderAttested;

  @ColumnInfo(
      name = "issue_condition"
  )
  public String issueCondition;

  @ColumnInfo(
      name = "transfer_condition"
  )
  public String transferCondition;
}
