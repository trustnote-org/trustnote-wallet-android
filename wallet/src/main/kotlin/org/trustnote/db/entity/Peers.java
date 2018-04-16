package org.trustnote.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import java.lang.String;

@Entity(
    tableName = "`peers`"
)
public class Peers {
  @ColumnInfo(
      name = "`peer`"
  )
  public String peer;

  @ColumnInfo(
      name = "`peer_host`"
  )
  public String peerHost;

  @ColumnInfo(
      name = "`learnt_from_peer_host`"
  )
  public String learntFromPeerHost;

  @ColumnInfo(
      name = "`is_self`"
  )
  public int isSelf;

  @ColumnInfo(
      name = "`creation_date`"
  )
  public long creationDate;
}
