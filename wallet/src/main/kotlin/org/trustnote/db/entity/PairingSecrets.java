package org.trustnote.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import java.lang.String;

@Entity(
    tableName = "pairing_secrets"
)
public class PairingSecrets {
  @ColumnInfo(
      name = "pairing_secret"
  )
  public String pairingSecret;

  @ColumnInfo(
      name = "is_permanent"
  )
  public int isPermanent;

  @ColumnInfo(
      name = "creation_date"
  )
  public long creationDate;

  @ColumnInfo(
      name = "expiry_date"
  )
  public long expiryDate;
}
