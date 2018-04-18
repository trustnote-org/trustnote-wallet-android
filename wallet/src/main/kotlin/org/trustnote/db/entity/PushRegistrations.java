package org.trustnote.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import java.lang.String;

@Entity(
    tableName = "push_registrations"
)
public class PushRegistrations extends TBaseEntity {
  @ColumnInfo(
      name = "registrationId"
  )
  public String registrationId;

  @ColumnInfo(
      name = "device_address"
  )
  public String deviceAddress;
}
