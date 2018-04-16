package org.trustnote.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import java.lang.String;

@Entity(
    tableName = "`messages`"
)
public class Messages {
  @ColumnInfo(
      name = "`unit`"
  )
  public String unit;

  @ColumnInfo(
      name = "`message_index`"
  )
  public int messageIndex;

  @ColumnInfo(
      name = "`app`"
  )
  public String app;

  @ColumnInfo(
      name = "`payload_location`"
  )
  public String payloadLocation;

  @ColumnInfo(
      name = "`payload_hash`"
  )
  public String payloadHash;

  @ColumnInfo(
      name = "`payload`"
  )
  public String payload;

  @ColumnInfo(
      name = "`payload_uri_hash`"
  )
  public String payloadUriHash;

  @ColumnInfo(
      name = "`payload_uri`"
  )
  public String payloadUri;
}
