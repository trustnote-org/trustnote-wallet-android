package org.trustnote.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import java.lang.String;

@Entity(
    tableName = "`outbox`"
)
public class Outbox {
  @ColumnInfo(
      name = "`message_hash`"
  )
  public String messageHash;

  @ColumnInfo(
      name = "`to`"
  )
  public String to;

  @ColumnInfo(
      name = "`message`"
  )
  public String message;

  @ColumnInfo(
      name = "`creation_date`"
  )
  public long creationDate;

  @ColumnInfo(
      name = "`last_error`"
  )
  public String lastError;
}
