package org.trustnote.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import java.lang.String;

@Entity(
    tableName = "`parenthoods`"
)
public class Parenthoods {
  @ColumnInfo(
      name = "`child_unit`"
  )
  public String childUnit;

  @ColumnInfo(
      name = "`parent_unit`"
  )
  public String parentUnit;
}
