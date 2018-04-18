package org.trustnote.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import java.lang.String;

@Entity(
    tableName = "peer_hosts"
)
public class PeerHosts extends TBaseEntity {
  @ColumnInfo(
      name = "peer_host"
  )
  public String peerHost;

  @ColumnInfo(
      name = "creation_date"
  )
  public long creationDate;

  @ColumnInfo(
      name = "count_new_good_joints"
  )
  public int countNewGoodJoints;

  @ColumnInfo(
      name = "count_invalid_joints"
  )
  public int countInvalidJoints;

  @ColumnInfo(
      name = "count_nonserial_joints"
  )
  public int countNonserialJoints;
}
