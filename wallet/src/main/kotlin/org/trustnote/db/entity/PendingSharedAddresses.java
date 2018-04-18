package org.trustnote.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import java.lang.String;

@Entity(
    tableName = "pending_shared_addresses"
)
public class PendingSharedAddresses {
  @ColumnInfo(
      name = "definition_template_chash"
  )
  public String definitionTemplateChash;

  @ColumnInfo(
      name = "definition_template"
  )
  public String definitionTemplate;

  @ColumnInfo(
      name = "creation_date"
  )
  public long creationDate;
}
