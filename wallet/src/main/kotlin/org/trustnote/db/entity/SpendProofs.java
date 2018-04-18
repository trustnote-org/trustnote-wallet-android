package org.trustnote.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import java.lang.String;

@Entity(
    tableName = "spend_proofs"
)
public class SpendProofs extends TBaseEntity {
  @ColumnInfo(
      name = "unit"
  )
  public String unit;

  @ColumnInfo(
      name = "message_index"
  )
  public int messageIndex;

  @ColumnInfo(
      name = "spend_proof_index"
  )
  public int spendProofIndex;

  @ColumnInfo(
      name = "spend_proof"
  )
  public String spendProof;

  @ColumnInfo(
      name = "address"
  )
  public String address;
}
