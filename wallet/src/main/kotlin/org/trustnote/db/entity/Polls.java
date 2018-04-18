package org.trustnote.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import java.lang.String;

@Entity(
    tableName = "polls"
)
public class Polls {
  @ColumnInfo(
      name = "unit"
  )
  public String unit;

  @ColumnInfo(
      name = "message_index"
  )
  public int messageIndex;

  @ColumnInfo(
      name = "question"
  )
  public String question;
}
