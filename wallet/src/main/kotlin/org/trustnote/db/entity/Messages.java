package org.trustnote.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.support.annotation.NonNull;

import java.lang.String;
import java.util.List;

@Entity(
    tableName = "messages",
    primaryKeys = {"unit", "message_index"}
)
public class Messages extends TBaseEntity {
  @ColumnInfo(
      name = "unit"
  )
  @NonNull
  public String unit;

  @ColumnInfo(
      name = "message_index"
  )
  @NonNull
  public int messageIndex;

  @ColumnInfo(
      name = "app"
  )
  public String app;

  @ColumnInfo(
      name = "payload_location"
  )
  public String payloadLocation;

  @ColumnInfo(
      name = "payload_hash"
  )
  public String payloadHash;

  @ColumnInfo(
      name = "payload"
  )
  public transient String payload;

  @ColumnInfo(
      name = "payload_uri_hash"
  )
  public String payloadUriHash;

  @ColumnInfo(
      name = "payload_uri"
  )
  public String payloadUri;

  @Ignore
  public List<Inputs> inputs;
  @Ignore
  public List<Outputs> outputs;

}
