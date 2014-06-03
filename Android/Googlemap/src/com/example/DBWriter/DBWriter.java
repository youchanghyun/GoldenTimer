package com.example.DBWriter;

import android.database.sqlite.SQLiteDatabase;

public class DBWriter {
	// DBHelper
	private DBHelper mDBHelper;

	public DBWriter(DBHelper mDBHelper) {
		super();
		this.mDBHelper = mDBHelper;
	}

	void insert(DataFormat data) {
		SQLiteDatabase db;
		db = mDBHelper.getWritableDatabase();
		db.execSQL("INSERT INTO " + DBHelper.TABLE_NAME + " VALUES (null, "
				+ data.getMotionType() + ", " + data.getIsSleep() + ", "
				+ data.getLatitude() + ", " + data.getLongitude() + ", "
				+ data.getCurSysTime() + ", " + data.getYear() + ", "
				+ data.getMonth() + ", " + data.getDay() + ", '"
				+ data.getAddress() + "');");
	}

	void insertSchedule(DataFormatSchedule data) {
		SQLiteDatabase db;
		db = mDBHelper.getWritableDatabase();
		db.execSQL("INSERT INTO " + DBHelper.TABLE_NAME_SCHEDULE
				+ " VALUES (null, '" + data.getTime() + "', " + data.getYear()
				+ ", " + data.getMonth() + ", " + data.getPosition() + ", "
				+ data.getListPosition() + ", '" + data.getMemo() + "');");
	}

	void deleteSchedule(int year, int month, int position) {
		SQLiteDatabase db;
		db = mDBHelper.getWritableDatabase();
		db.execSQL("DELETE FROM " + DBHelper.TABLE_NAME_SCHEDULE
				+ " WHERE schedule_year=" + year + " AND schedule_month="
				+ month + " AND schedule_position=" + position + ";");
	}

	void deleteSchedule(int year, int month, int position, int listPositon) {
		SQLiteDatabase db;
		db = mDBHelper.getWritableDatabase();
		db.execSQL("DELETE FROM " + DBHelper.TABLE_NAME_SCHEDULE
				+ " WHERE schedule_year=" + year + " AND schedule_month="
				+ month + " AND schedule_position=" + position
				+ " AND schedule_listPosition=" + listPositon + ";");
	}
}
