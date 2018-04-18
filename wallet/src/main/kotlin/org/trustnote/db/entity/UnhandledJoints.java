package org.trustnote.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import java.lang.String;

@Entity(
    tableName = "unhandled_joints"
)
public class UnhandledJoints {
  @ColumnInfo(
      name = "unit"
  )
  public String unit;

  @ColumnInfo(
      name = "peer"
  )
  public String peer;

  @ColumnInfo(
      name = "json"
  )
  public String json;

  @ColumnInfo(
      name = "creation_date"
  )
  public long creationDate;
}
