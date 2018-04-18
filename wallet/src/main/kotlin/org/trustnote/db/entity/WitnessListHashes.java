package org.trustnote.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import java.lang.String;

@Entity(
    tableName = "witness_list_hashes"
)
public class WitnessListHashes extends TBaseEntity {
  @ColumnInfo(
      name = "witness_list_unit"
  )
  public String witnessListUnit;

  @ColumnInfo(
      name = "witness_list_hash"
  )
  public String witnessListHash;

  @ColumnInfo(
      name = "creation_date"
  )
  public long creationDate;
}
