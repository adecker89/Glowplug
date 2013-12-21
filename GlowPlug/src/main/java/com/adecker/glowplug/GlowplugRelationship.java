package com.adecker.glowplug;

import com.adecker.glowplugannotations.GlowplugType;

/**
 * Created by alex on 12/12/13.
 */
public class GlowplugRelationship extends GlowplugProperty {

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

    @Override
	public String getName() {
		return name;
	}

    @Override
    public String getSqliteName() {
        return name;
    }

    @Override
    public String getRemoteName() {
        return name;
    }

    @Override
	public String getFQName() {
		return tableName + "." + getSqliteName();
	}

    @Override
    public GlowplugType getType() {
        return GlowplugType.LONG;
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
}
