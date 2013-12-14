package com.adecker.glowplug;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

import com.adecker.glowplugcompiler.GlowplugEntity;
import com.adecker.glowplugcompiler.GlowplugRelationship;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by alex on 12/9/13.
 */
public class GlowplugOpenHelper extends SQLiteOpenHelper {

    private static final String TAG = "GlowplugOpenHelper";
    private GlowplugEntity[] entities;
    private Set<GlowplugRelationship> relationshipTables = new HashSet<GlowplugRelationship>();

    public GlowplugOpenHelper(Context context, String name, int version, GlowplugEntity[] entities) {
        super(context, name, null, version);
        this.entities = entities;
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        db.execSQL("PRAGMA foreign_keys=ON;");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.v(TAG, "Creating new database: " + getDatabaseName());

        for (GlowplugEntity entity : entities) {
            for (GlowplugRelationship relationship : entity.getRelationships()) {
	            if(relationship.isManyToMany()) {
                    relationshipTables.add(relationship);
	            }
            }

            String command = entity.getCreateSql();
            if (!TextUtils.isEmpty(command)) {
                db.execSQL(command);
                Log.v(TAG, "Creating table with command: " + command);
            }
        }

        for(GlowplugRelationship relationship : relationshipTables) {
            String command = relationship.getCreateSql();
            if (!TextUtils.isEmpty(command)) {
                db.execSQL(command);
                Log.v(TAG, "Creating table with command: " + command);
            }
        }
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	    String dropSql = "DROP TABLE IF EXISTS ";
	    for (GlowplugEntity entity : entities) {
		    for (GlowplugRelationship relationship : entity.getRelationships()) {
			    if(relationship.isManyToMany()) {
				    String command = dropSql + relationship.getManyToManyTableName();
				    db.execSQL(command);
				    Log.v(TAG, "dropping table with command: " + command);
			    }
		    }
		    String command = dropSql + entity.getLocalName();
		    db.execSQL(command);
		    Log.v(TAG, "dropping table with command: " + command);
	    }

	    onCreate(db);
    }

}
