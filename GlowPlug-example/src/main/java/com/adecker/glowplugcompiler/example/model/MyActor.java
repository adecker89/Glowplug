package com.adecker.glowplugcompiler.example.model;

import android.database.Cursor;
import android.util.JsonReader;

/**
 * Created by alex on 12/19/13.
 */
public class MyActor extends ActorEntity {
    public MyActor() {
        super();
    }

    public MyActor(JsonReader reader) throws IllegalStateException {
        super(reader);
    }

    public MyActor(Cursor cursor, int index) {
        super(cursor, index);
    }

    public MyActor(Cursor cursor, int index, String[] projection) {
        super(cursor, index, projection);
    }

    @Override
    public String toString() {
        return "";//this.getFirstName() + " " + this.getLastName();
    }
}
