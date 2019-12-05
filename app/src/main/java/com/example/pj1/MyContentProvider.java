package com.example.pj1;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import androidx.annotation.Nullable;

import java.util.HashMap;

public class MyContentProvider extends ContentProvider {
    static final String AUTHORITY = "com.example.pj1";
    static final String URL = "content://" + AUTHORITY + "/data";
    static final Uri uri = Uri.parse(URL);
    static HashMap<String, String> MAP;
    private SQLiteDatabase db;
    static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        uriMatcher.addURI(AUTHORITY, "data", 1); //all
        uriMatcher.addURI(AUTHORITY, "data" + "/#", 2); //one
    }

    public MyContentProvider() {
    }

    public static class DBHelper extends SQLiteOpenHelper {
        public DBHelper(@Nullable Context context) {
            super(context, "QLTG", null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("create table authors(id integer primary key, name text, address text, email text)");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("drop table if exists authors");
            onCreate(db);
        }
    }


    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        int count = db.delete("authors", selection, selectionArgs);
        return count;
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // TODO: Implement this to handle requests to insert a new row.
        int row1 = (int) db.insert("authors", null, values);
        Uri uri1 = ContentUris.withAppendedId(uri, row1);
        getContext().getContentResolver().notifyChange(uri1, null);
        return uri1;

    }

    @Override
    public boolean onCreate() {
        // TODO: Implement this to initialize your content provider on startup.
        DBHelper dbHelper = new DBHelper(getContext());
        db = dbHelper.getWritableDatabase();
        if (db == null)
            return false;
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        // TODO: Implement this to handle query requests from clients.
        SQLiteQueryBuilder sql = new SQLiteQueryBuilder();
        sql.setTables("authors");
        switch (uriMatcher.match(uri)) {
            case 1:
                sql.setProjectionMap(MAP);
                break;
            case 2:
                sql.appendWhere("id=" + uri.getPathSegments().get(1));
        }

        Cursor cursor = sql.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        int count = db.update("authors", values, selection, selectionArgs);
        return count;
    }
}
