package com.adecker.glowplugcompiler;

import javax.lang.model.element.VariableElement;

/**
 * Created by alex on 10/29/13.
 */
public abstract class GlowplugAttribute {
	public abstract String getName();
    public abstract String getLocalName();
    public abstract String getRemoteName();

    public abstract String getSqliteType();
    public abstract String[] getConstraints();


    public String getCreateSql() {
        StringBuilder sb = new StringBuilder();
        sb.append(getLocalName());
        sb.append(" ");
        sb.append(getSqliteType());
        sb.append(" ");
        for(String contraint : getConstraints()) {
            sb.append(contraint);
            sb.append(" ");
        }

        return sb.toString();
    }
}
