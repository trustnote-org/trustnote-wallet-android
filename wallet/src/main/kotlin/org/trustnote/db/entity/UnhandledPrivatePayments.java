package org.trustnote.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import java.lang.String;

@Entity(
    tableName = "unhandled_private_payments"
)
public class UnhandledPrivatePayments {
  @ColumnInfo(
      name = "unit"
  )
  public String unit;

  @ColumnInfo(
      name = "message_index"
  )
  public int messageIndex;

  @ColumnInfo(
      name = "output_index"
  )
  public int outputIndex;

  @ColumnInfo(
      name = "json"
  )
  public String json;

  @ColumnInfo(
      name = "peer"
  )
  public String peer;

  @ColumnInfo(
      name = "linked"
  )
  public int linked;

  @ColumnInfo(
      name = "creation_date"
  )
  public long creationDate;
}
