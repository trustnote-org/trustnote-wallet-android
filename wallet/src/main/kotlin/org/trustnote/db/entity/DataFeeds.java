package org.trustnote.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import java.lang.Integer;
import java.lang.String;

@Entity(
    tableName = "data_feeds"
)
public class DataFeeds extends TBaseEntity {
  @ColumnInfo(
      name = "unit"
  )
  public String unit;

  @ColumnInfo(
      name = "message_index"
  )
  public int messageIndex;

  @ColumnInfo(
      name = "feed_name"
  )
  public String feedName;

  @ColumnInfo(
      name = "value"
  )
  public String value;

  @ColumnInfo(
      name = "int_value"
  )
  public Integer intValue;
}
