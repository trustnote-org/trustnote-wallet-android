package org.trustnote.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import java.lang.Integer;
import java.lang.String;

@Entity(
    tableName = "balls"
)
public class Balls {
  @ColumnInfo(
      name = "ball"
  )
  public String ball;

  @ColumnInfo(
      name = "creation_date"
  )
  public long creationDate;

  @ColumnInfo(
      name = "unit"
  )
  public String unit;

  @ColumnInfo(
      name = "count_paid_witnesses"
  )
  public Integer countPaidWitnesses;
}
