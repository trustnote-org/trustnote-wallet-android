package org.trustnote.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import java.lang.String;

@Entity(
    tableName = "`device_messages`"
)
public class DeviceMessages {
  @ColumnInfo(
      name = "`message_hash`"
  )
  public String messageHash;

  @ColumnInfo(
      name = "`device_address`"
  )
  public String deviceAddress;

  @ColumnInfo(
      name = "`message`"
  )
  public String message;

  @ColumnInfo(
      name = "`creation_date`"
  )
  public long creationDate;
}
