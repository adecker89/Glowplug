package com.adecker.glowplugcompiler;

import java.util.Collection;

/**
 * Created by alex on 12/12/13.
 */
public abstract class GlowplugEntity {

    public abstract String getName();
    public abstract String getLocalName();
    public abstract String getRemoteName();

    public abstract GlowplugAttribute[] getAttributes();
    public abstract GlowplugRelationship[] getRelationships();

    public String getCreateSql() {
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE ").append(getLocalName()).append(" (");

        for(GlowplugAttribute attr : getAttributes()) {
            sb.append(attr.getCreateSql());
            sb.append(",");
        }

        sb.setLength(sb.length() - 1); //remove trailing comma
        sb.append(");");

        return sb.toString();
    }
}
