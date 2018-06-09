package org.trustnote.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.gson.JsonArray;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.trustnote.wallet.biz.TTT;

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
  @SerializedName("timestamp")
  public long creationDate;

  @ColumnInfo(
      name = "version"
  )
  public String version = TTT.version;

  @ColumnInfo(
      name = "alt"
  )
  public String alt = TTT.alt;

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
  public Long headersCommission;

  @ColumnInfo(
      name = "payload_commission"
  )
  public Long payloadCommission;

  @ColumnInfo(
      name = "is_free"
  )
  public transient int isFree;

  @ColumnInfo(
      name = "is_on_main_chain"
  )
  public transient int isOnMainChain;

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
  public transient int isStable;

  @ColumnInfo(
      name = "sequence"
  )
  public String sequence;

  @ColumnInfo(
      name = "best_parent_unit"
  )
  public String bestParentUnit;

  @Ignore
  public JsonArray parentUnits;
  @Ignore
  public String lastBall;

  @Ignore
  @Expose
  public List<Messages> messages;

  @Ignore
  @Expose
  @SerializedName("authors")
  public List<Authentifiers> authenfiers;

  @Ignore
  @Expose
  @SerializedName("earned_headers_commission_recipients")
  public List<CommissionRecipients> commissionRecipients = null;

}
