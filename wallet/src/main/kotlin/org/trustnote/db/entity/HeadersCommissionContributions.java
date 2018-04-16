package org.trustnote.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import java.lang.String;

@Entity(
    tableName = "`headers_commission_contributions`"
)
public class HeadersCommissionContributions {
  @ColumnInfo(
      name = "`unit`"
  )
  public String unit;

  @ColumnInfo(
      name = "`address`"
  )
  public String address;

  @ColumnInfo(
      name = "`amount`"
  )
  public int amount;

  @ColumnInfo(
      name = "`creation_date`"
  )
  public long creationDate;
}
