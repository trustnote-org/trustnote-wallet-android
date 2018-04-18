package org.trustnote.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import java.lang.String;

@Entity(
    tableName = "watched_light_units"
)
public class WatchedLightUnits extends TBaseEntity {
  @ColumnInfo(
      name = "peer"
  )
  public String peer;

  @ColumnInfo(
      name = "unit"
  )
  public String unit;

  @ColumnInfo(
      name = "creation_date"
  )
  public long creationDate;
}
