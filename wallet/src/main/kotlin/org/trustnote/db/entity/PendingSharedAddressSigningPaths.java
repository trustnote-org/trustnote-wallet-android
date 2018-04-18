package org.trustnote.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import java.lang.Long;
import java.lang.String;

@Entity(
    tableName = "pending_shared_address_signing_paths"
)
public class PendingSharedAddressSigningPaths extends TBaseEntity {
  @ColumnInfo(
      name = "definition_template_chash"
  )
  public String definitionTemplateChash;

  @ColumnInfo(
      name = "device_address"
  )
  public String deviceAddress;

  @ColumnInfo(
      name = "signing_path"
  )
  public String signingPath;

  @ColumnInfo(
      name = "address"
  )
  public String address;

  @ColumnInfo(
      name = "device_addresses_by_relative_signing_paths"
  )
  public String deviceAddressesByRelativeSigningPaths;

  @ColumnInfo(
      name = "creation_date"
  )
  public long creationDate;

  @ColumnInfo(
      name = "approval_date"
  )
  public Long approvalDate;
}
