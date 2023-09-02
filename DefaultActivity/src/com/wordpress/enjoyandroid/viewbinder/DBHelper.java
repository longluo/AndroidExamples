/**
 * 
 */
package com.wordpress.enjoyandroid.viewbinder;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @author gautham
 *
 */
public class DBHelper extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = "data";
	private static final int DATABASE_VERSION = 1;

	public DBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	/* (non-Javadoc)
	 * @see android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite.SQLiteDatabase)
	 * 
	 * This is called when the database is created. DB is created when called
	 * for the first time when DB does not exist. data is the name of the database
	 * and data is stored in the file: /data/data/your-package-name/databases/data
	 * in the emulator.
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		// Create the table clubs.
		db.execSQL(ClubCP.DATABASE_CREATE);
		
		// Load the clubs.
		InitialClubs.addClubs(db);
	}

	/* (non-Javadoc)
	 * @see android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite.SQLiteDatabase, int, int)
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// No updation done here.

	}

}
