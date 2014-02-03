package com.adecker.glowplug;

import com.adecker.glowplugannotations.GlowplugType;

import java.util.ArrayList;

/**
 * Created by alex on 10/29/13.
 */
public class GlowplugAttribute extends GlowplugProperty {

    private final String tableName;
    private final String name;
    private final GlowplugType type;
    private final int index;

    private boolean primaryKey = false;
    private boolean autoIncrement = false;
    private String primaryKeyConflict = "";

    private ArrayList<String> constaints = new ArrayList<String>();

    private String sqliteType;
    private String sqliteName;

    private String remoteName;


    public GlowplugAttribute(String tableName, String name, GlowplugType type, int index) {
        this.tableName = tableName;
        this.name = name;
        this.type = type;
        this.index = index;

        sqliteName = name;
        sqliteType = "";

        remoteName = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getSqliteName() {
        return sqliteName;
    }

    @Override
    public String getRemoteName() {
        return remoteName;
    }

    @Override
    public String getFQName() {
        return tableName + "." + getSqliteName();
    }

    @Override
    public GlowplugType getType() {
        return type;
    }

    public String getSqliteType() {
        return sqliteType;
    }

    public String[] getConstraints() {
        return new String[0];
    }

    public boolean isPrimaryKey() {
        return primaryKey;
    }

    public boolean isAutoIncrement() {
        return autoIncrement;
    }

    public String getPrimaryKeyConflictClause() {
        return primaryKeyConflict;
    }

    public GlowplugAttribute addConstraint(String constraint) {
        constaints.add(constraint);
        return this;
    }

    public GlowplugAttribute setPrimaryKey(boolean primaryKey) {
        setPrimaryKey(primaryKey, false);
        return this;
    }

    /**
     * @param primaryKey
     * @param autoIncrement set to add the autoincrement clause to the primary key, property is ignored if primary key is composite
     * @return
     */
    public GlowplugAttribute setPrimaryKey(boolean primaryKey, boolean autoIncrement) {
        setPrimaryKey(primaryKey, autoIncrement, null);
        return this;
    }

    /**
     * @param primaryKey
     * @param autoIncrement  set to add the autoincrement clause to the primary key, property is ignored if primary key is composite
     * @param conflictClause use to specify an ON CONFLICT clause for the primary key
     * @return
     */
    public GlowplugAttribute setPrimaryKey(boolean primaryKey, boolean autoIncrement, String conflictClause) {
        this.primaryKey = primaryKey;
        this.autoIncrement = autoIncrement;
        this.primaryKeyConflict = conflictClause;
        return this;
    }

    public GlowplugAttribute setSqliteName(String sqliteName) {
        if (!sqliteName.isEmpty()) {
            this.sqliteName = sqliteName;
        }
        return this;
    }

    public GlowplugAttribute setSqliteType(String sqliteType) {
        this.sqliteType = sqliteType;
        return this;
    }

    public GlowplugAttribute setRemoteName(String remoteName) {
        if (!remoteName.isEmpty()) {
            this.remoteName = remoteName;
        }
        return this;
    }

    public int getIndex() {
        return index;
    }
}
