package com.adecker.glowplug;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

import com.adecker.glowplugannotations.GlowplugType;

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
                if (relationship.isManyToMany()) {
                    relationshipTables.add(relationship);
                }
            }

            String command = getCreateSqlForEntity(entity);
            if (!TextUtils.isEmpty(command)) {
                db.execSQL(command);
                Log.v(TAG, "Creating table with command: " + command);
            }
        }

        for (GlowplugRelationship relationship : relationshipTables) {
            String command = getCreateSqlForRelationship(relationship);
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
                if (relationship.isManyToMany()) {
                    String command = dropSql + relationship.getManyToManyTableName();
                    db.execSQL(command);
                    Log.v(TAG, "dropping table with command: " + command);
                }
            }
            String command = dropSql + entity.getEntitySqliteName();
            db.execSQL(command);
            Log.v(TAG, "dropping table with command: " + command);
        }

        onCreate(db);
    }


    public String getCreateSqlForEntity(GlowplugEntity entity) {
        Set<GlowplugAttribute> primaryKeys = new HashSet<GlowplugAttribute>();


        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE ").append(entity.getEntitySqliteName()).append(" (");

        for (GlowplugAttribute attr : entity.getAttributes()) {
            if (attr.isPrimaryKey()) {
                primaryKeys.add(attr);
            }
        }

        for (GlowplugAttribute attr : entity.getAttributes()) {
            sb.append(getCreateSqlForAttribute(attr));
            if (attr.isPrimaryKey() && primaryKeys.size() == 1) {
                sb.append("PRIMARY KEY ");
                String primaryKeyConflictClause = attr.getPrimaryKeyConflictClause();
                if (!primaryKeyConflictClause.isEmpty()) {
                    sb.append(primaryKeyConflictClause);
                    sb.append(" ");
                }
                if (attr.isAutoIncrement()) {
                    sb.append("AUTOINCREMENT ");
                }
            }

            sb.append(",");
        }

        for (GlowplugRelationship rel : entity.getRelationships()) {
            if (!rel.isManyToMany()) {
                sb.append(getCreateSqlForRelationship(rel));
                sb.append(",");
            }
        }

        //TODO create composite primary keys

        sb.setLength(sb.length() - 1); //remove trailing comma
        sb.append(");");

        return sb.toString();
    }

    public String getCreateSqlForAttribute(GlowplugAttribute attr) {
        String type = attr.getSqliteType();
        if (type.isEmpty()) {
            type = inferSqliteTypeName(attr);
        }

        StringBuilder sb = new StringBuilder();
        sb.append(attr.getSqliteName());
        sb.append(" ");
        sb.append(type);
        sb.append(" ");
        for (String constraint : attr.getConstraints()) {
            sb.append(constraint);
            sb.append(" ");
        }

        return sb.toString();
    }

    private String inferSqliteTypeName(GlowplugAttribute attr) {
        GlowplugType type = attr.getType();
        switch (type) {
            case LONG:
            case INTEGER:
            case BOOLEAN:
                return "INTEGER";
            case DOUBLE:
            case FLOAT:
                return "REAL";
            case STRING:
                return "TEXT";
            default:
                throw new AssertionError("Glowplug was unable to infer sqlite type for " + attr.getFQName() + " with type: " + type + "\n please provide an explicit type using the appropriate annotation");
        }
    }

    public String getCreateSqlForRelationship(GlowplugRelationship rel) {
        if (rel.isManyToMany()) {
            //TODO create relationship table
            return "";
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append(rel.getSqliteName());
            sb.append(" INTEGER ");

            sb.append("REFERENCES ");
            sb.append(rel.getForeignTable());
            sb.append("(");
            sb.append(rel.getForeignKey());
            sb.append(") ");

            for (String constraint : rel.getConstraints()) {
                sb.append(constraint);
                sb.append(" ");
            }

            return sb.toString();
        }
    }
}
