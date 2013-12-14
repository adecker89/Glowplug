package com.adecker.glowplugcompiler;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.MirroredTypeException;

/**
 * Created by alex on 12/12/13.
 */
public class VariableParser {
    private final VariableElement element;
	private final ProcessingEnvironment processingEnv;

	public VariableParser(ProcessingEnvironment processingEnv, VariableElement element) {
        this.element = element;
	    this.processingEnv = processingEnv;

    }

    public GlowplugAttribute parseAttribute() {
        Attribute columnAnnotation = element.getAnnotation(Attribute.class);

        String name = element.getSimpleName().toString();

        String localName = null;
        if (columnAnnotation != null) {
            localName = columnAnnotation.localName();
        }
        if (localName == null || localName.isEmpty()) {
            localName = name;
        }

        return new GlowplugAttribute(null, name, "");
    }

    public boolean isRelationship() {
        return element.getAnnotation(Relationship.class) != null;
    }

    public GlowplugRelationship parseRelationship() {
        Relationship rel = element.getAnnotation(Relationship.class);

        String name = element.getSimpleName().toString();

        String localName = null;
        localName = rel.localName();

        if (localName == null || localName.isEmpty()) {
            localName = name;
        }

        return new GlowplugRelationship(null,localName,getRelationshipType(rel),rel.key());

    }

	public String getRelationshipType(Relationship rel) {
		try {
			rel.table();
		} catch (MirroredTypeException e) {


			return processingEnv.getElementUtils().getTypeElement(e.getTypeMirror().toString()).getSimpleName().toString();
		}
		return null;
	}
}
