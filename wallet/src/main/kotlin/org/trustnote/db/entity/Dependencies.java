package org.trustnote.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import java.lang.String;

@Entity(
    tableName = "dependencies"
)
public class Dependencies {
  @ColumnInfo(
      name = "unit"
  )
  public String unit;

  @ColumnInfo(
      name = "depends_on_unit"
  )
  public String dependsOnUnit;

  @ColumnInfo(
      name = "creation_date"
  )
  public long creationDate;
}
