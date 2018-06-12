package org.trustnote.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;

import com.google.gson.annotations.SerializedName;

import java.lang.Integer;
import java.lang.String;

@Entity(
    tableName = "balls"
)
public class Balls extends TBaseEntity {
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

  @SerializedName("is_nonserial")
  public boolean isNonserial = false;

  @Override
  public boolean equals(Object o) {
    if (o instanceof Balls) {
      return unit != null && !unit.isEmpty() && unit.equals(((Balls)o).unit);
    }
    return false;
  }
}
