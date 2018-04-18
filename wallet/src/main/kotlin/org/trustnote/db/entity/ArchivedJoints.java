package org.trustnote.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import java.lang.String;

@Entity(
    tableName = "archived_joints"
)
public class ArchivedJoints extends TBaseEntity {
  @ColumnInfo(
      name = "unit"
  )
  public String unit;

  @ColumnInfo(
      name = "reason"
  )
  public String reason;

  @ColumnInfo(
      name = "json"
  )
  public String json;

  @ColumnInfo(
      name = "creation_date"
  )
  public long creationDate;
}
