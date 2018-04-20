package org.trustnote.db.entity;


import android.arch.persistence.room.Ignore;
import android.support.annotation.NonNull;

import com.google.gson.JsonObject;

public class TBaseEntity implements Comparable{
    public static TBaseEntity VoidEntity = new TBaseEntity();
    @Ignore
    public JsonObject originalJson;
    @Ignore
    public JsonObject parentJson;
    @Ignore
    public TBaseEntity parent;

    @Override
    public int compareTo(@NonNull Object o) {
        return 0;
    }
}
