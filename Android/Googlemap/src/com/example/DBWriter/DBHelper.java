package com.example.DBWriter;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

	private final static String DB_NAME = "MotionDiary.db";
	private final static int DB_VERSION = 1;

	public static final String TABLE_NAME = "MotionData";
	private final String COLUMN_1 = "motion_type INT";
	private final String COLUMN_2 = "is_sleep INT";
	private final String COLUMN_3 = "latitude DOUBLE";
	private final String COLUMN_4 = "longitude DOUBLE";
	private final String COLUMN_5 = "current_system_time LONG";
	private final String COLUMN_6 = "year INT";
	private final String COLUMN_7 = "month INT";
	private final String COLUMN_8 = "day INT";
	private final String COLUMN_9 = "address TEXT";
	
	public static final String TABLE_NAME_SCHEDULE = "SCHEDULE";
	private final String SCHEDULE_1 = "schedule_time TEXT";
	private final String SCHEDULE_2 = "schedule_year INT";
	private final String SCHEDULE_3 = "schedule_month INT";
	private final String SCHEDULE_4 = "schedule_position INT";
	private final String SCHEDULE_5 = "schedule_listPosition INT";
	private final String SCHEDULE_6 = "schedule_memo TEXT";

	public DBHelper(Context context, CursorFactory factory) {
		super(context, DB_NAME, factory, DB_VERSION);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE " + TABLE_NAME
				+ " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_1 + ", "
				+ COLUMN_2 + ", " + COLUMN_3 + ", " + COLUMN_4 + ", "
				+ COLUMN_5 + ", " + COLUMN_6 + ", " + COLUMN_7 + ", "
				+ COLUMN_8 + ", " + COLUMN_9 + ");");
		
		db.execSQL("CREATE TABLE " + TABLE_NAME_SCHEDULE
				+ " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " + SCHEDULE_1 + ", "
				+ SCHEDULE_2 + ", " + SCHEDULE_3 + ", " + SCHEDULE_4 + ", "
				+ SCHEDULE_5 + ", " + SCHEDULE_6 + ");");

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

}
