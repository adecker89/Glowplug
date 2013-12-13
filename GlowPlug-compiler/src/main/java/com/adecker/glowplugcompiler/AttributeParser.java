package com.adecker.glowplugcompiler;

import javax.lang.model.element.VariableElement;

/**
 * Created by alex on 12/12/13.
 */
public class AttributeParser {
    private final VariableElement element;

    public AttributeParser(VariableElement element) {
        this.element = element;

    }

    public GlowplugAttribute parse() {
        Attribute columnAnnotation = element.getAnnotation(Attribute.class);

        String name = element.getSimpleName().toString();

        String localName = null;
        if (columnAnnotation != null) {
            localName = columnAnnotation.name();
        }
        if (localName == null || localName.isEmpty()) {
            localName = element.getSimpleName().toString();
        }

        return new GlowplugAttribute(null,name,"");
    }
}
