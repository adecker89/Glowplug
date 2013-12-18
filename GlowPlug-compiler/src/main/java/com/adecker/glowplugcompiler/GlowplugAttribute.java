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


    public GlowplugAttribute(String tableName, String name, String sqliteType) {
        this.tableName = tableName;
        this.name = name;
        this.sqliteType = sqliteType;
    }

    public String getName() {return name;};
    public String getLocalName() {return name;};
    public String getRemoteName() {return name;};

    public String getFQName() {
        return tableName + "." + getLocalName();
    }

    public String getSqliteType() {return sqliteType;};
    public String[] getConstraints() {return new String[0];};

    public boolean isPrimaryKey() {
        return primaryKey;
    }

    public GlowplugAttribute addConstraint(String constraint) {
        constaints.add(constraint);
        return this;
    }

    public GlowplugAttribute setPrimaryKey(boolean primaryKey) {
        this.primaryKey = primaryKey;
        return this;
    }
}
