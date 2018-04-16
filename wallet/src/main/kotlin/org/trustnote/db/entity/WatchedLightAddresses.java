package org.trustnote.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import java.lang.String;

@Entity(
    tableName = "`watched_light_addresses`"
)
public class WatchedLightAddresses {
  @ColumnInfo(
      name = "`peer`"
  )
  public String peer;

  @ColumnInfo(
      name = "`address`"
  )
  public String address;

  @ColumnInfo(
      name = "`creation_date`"
  )
  public long creationDate;
}
