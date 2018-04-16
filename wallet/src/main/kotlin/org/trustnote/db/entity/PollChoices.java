package org.trustnote.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import java.lang.String;

@Entity(
    tableName = "`poll_choices`"
)
public class PollChoices {
  @ColumnInfo(
      name = "`unit`"
  )
  public String unit;

  @ColumnInfo(
      name = "`choice_index`"
  )
  public int choiceIndex;

  @ColumnInfo(
      name = "`choice`"
  )
  public String choice;
}
