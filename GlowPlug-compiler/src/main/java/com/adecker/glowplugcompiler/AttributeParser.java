package com.adecker.glowplugcompiler;

import javax.lang.model.element.VariableElement;

/**
 * Created by alex on 12/12/13.
 */
public class AttributeParser extends GlowplugAttribute {
    private final String name;
    private final String localName;

    public AttributeParser(VariableElement element) {
        Attribute columnAnnotation = element.getAnnotation(Attribute.class);

        name = element.getSimpleName().toString();

        String localName = null;
        if (columnAnnotation != null) {
            localName = columnAnnotation.name();
        }
        if (localName == null || localName.isEmpty()) {
            localName = element.getSimpleName().toString();
        }
        this.localName = localName;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getLocalName() {
        return (localName != null && !localName.isEmpty()) ? localName : name;
    }

    @Override
    public String getRemoteName() {
        return null;
    }

    @Override
    public String getSqliteType() {
        return null;
    }

    @Override
    public String[] getConstraints() {
        return new String[0];
    }
}
