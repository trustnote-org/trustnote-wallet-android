package org.trustnote.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import java.lang.String;

@Entity(
    tableName = "bots"
)
public class Bots {
  @ColumnInfo(
      name = "id"
  )
  @PrimaryKey(
      autoGenerate = true
  )
  public int id;

  @ColumnInfo(
      name = "rank"
  )
  public int rank;

  @ColumnInfo(
      name = "name"
  )
  public String name;

  @ColumnInfo(
      name = "pairing_code"
  )
  public String pairingCode;

  @ColumnInfo(
      name = "description"
  )
  public String description;
}
