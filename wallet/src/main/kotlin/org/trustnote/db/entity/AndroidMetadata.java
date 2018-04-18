package org.trustnote.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import java.lang.String;

@Entity(
    tableName = "android_metadata"
)
public class AndroidMetadata {
  @ColumnInfo(
      name = "locale"
  )
  public String locale;
}
