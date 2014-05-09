package com.example.googlemaptest;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DB_OPEN extends SQLiteOpenHelper {

	/** Called when the activity is first created. */

	public DB_OPEN(Context context) {
		super(context, "db_user", null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL("CREATE TABLE sensor_data"
				+ "(_user_id text primarykey, "
				+ "user_id integer, latitude float, longitude float, month INTEGER, day INTEGER, hour INTEGER, minute INTEGER, second INTEGER);");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		db.execSQL("DROP TABLE IF EXITS db_user");
		onCreate(db);
	}

}
