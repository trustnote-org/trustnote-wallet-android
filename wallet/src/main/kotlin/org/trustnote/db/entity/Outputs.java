package org.trustnote.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import java.lang.Integer;
import java.lang.String;

@Entity(
    tableName = "`outputs`"
)
public class Outputs {
  @ColumnInfo(
      name = "`output_id`"
  )
  @PrimaryKey(
      autoGenerate = true
  )
  public int outputId;

  @ColumnInfo(
      name = "`unit`"
  )
  public String unit;

  @ColumnInfo(
      name = "`message_index`"
  )
  public int messageIndex;

  @ColumnInfo(
      name = "`output_index`"
  )
  public int outputIndex;

  @ColumnInfo(
      name = "`asset`"
  )
  public String asset;

  @ColumnInfo(
      name = "`denomination`"
  )
  public int denomination;

  @ColumnInfo(
      name = "`address`"
  )
  public String address;

  @ColumnInfo(
      name = "`amount`"
  )
  public int amount;

  @ColumnInfo(
      name = "`blinding`"
  )
  public String blinding;

  @ColumnInfo(
      name = "`output_hash`"
  )
  public String outputHash;

  @ColumnInfo(
      name = "`is_serial`"
  )
  public Integer isSerial;

  @ColumnInfo(
      name = "`is_spent`"
  )
  public int isSpent;
}
