package com.adecker.glowplugcompiler;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.MirroredTypeException;
import javax.tools.Diagnostic;

/**
 * Created by alex on 12/12/13.
 */
public class VariableParser {
    public static final String ID_PRIMARYKEY_COLUMN = "_id";
    private final VariableElement element;
	private final ProcessingEnvironment processingEnv;

	public VariableParser(ProcessingEnvironment processingEnv, VariableElement element) {
        this.element = element;
	    this.processingEnv = processingEnv;

    }

    public GlowplugAttribute parseAttribute() {
        String name = element.getSimpleName().toString();
        String type = Util.typeToString(element.asType());
        GlowplugAttribute attr = new GlowplugAttribute(null, name, type);

        Attribute attrAnnotation = element.getAnnotation(Attribute.class);
        if (attrAnnotation != null) {
            attr.setPrimaryKey(attrAnnotation.primaryKey(), attrAnnotation.autoIncrement(), attrAnnotation.primaryKeyContraint());
            attr.setSqliteName(attrAnnotation.sqliteName());
            attr.setSqliteType(attrAnnotation.sqliteType());
        }
        return attr;
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
