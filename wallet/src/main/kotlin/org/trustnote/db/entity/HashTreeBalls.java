package org.trustnote.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import java.lang.String;

@Entity(
    tableName = "`hash_tree_balls`"
)
public class HashTreeBalls {
  @ColumnInfo(
      name = "`ball_index`"
  )
  @PrimaryKey(
      autoGenerate = true
  )
  public int ballIndex;

  @ColumnInfo(
      name = "`ball`"
  )
  public String ball;

  @ColumnInfo(
      name = "`unit`"
  )
  public String unit;
}
