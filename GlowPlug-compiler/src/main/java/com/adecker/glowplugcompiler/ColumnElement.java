package com.adecker.glowplugcompiler;

import javax.lang.model.element.VariableElement;

/**
 * Created by alex on 10/29/13.
 */
public class ColumnElement {
	private String name;
	private String modelName;
	private String foreignKeyTable;
	private String foreignKeyColumn;

	public ColumnElement(VariableElement element) {
		Column columnAnnotation = element.getAnnotation(Column.class);

		name = element.getSimpleName().toString();

		if (columnAnnotation != null) {
			modelName = columnAnnotation.name();
		}
		if (modelName == null || modelName.isEmpty()) {
			modelName = element.getSimpleName().toString();
		}
	}

	public String getModelName() {
		return modelName;
	}

	public void setModelName(String modelName) {
		this.modelName = modelName;
	}

	public String getForeignKeyColumn() {
		return foreignKeyColumn;
	}

	public void setForeignKeyColumn(String foreignKeyColumn) {
		this.foreignKeyColumn = foreignKeyColumn;
	}

	public String getForeignKeyTable() {
		return foreignKeyTable;
	}

	public void setForeignKeyTable(String foreignKeyTable) {
		this.foreignKeyTable = foreignKeyTable;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
