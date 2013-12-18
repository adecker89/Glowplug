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
        String type = "", name = "", primaryKeyConstraint = "";
        boolean primaryKey = false, autoIncrement = false;
        Attribute attr = element.getAnnotation(Attribute.class);

        if (attr != null) {
            name = attr.localName();
            type = attr.type();
            primaryKey = attr.primaryKey();
            primaryKeyConstraint = attr.primaryKeyContraint();
            autoIncrement = attr.autoIncrement();
        }

        if (name.isEmpty()) {
            name = element.getSimpleName().toString();
        }

        if(type.isEmpty()) {
            type = inferSqliteTypeName(element);
        }

        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, name +", "+ type);
        return new GlowplugAttribute(null, name, type).setPrimaryKey(primaryKey,autoIncrement,primaryKeyConstraint);
    }

    private String inferSqliteTypeName(Element element) {
        String type = Util.typeToString(element.asType());
        if(type.equals("java.lang.Long") || type.equals("java.lang.Integer") || type.equals("java.lang.Boolean")) {
            return "INTEGER";
        } else if(type.equals("java.lang.Double") || type.equals("java.lang.Float")) {
            return "REAL";
        } else if(type.equals("java.lang.String")) {
            return "TEXT";
        }

        else {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Glowplug: unable to infer sqlite type from: "+type+"\n please provide type using the appropriate annotation");
            return null;
        }
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
