package org.trustnote.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import java.lang.String;

@Entity(
    tableName = "skiplist_units"
)
public class SkiplistUnits extends TBaseEntity {
  @ColumnInfo(
      name = "unit"
  )
  public String unit;

  @ColumnInfo(
      name = "skiplist_unit"
  )
  public String skiplistUnit;
}
