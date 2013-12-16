package com.adecker.glowplugcompiler;

/**
 * Created by alex on 10/29/13.
 */
public class GlowplugAttribute {

    private final String tableName;
    private final String name;
    private final String sqliteType;

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


    public String getCreateSql() {
        StringBuilder sb = new StringBuilder();
        sb.append(getLocalName());
        sb.append(" ");
        sb.append(getSqliteType());
        sb.append(" ");
        for(String constraint : getConstraints()) {
            sb.append(constraint);
            sb.append(" ");
        }

        return sb.toString();
    }
}
