package com.adecker.glowplug;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.JsonReader;
import android.util.Log;

import java.io.IOException;
import java.util.Map;

/**
 * Created by alex on 12/12/13.
 */
public abstract class GlowplugEntity {

	private static final String TAG = "Entity";
	protected ContentValues values;
	protected Cursor cursor;
	protected int cursorPosition = -1;

	public GlowplugEntity() {
	}

	public GlowplugEntity(JsonReader reader) throws IllegalStateException {
		fromJson(reader);
	}

	public GlowplugEntity(Cursor cursor) {
		fromCursor(cursor);
	}

    public GlowplugEntity(Bundle bundle) {
        fromBundle(bundle);
    }


	public GlowplugEntity fromJson(JsonReader reader) {
		values = new ContentValues();
		try {
			reader.beginObject();
			GlowplugProperty property = null;
			while (reader.hasNext()) {
				String name = null;
				switch (reader.peek()) {
					case NAME:
						name = reader.nextName();
						property = getPropertyMap().get(name);
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
									Log.d(TAG, "skipping " + property.getType());
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
					Log.v(TAG,getEntityName()+ " -- skipping unknown property: "+name);
				}
			}
			reader.endObject();

		} catch (IOException e) {
			e.printStackTrace();
		}
        return this;
	}

	public GlowplugEntity fromCursor(Cursor cursor) {
        this.cursor = cursor;
        this.cursorPosition = cursor.getPosition();

        return this;
    }

    public GlowplugEntity fromBundle(Bundle bundle) {
        for(GlowplugProperty property : getPropertyMap().values()) {
            switch (property.getType()) {
                case LONG:
                    setPropertyInternal(property,bundle.getLong(property.getName()));
                    break;
                case INTEGER:
                    setPropertyInternal(property,bundle.getInt(property.getName()));
                    break;
                case STRING:
                    setPropertyInternal(property,bundle.getString(property.getName()));
                    break;
                case BOOLEAN:
                    setPropertyInternal(property,bundle.getBoolean(property.getName()));
                    break;
                case DOUBLE:
                    setPropertyInternal(property,bundle.getDouble(property.getName()));
                    break;
                case FLOAT:
                    setPropertyInternal(property,bundle.getFloat(property.getName()));
                    break;
                case BLOB:
                    Log.w(TAG,"Putting blobs in bundles is not implemented");
                    break;
            }
        }

        return this;
    }

    /**
     * @return the non-composite primary key of an entity. Returns null if key is composite or not found
     */
    public GlowplugAttribute getPrimaryKey() {
        GlowplugAttribute primaryKey = null;
        for(GlowplugAttribute attr : getAttributes()) {
            if(attr.isPrimaryKey()) {
                if(primaryKey == null) {
                    primaryKey = attr;
                } else {
                    return null;
                }
            }
        }
        return primaryKey;
    }

	public abstract Map<String, GlowplugProperty> getPropertyMap();

	public abstract GlowplugRelationship[] getRelationships();

	public abstract GlowplugAttribute[] getAttributes();

	protected void setPropertyInternalNull(GlowplugProperty property) {
		if (values == null) {
			values = new ContentValues();
		}
		values.putNull(property.getSqliteName());
	}

	protected void setPropertyInternal(GlowplugProperty property, boolean value) {
		if (values == null) {
			values = new ContentValues();
		}
		values.put(property.getSqliteName(), value);
	}

	protected void setPropertyInternal(GlowplugProperty property, double value) {
		if (values == null) {
			values = new ContentValues();
		}
		values.put(property.getSqliteName(), value);
	}

    protected void setPropertyInternal(GlowplugProperty property, int value) {
        if (values == null) {
            values = new ContentValues();
        }
        values.put(property.getSqliteName(), value);
    }

	protected void setPropertyInternal(GlowplugProperty property, long value) {
		if (values == null) {
			values = new ContentValues();
		}
		values.put(property.getSqliteName(), value);
	}

	protected void setPropertyInternal(GlowplugProperty property, String value) {
		if (values == null) {
			values = new ContentValues();
		}
		values.put(property.getSqliteName(), value);
	}

	protected void setPropertyInternal(GlowplugProperty property, byte[] value) {
		if (values == null) {
			values = new ContentValues();
		}
		values.put(property.getSqliteName(), value);
	}

	public abstract String getEntityName();

	public abstract String getEntitySqliteName();

	public abstract String getEntityRemoteName();

	protected Object getPropertyInternal(GlowplugProperty property) {
		if (values != null && values.containsKey(property.getSqliteName())) {
            Log.i(TAG,"Property:"+property.getName()+" getting value from ContentValues");
			return values.get(property.getSqliteName());
		} else if (cursor != null) {
            Log.i(TAG,"Property:"+property.getName()+" getting value from cursor");
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
                default:
                    throw new InternalError("Property with name <"+property.getName()+"> accessed with unknown type"+property.getType());
			}
		} else {
            Log.v(TAG,"Property:"+property.getName()+" getting default value");
            switch (property.getType()) {
                case LONG:
                    return Long.valueOf(0);
                case INTEGER:
                    return Integer.valueOf(0);
                case STRING:
                    return null;
                case BOOLEAN:
                    return Boolean.valueOf(false);
                case DOUBLE:
                    return Double.valueOf(0);
                case FLOAT:
                    return Float.valueOf(0);
                case BLOB:
                    return new byte[0];
                default:
                    throw new InternalError("Property with name <"+property.getName()+"> accessed with unknown type"+property.getType());
            }
		}
	}

	protected int getPropertyIndex(GlowplugProperty property) {
		return property.getIndex();
	}

	public ContentValues getContentValues() {
		return values;
	}

    public Bundle getBundle() {
        Bundle bundle = new Bundle();
        for(GlowplugProperty property : getPropertyMap().values()) {
            Log.v("","Bundling " + property.getName());
            Object value = getPropertyInternal(property);
            switch (property.getType()) {
                case LONG:
                    bundle.putLong(property.getName(),(Long)value);
                    break;
                case INTEGER:
                    bundle.putInt(property.getName(), (Integer) value);
                    break;
                case STRING:
                    bundle.putString(property.getName(), (String) value);
                    break;
                case BOOLEAN:
                    bundle.putBoolean(property.getName(), (Boolean) value);
                    break;
                case DOUBLE:
                    bundle.putDouble(property.getName(), (Double) value);
                    break;
                case FLOAT:
                    bundle.putFloat(property.getName(), (Float) value);
                    break;
                case BLOB:
                    Log.w(TAG,"Putting blobs in bundles is not implemented");
                    break;
            }
        }

        return bundle;
    }

    public String getUriPath() {
        return "/" + getEntityName() + "/" + getPropertyInternal(getPrimaryKey());
    }
}
