package org.trustnote.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import java.lang.String;

@Entity(
    tableName = "authentifiers"
)
public class Authentifiers {
  @ColumnInfo(
      name = "unit"
  )
  public String unit;

  @ColumnInfo(
      name = "address"
  )
  public String address;

  @ColumnInfo(
      name = "path"
  )
  public String path;

  @ColumnInfo(
      name = "authentifier"
  )
  public String authentifier;
}
