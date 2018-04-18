package org.trustnote.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import java.lang.String;

@Entity(
    tableName = "peer_events"
)
public class PeerEvents extends TBaseEntity {
  @ColumnInfo(
      name = "peer_host"
  )
  public String peerHost;

  @ColumnInfo(
      name = "event_date"
  )
  public long eventDate;

  @ColumnInfo(
      name = "event"
  )
  public String event;
}
