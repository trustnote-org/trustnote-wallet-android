package org.trustnote.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import java.lang.String;

@Entity(
    tableName = "earned_headers_commission_recipients"
)
public class EarnedHeadersCommissionRecipients extends TBaseEntity {
  @ColumnInfo(
      name = "unit"
  )
  public String unit;

  @ColumnInfo(
      name = "address"
  )
  public String address;

  @ColumnInfo(
      name = "earned_headers_commission_share"
  )
  public int earnedHeadersCommissionShare;
}
