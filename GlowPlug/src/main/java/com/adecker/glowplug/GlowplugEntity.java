package com.adecker.glowplug;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.JsonReader;
import android.util.Log;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by alex on 12/12/13.
 */
public abstract class GlowplugEntity {

	private static final String TAG = "Entity";
	protected ContentValues values;
	protected Cursor cursor;
	protected int cursorPosition;
	protected Map<String, GlowplugProperty> propertyMap;

	public GlowplugEntity() {
	}

	public GlowplugEntity(JsonReader reader) throws IllegalStateException {
		fromJson(reader);
	}

	public void fromJson(JsonReader reader) {
		values = new ContentValues();
		try {
			reader.beginObject();
			GlowplugProperty property = null;
			while (reader.hasNext()) {
				switch (reader.peek()) {
					case NAME:
						String name = reader.nextName();
						property = getPropertyMap().get(name);
						Log.v(TAG, name + " " + property);
						break;
					default:
						throw new IllegalStateException("JsonReader: Expected name, but got " + reader.peek());
				}

				if (property != null) {
					switch (reader.peek()) {
						case STRING:
							setPropertyInternal(property, reader.nextString());
							break;
						case NUMBER:
							switch (property.getType()) {
								case LONG:
									setPropertyInternal(property, reader.nextLong());
									break;
								case INTEGER:
									setPropertyInternal(property, reader.nextInt());
									break;
								case DOUBLE:
								case FLOAT:
									setPropertyInternal(property, reader.nextDouble());
									break;
								case STRING:
									setPropertyInternal(property, reader.nextString());
									break;
								default:
									Log.d(TAG,"skipping "+property.getType());
									reader.skipValue();
									break;
							}
							break;
						case BOOLEAN:
							setPropertyInternal(property, reader.nextBoolean());
							break;
						case NULL:
							reader.nextNull();
							setPropertyInternalNull(property);
							break;
					}
				} else {
					reader.skipValue();
				}
			}
			reader.endObject();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected Map<String, GlowplugProperty> getPropertyMap() {
		if (propertyMap == null) {
			propertyMap = new HashMap<String, GlowplugProperty>();
			for (GlowplugProperty attr : getAttributes()) {
				propertyMap.put(attr.getRemoteName(), attr);
			}
			for (GlowplugProperty rel : getRelationships()) {
				propertyMap.put(rel.getRemoteName(), rel);
			}
		}
		return propertyMap;
	}

	public abstract GlowplugRelationship[] getRelationships();

	public abstract GlowplugAttribute[] getAttributes();

	protected void setPropertyInternalNull(GlowplugProperty property) {
		if(values == null) {
			values = new ContentValues();
		}
		values.putNull(property.getSqliteName());
	}

	protected void setPropertyInternal(GlowplugProperty property, boolean value) {
		if(values == null) {
			values = new ContentValues();
		}
		values.put(property.getSqliteName(), value);
	}

	protected void setPropertyInternal(GlowplugProperty property, double value) {
		if(values == null) {
			values = new ContentValues();
		}
		values.put(property.getSqliteName(), value);
	}

	protected void setPropertyInternal(GlowplugProperty property, long value) {
		if(values == null) {
			values = new ContentValues();
		}
		values.put(property.getSqliteName(), value);
	}

	protected void setPropertyInternal(GlowplugProperty property, String value) {
		if(values == null) {
			values = new ContentValues();
		}
		values.put(property.getSqliteName(), value);
	}

	protected void setPropertyInternal(GlowplugProperty property, byte[] value) {
		if(values == null) {
			values = new ContentValues();
		}
		values.put(property.getSqliteName(), value);
	}


	public GlowplugEntity(Cursor cursor, int index) {

	}

	public GlowplugEntity(Cursor cursor, int index, String[] projection) {
	}


	public abstract String getEntityName();

	public abstract String getEntitySqliteName();

	public abstract String getEntityRemoteName();

	protected Object getPropertyInternal(GlowplugProperty property) {
		if (values != null) {
			return values.get(property.getSqliteName());
		} else if (cursor != null) {
			cursor.moveToPosition(cursorPosition);
			switch (property.getType()) {
				case LONG:
					return cursor.getLong(getPropertyIndex(property));
				case INTEGER:
					return cursor.getInt(getPropertyIndex(property));
				case STRING:
					return cursor.getString(getPropertyIndex(property));
				case BOOLEAN:
					return cursor.getShort(getPropertyIndex(property)) == 1;
				case DOUBLE:
					return cursor.getDouble(getPropertyIndex(property));
				case FLOAT:
					return cursor.getFloat(getPropertyIndex(property));
				case BLOB:
					return cursor.getBlob(getPropertyIndex(property));
			}
			return null;
		} else {
			return null;
		}
	}

	protected int getPropertyIndex(GlowplugProperty property) {
		return cursor.getColumnIndex(property.getFQName());
	}

	public ContentValues getContentValues() {
		return values;
	}

}
