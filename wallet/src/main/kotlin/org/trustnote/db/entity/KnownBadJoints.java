package org.trustnote.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import java.lang.String;

@Entity(
    tableName = "`known_bad_joints`"
)
public class KnownBadJoints {
  @ColumnInfo(
      name = "`joint`"
  )
  public String joint;

  @ColumnInfo(
      name = "`unit`"
  )
  public String unit;

  @ColumnInfo(
      name = "`json`"
  )
  public String json;

  @ColumnInfo(
      name = "`error`"
  )
  public String error;

  @ColumnInfo(
      name = "`creation_date`"
  )
  public long creationDate;
}
