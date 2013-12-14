package com.adecker.glowplugcompiler;

/**
 * Created by alex on 12/12/13.
 */
public class GlowplugRelationship {

	private String tableName;
	private String name;
	private String localName;
	private String remoteName;
	private String foreignTable;
	private String foreignKey;
	private boolean manyToMany;

	public GlowplugRelationship(String tableName, String name, String foreignTable, String foreignKey) {
		this.tableName = tableName;
		this.name = name;
		this.foreignTable = foreignTable;
		this.foreignKey = foreignKey;
	}

	public String getName() {
		return name;
	}

	public String getFQName() {
		return tableName + "." + getLocalName();
	}

	public String getLocalName() {
		return name;
	}

	public String getForeignTable() {
		return foreignTable;
	}

	public String getForeignKey() {
		return foreignKey;
	}

	public boolean isManyToMany() {
		return manyToMany;
	}

	public String[] getConstraints() {
		return new String[0];
	}

	/**
	 * @return The name of the relationship tablename if this relationship is many-to-many or null
	 */
	public String getManyToManyTableName() {
		if (!manyToMany) {
			return null;
		} else {
			return tableName + "_" + foreignTable;
		}
	}

	public String getCreateSql() {
		if (manyToMany) {
			return "";
		} else {
			StringBuilder sb = new StringBuilder();
			sb.append(getLocalName());
			sb.append(" INTEGER ");

			sb.append("REFERENCES ");
			sb.append(foreignTable);
			sb.append("(");
			sb.append(foreignKey);
			sb.append(") ");

			for (String constraint : getConstraints()) {
				sb.append(constraint);
				sb.append(" ");
			}

			return sb.toString();
		}
	}
}
