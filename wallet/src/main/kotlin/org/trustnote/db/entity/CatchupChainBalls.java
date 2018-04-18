package org.trustnote.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import java.lang.String;

@Entity(
    tableName = "catchup_chain_balls"
)
public class CatchupChainBalls extends TBaseEntity {
  @ColumnInfo(
      name = "member_index"
  )
  @PrimaryKey(
      autoGenerate = true
  )
  public int memberIndex;

  @ColumnInfo(
      name = "ball"
  )
  public String ball;
}
