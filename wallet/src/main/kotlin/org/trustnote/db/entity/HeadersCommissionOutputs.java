package org.trustnote.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import java.lang.String;

@Entity(
    tableName = "`headers_commission_outputs`"
)
public class HeadersCommissionOutputs {
  @ColumnInfo(
      name = "`main_chain_index`"
  )
  public int mainChainIndex;

  @ColumnInfo(
      name = "`address`"
  )
  public String address;

  @ColumnInfo(
      name = "`amount`"
  )
  public int amount;

  @ColumnInfo(
      name = "`is_spent`"
  )
  public int isSpent;

  @ColumnInfo(
      name = "`creation_date`"
  )
  public long creationDate;
}
