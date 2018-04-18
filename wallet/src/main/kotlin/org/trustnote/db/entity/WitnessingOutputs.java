package org.trustnote.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import java.lang.String;

@Entity(
    tableName = "witnessing_outputs"
)
public class WitnessingOutputs extends TBaseEntity {
  @ColumnInfo(
      name = "main_chain_index"
  )
  public int mainChainIndex;

  @ColumnInfo(
      name = "address"
  )
  public String address;

  @ColumnInfo(
      name = "amount"
  )
  public int amount;

  @ColumnInfo(
      name = "is_spent"
  )
  public int isSpent;

  @ColumnInfo(
      name = "creation_date"
  )
  public long creationDate;
}
