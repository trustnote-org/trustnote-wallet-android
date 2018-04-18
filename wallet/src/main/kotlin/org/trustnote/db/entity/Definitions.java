package org.trustnote.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import java.lang.String;

@Entity(
    tableName = "definitions"
)
public class Definitions extends TBaseEntity {
  @ColumnInfo(
      name = "definition_chash"
  )
  public String definitionChash;

  @ColumnInfo(
      name = "definition"
  )
  public String definition;

  @ColumnInfo(
      name = "has_references"
  )
  public int hasReferences;
}
