package org.trustnote.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.lang.Integer;
import java.lang.String;

@Entity(tableName = "inputs", primaryKeys = {"unit", "message_index", "input_index"})
public class Inputs extends TBaseEntity {
  @ColumnInfo(
      name = "unit"
  )
  @NonNull
  @SerializedName("owner_unit")
  public transient String unit;

  @ColumnInfo(
      name = "message_index"
  )
  @NonNull
  @SerializedName("msg_non_dup_index")
  public transient int messageIndex;

  @ColumnInfo(
      name = "input_index"
  )
  @NonNull
  public transient int inputIndex;

  @ColumnInfo(
      name = "asset"
  )
  public transient String asset;

  @ColumnInfo(
      name = "denomination"
  )
  public transient int denomination;

  @ColumnInfo(
      name = "is_unique"
  )
  public transient Integer isUnique;

  @ColumnInfo(
      name = "type"
  )
  public transient String type;

  @ColumnInfo(
      name = "src_unit"
  )
  @SerializedName("unit")
  public String srcUnit;

  @ColumnInfo(
      name = "src_message_index"
  )
  @SerializedName("message_index")
  public Integer srcMessageIndex;

  @ColumnInfo(
      name = "src_output_index"
  )
  @SerializedName("output_index")
  public Integer srcOutputIndex;

  @ColumnInfo(
      name = "from_main_chain_index"
  )
  public transient Integer fromMainChainIndex;

  @ColumnInfo(
      name = "to_main_chain_index"
  )
  public transient Integer toMainChainIndex;

  @ColumnInfo(
      name = "serial_number"
  )
  public transient Integer serialNumber;

  @ColumnInfo(
      name = "amount"
  )
  public transient Long amount;

  @ColumnInfo(
      name = "address"
  )
  public transient String address;

}
