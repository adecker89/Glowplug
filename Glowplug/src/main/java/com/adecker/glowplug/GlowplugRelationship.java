package com.adecker.glowplug;

import com.adecker.glowplugannotations.GlowplugType;

import java.util.ArrayList;

/**
 * Created by alex on 12/12/13.
 */
public class GlowplugRelationship extends GlowplugProperty {

	private final String tableName;
	private final String name;
	private String sqliteName;
	private String remoteName;
	private final String foreignTable;
	private final String foreignKey;
    private final int index;

	private boolean manyToMany;

	private ArrayList<String> constraints = new ArrayList<String>();

	public GlowplugRelationship(String tableName, String name, String foreignTable, String foreignKey, int index) {
		this.tableName = tableName;
		this.name = name;
		this.foreignTable = foreignTable;
		this.foreignKey = foreignKey;

        this.index = index;

		this.sqliteName = this.name;
		this.remoteName = this.name;
	}

    @Override
	public String getName() {
		return name;
	}

    @Override
    public String getSqliteName() {
        return sqliteName;
    }

    @Override
    public String getRemoteName() {
        return remoteName;
    }

    @Override
	public String getFQName() {
		return tableName + "." + getSqliteName();
	}

    public String getNaturalJoin() {
        return getFQName() + "=" + getForeignTable()+"."+getForeignKey();
    }

    @Override
    public GlowplugType getType() {
        return GlowplugType.LONG;
    }

    @Override
    public int getIndex() {
        return index;
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

	public ArrayList<String> getConstraints() {
		return constraints;
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

	public GlowplugRelationship setSqliteName(String sqliteName) {
		if (!sqliteName.isEmpty()) {
			this.sqliteName = sqliteName;
		}
		return this;
	}

	public GlowplugRelationship setRemoteName(String remoteName) {
		if (!remoteName.isEmpty()) {
			this.remoteName = remoteName;
		}
		return this;
	}

	public GlowplugRelationship addConstraint(String constraint) {
		constraints.add(constraint);
		return this;
	}

}
