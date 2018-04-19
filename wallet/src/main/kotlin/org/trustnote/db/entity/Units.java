package org.trustnote.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.gson.annotations.Expose;

import java.lang.Integer;
import java.lang.String;
import java.util.List;

@Entity(
    tableName = "units"
)
public class Units extends TBaseEntity {
  @ColumnInfo(
      name = "unit"
  )
  @PrimaryKey
  @NonNull
  public String unit;

  @ColumnInfo(
      name = "creation_date"
  )
  public long creationDate;

  @ColumnInfo(
      name = "version"
  )
  public String version;

  @ColumnInfo(
      name = "alt"
  )
  public String alt;

  @ColumnInfo(
      name = "witness_list_unit"
  )
  public String witnessListUnit;

  @ColumnInfo(
      name = "last_ball_unit"
  )
  public String lastBallUnit;

  @ColumnInfo(
      name = "content_hash"
  )
  public String contentHash;

  @ColumnInfo(
      name = "headers_commission"
  )
  public int headersCommission;

  @ColumnInfo(
      name = "payload_commission"
  )
  public int payloadCommission;

  @ColumnInfo(
      name = "is_free"
  )
  public int isFree;

  @ColumnInfo(
      name = "is_on_main_chain"
  )
  public int isOnMainChain;

  @ColumnInfo(
      name = "main_chain_index"
  )
  public Integer mainChainIndex;

  @ColumnInfo(
      name = "latest_included_mc_index"
  )
  public Integer latestIncludedMcIndex;

  @ColumnInfo(
      name = "level"
  )
  public Integer level;

  @ColumnInfo(
      name = "witnessed_level"
  )
  public Integer witnessedLevel;

  @ColumnInfo(
      name = "is_stable"
  )
  public int isStable;

  @ColumnInfo(
      name = "sequence"
  )
  public String sequence;

  @ColumnInfo(
      name = "best_parent_unit"
  )
  public String bestParentUnit;

  @Ignore
  @Expose
  public transient List<Messages> messages;

}
