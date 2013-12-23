package com.adecker.glowplugcompiler;

import com.adecker.glowplugannotations.Attribute;
import com.adecker.glowplugannotations.GlowplugType;
import com.adecker.glowplugannotations.Relationship;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.MirroredTypeException;
import javax.tools.Diagnostic;

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

	public AttributeStruct parseAttribute() {
		AttributeStruct attr = new AttributeStruct();
		attr.name = element.getSimpleName().toString();
		attr.type = GlowplugType.fromTypeName(Util.typeToString(element.asType()));

		if (attr.type == null) {
			processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Glowplug: attempted to add " + attr.name
					+ " with unsupported type: " + Util.typeToString(element.asType()));
			return null;
		}

		Attribute attrAnnotation = element.getAnnotation(Attribute.class);
		if (attrAnnotation != null) {
			attr.primaryKey = attrAnnotation.primaryKey();
			attr.autoIncrement = attrAnnotation.autoIncrement();
			attr.primaryKeyConflictClause = attrAnnotation.primaryKeyConflict();
			attr.sqliteName = attrAnnotation.sqliteName();
			attr.sqliteType = attrAnnotation.sqliteType();
			attr.remoteName = attrAnnotation.remoteName();
		}

		if (attr.sqliteName == null || attr.sqliteName.isEmpty()) {
			attr.sqliteName = attr.name;
		}

		if (attr.remoteName == null || attr.remoteName.isEmpty()) {
			attr.remoteName = attr.name;
		}

		return attr;
	}

	public boolean isRelationship() {
		return element.getAnnotation(Relationship.class) != null;
	}

	public RelationStruct parseRelationship() {
		Relationship rel = element.getAnnotation(Relationship.class);
		RelationStruct relationship = new RelationStruct();

		relationship.name = element.getSimpleName().toString();
		relationship.sqliteName = rel.sqliteName();
		relationship.remoteName = rel.remoteName();
		relationship.foreignTable = getRelationshipType(rel);
		relationship.foreignKey = rel.key();
		relationship.constraints = rel.constaints();

		if (relationship.sqliteName == null || relationship.sqliteName.isEmpty()) {
			relationship.sqliteName = relationship.name;
		}

		if (relationship.remoteName == null || relationship.remoteName.isEmpty()) {
			relationship.remoteName = relationship.name;
		}

		return relationship;
	}

	public String getRelationshipType(Relationship rel) {
		try {
			rel.table();
		} catch (MirroredTypeException e) {
			return processingEnv.getElementUtils().getTypeElement(e.getTypeMirror().toString()).getSimpleName()
					.toString();
		}
		return null;
	}

	public static class RelationStruct {
		public String name;
		public String sqliteName;
		public String remoteName;
		public String foreignTable;
		public String foreignKey;
		public String[] constraints;

		public String getName() {
			return name;
		}

		public String getSqliteName() {
			return sqliteName;
		}

		public String getRemoteName() {
			return remoteName;
		}

		public String getForeignTable() {
			return foreignTable;
		}

		public String getForeignKey() {
			return foreignKey;
		}

		public String[] getConstraints() { return constraints; }
	}

	public static class AttributeStruct {
		public String name;
		public GlowplugType type;
		public String sqliteName;
		public String sqliteType = "";
		public String remoteName;
		public boolean primaryKey;
		public boolean autoIncrement;
		public String primaryKeyConflictClause;

		public String getName() {
			return name;
		}

		public String getGlowplugType() {
			return type.toString();
		}

		public String getSimpleType() {
			return type.getSimpleName();
		}

		public String getSqliteName() {
			return sqliteName;
		}

		public String getSqliteType() {
			return sqliteType;
		}

		public String getRemoteName() {
			return remoteName;
		}

		public boolean isPrimaryKey() {
			return primaryKey;
		}

		public boolean isAutoIncrement() {
			return autoIncrement;
		}

		public String getPrimaryKeyConflictClause() {
			return primaryKeyConflictClause;
		}
	}
}
