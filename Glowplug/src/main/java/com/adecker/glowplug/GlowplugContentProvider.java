package com.adecker.glowplug;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import java.util.HashMap;

public abstract class GlowplugContentProvider extends ContentProvider {
    private static final String TAG = "GlowplugContentProvider";

    public static final int TABLE = 101;
    public static final int TABLE_ITEM = 102;


    protected UriMatcher mUriMatcher;
    protected HashMap<String, GlowplugEntity> mEntityMap;

    @Override
    public boolean onCreate() {
        return true;
    }

    @Override
    public String getType(Uri uri) {
        switch(getUriMatcher().match(uri)) {
            case TABLE:
                return "vnd.android.cursor.dir";
            case TABLE_ITEM:
                return "vnd.android.cursor.item";
            default:
                throw new UnsupportedOperationException("Not yet implemented");
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        String entityName = uri.getPathSegments().get(0);

        SQLiteDatabase db = getOpenHelper().getReadableDatabase();
        long id = db.insert(entityName, null, values);

        notifyChangeForUri(uri);

        return Uri.withAppendedPath(uri, String.valueOf(id));

    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        String entityName = uri.getPathSegments().get(0);
        SQLiteDatabase db = getOpenHelper().getReadableDatabase();
        GlowplugEntity entity = getEntityMap().get(entityName);

        switch (getUriMatcher().match(uri)) {
            case TABLE:
                break;
            case TABLE_ITEM:
                long id = ContentUris.parseId(uri);
                GlowplugAttribute key = entity.getPrimaryKey();
                if (key == null) {
                    Log.w(TAG, "Attempted to query item in table without primary key. " + uri);
                    return 0;
                }

                if (TextUtils.isEmpty(selection)) {
                    selection = key.getSqliteName() + "=" + id;
                } else {
                    selection = selection + "AND (" + key.getSqliteName() + "=" + id + ")";
                }
                break;
            default:
                return 0;
        }

        int count = db.delete(entityName, selection, selectionArgs);

        notifyChangeForUri(uri);

        return count;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
            String[] selectionArgs, String sortOrder) {

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        String entityName = uri.getPathSegments().get(0);
        GlowplugEntity entity = getEntityMap().get(entityName);

        queryBuilder.setTables(entityName);

       switch(getUriMatcher().match(uri)) {
           case TABLE:
               break;
           case TABLE_ITEM:
               long id = ContentUris.parseId(uri);
               GlowplugAttribute key = entity.getPrimaryKey();
               if(key == null) {
                   Log.w(TAG, "Attempted to query item in table without primary key. " + uri);
                   return null;
               }
               queryBuilder.appendWhereEscapeString(key.getSqliteName() + "=" + id);
               break;
           default:
               return null;
       }

        SQLiteDatabase db = getOpenHelper().getReadableDatabase();
        Cursor cursor = queryBuilder.query(db,projection,selection,selectionArgs,null,null,sortOrder);

        cursor.setNotificationUri(getContext().getContentResolver(),uri);

        return cursor;
    }

    private void notifyChangeForUri(Uri uri) {
        this.getContext().getContentResolver().notifyChange(uri, null, false);
    }

    protected abstract String getAuthority();

    protected abstract GlowplugEntity[] getEntities();

    protected abstract SQLiteOpenHelper getOpenHelper();

    protected UriMatcher getUriMatcher() {

        if(mUriMatcher == null) {
            mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

            String authority = this.getAuthority();
            for (GlowplugEntity entity : this.getEntities()) {
                mUriMatcher.addURI(authority, entity.getEntityName(), TABLE);
                mUriMatcher.addURI(authority, entity.getEntityName() + "/#", TABLE_ITEM);
            }
        }

        return mUriMatcher;
    }

    protected HashMap<String, GlowplugEntity> getEntityMap() {
        if(mEntityMap == null) {
            GlowplugEntity[] entities = getEntities();
            mEntityMap = new HashMap<String, GlowplugEntity>(entities.length);

            for(GlowplugEntity entity : entities) {
                mEntityMap.put(entity.getEntityName(),entity);
            }
        }
        return mEntityMap;
    }
}
