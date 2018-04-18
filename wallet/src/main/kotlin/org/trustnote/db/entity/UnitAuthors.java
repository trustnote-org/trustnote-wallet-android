package org.trustnote.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import java.lang.String;

@Entity(
    tableName = "unit_authors"
)
public class UnitAuthors {
  @ColumnInfo(
      name = "unit"
  )
  public String unit;

  @ColumnInfo(
      name = "address"
  )
  public String address;

  @ColumnInfo(
      name = "definition_chash"
  )
  public String definitionChash;
}
