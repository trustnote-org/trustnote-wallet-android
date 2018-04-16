package org.trustnote.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import java.lang.String;

@Entity(
    tableName = "`address_definition_changes`"
)
public class AddressDefinitionChanges {
  @ColumnInfo(
      name = "`unit`"
  )
  public String unit;

  @ColumnInfo(
      name = "`message_index`"
  )
  public int messageIndex;

  @ColumnInfo(
      name = "`address`"
  )
  public String address;

  @ColumnInfo(
      name = "`definition_chash`"
  )
  public String definitionChash;
}
