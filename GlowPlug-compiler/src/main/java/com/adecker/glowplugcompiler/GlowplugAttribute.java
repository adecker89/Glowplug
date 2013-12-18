package com.adecker.glowplugcompiler;

import java.util.ArrayList;

/**
 * Created by alex on 10/29/13.
 */
public class GlowplugAttribute {

    private final String tableName;
    private final String name;
    private final String sqliteType;

    private boolean primaryKey = false;
    private ArrayList<String> constaints = new ArrayList<String>();
    private boolean autoIncrement = false;
    private String primaryKeyConflict = "";


    public GlowplugAttribute(String tableName, String name, String sqliteType) {
        this.tableName = tableName;
        this.name = name;
        this.sqliteType = sqliteType;
    }

    public String getName() {
        return name;
    }

    ;

    public String getLocalName() {
        return name;
    }

    ;

    public String getRemoteName() {
        return name;
    }

    ;

    public String getFQName() {
        return tableName + "." + getLocalName();
    }

    public String getSqliteType() {
        return sqliteType;
    }

    ;

    public String[] getConstraints() {
        return new String[0];
    }

    ;

    public boolean isPrimaryKey() {


        return primaryKey;
    }

    public boolean isAutoIncrement() { return autoIncrement;  }

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
     *
     * @param primaryKey
     * @param autoIncrement set to add the autoincrement clause to the primary key, property is ignored if primary key is composite
     * @param conflictClause use to specify an ON CONFLICT clause for the primary key
     * @return
     */
    public GlowplugAttribute setPrimaryKey(boolean primaryKey, boolean autoIncrement, String conflictClause) {
        this.primaryKey = primaryKey;
        this.autoIncrement = autoIncrement;
        this.primaryKeyConflict = conflictClause;
        return this;
    }
}
