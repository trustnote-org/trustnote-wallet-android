package org.trustnote.db.entity;


import android.arch.persistence.room.Ignore;

import com.google.gson.JsonObject;

public class TBaseEntity {
    public static TBaseEntity VoidEntity = new TBaseEntity();
    @Ignore
    public JsonObject originalJson;
    @Ignore
    public JsonObject parentJson;
    @Ignore
    public TBaseEntity parent;
}
