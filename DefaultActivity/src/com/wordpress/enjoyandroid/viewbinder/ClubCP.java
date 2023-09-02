package com.wordpress.enjoyandroid.viewbinder;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class ClubCP extends ContentProvider {
	// DB helper
	private DBHelper mDbHelper;
	protected SQLiteDatabase mDb;

	public static final String AUTHORITY = 
			"com.wordpress.enjoyandroid.viewbinder.ClubCP";
	public static final String CLUBS_STRING = "clubs";

	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
			+ "/" + CLUBS_STRING);
	
	// Name of the table for storing club information.
	public static final String TABLE_NAME = "clubs";
	
	//Fields.
	public static final String KEY_ID = "_id";
	public static final String KEY_NAME = "name";
	public static final String KEY_LINK = "link";
	public static final String KEY_IS_STAR = "is_star";
	
	public static final String DATABASE_CREATE = "CREATE TABLE " + TABLE_NAME
			+ " (" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + KEY_NAME
			+ " TEXT NOT NULL, " + KEY_LINK + " TEXT, " + KEY_IS_STAR 
			+ " INTEGER NOT NULL " + " ) ";
	
	@Override
	public boolean onCreate() {
		open();
		return true;
	}
	
	private void open() throws SQLException {
		mDbHelper = new DBHelper(getContext());
		mDb = mDbHelper.getWritableDatabase();
	}
	
	public void close() {
		mDbHelper.close();
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		Cursor cursor;
		cursor = mDb.query(TABLE_NAME, projection, selection, 
				selectionArgs, null, null, sortOrder);
				
		return cursor;
	}

	/**
	 * Below methods are not used in this example.
	 */
	@Override
	public String getType(Uri uri) {
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		return null;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		return 0;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		return 0;
	}

}
