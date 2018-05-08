package org.trustnote.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

import java.lang.String;

@Entity(
        tableName = "authentifiers",
        primaryKeys = {"unit", "address", "path"}
)
public class Authentifiers extends TBaseEntity {
    @ColumnInfo(
            name = "unit"
    )
    @NonNull
    public String unit;

    @ColumnInfo(
            name = "address"
    )
    @NonNull
    public String address;

    @ColumnInfo(
            name = "path"
    )
    @NonNull
    public String path;

    @ColumnInfo(
            name = "authentifier"
    )
    public String authentifier;

    @Ignore
    @SerializedName("authentifiers")
    public JsonObject authentifiers;

    @Ignore
    public JsonArray definition;

    public void parsePathAndAuthentifier() {
        if (authentifiers != null) {
            if (authentifiers.has("r")) {
                path = "r";
                authentifier = authentifiers.get("r").getAsString();
            }
        }
    }
}
