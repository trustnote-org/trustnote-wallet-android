package org.trustnote.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.lang.String;

@Entity(
    tableName = "definitions"
)
public class Definitions extends TBaseEntity {
  @PrimaryKey
  @NonNull
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
