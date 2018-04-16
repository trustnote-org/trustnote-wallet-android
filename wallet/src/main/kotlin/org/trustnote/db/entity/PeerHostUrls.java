package org.trustnote.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import java.lang.Integer;
import java.lang.Long;
import java.lang.String;

@Entity(
    tableName = "`peer_host_urls`"
)
public class PeerHostUrls {
  @ColumnInfo(
      name = "`peer_host`"
  )
  public String peerHost;

  @ColumnInfo(
      name = "`url`"
  )
  public String url;

  @ColumnInfo(
      name = "`creation_date`"
  )
  public long creationDate;

  @ColumnInfo(
      name = "`is_active`"
  )
  public Integer isActive;

  @ColumnInfo(
      name = "`revocation_date`"
  )
  public Long revocationDate;
}
