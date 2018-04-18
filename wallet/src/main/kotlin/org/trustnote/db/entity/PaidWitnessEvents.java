package org.trustnote.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import java.lang.Integer;
import java.lang.String;

@Entity(
    tableName = "paid_witness_events"
)
public class PaidWitnessEvents extends TBaseEntity {
  @ColumnInfo(
      name = "unit"
  )
  public String unit;

  @ColumnInfo(
      name = "address"
  )
  public String address;

  @ColumnInfo(
      name = "delay"
  )
  public Integer delay;
}
