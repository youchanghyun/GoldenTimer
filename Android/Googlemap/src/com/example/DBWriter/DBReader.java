package com.example.DBWriter;

import java.util.ArrayList;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DBReader {
	// DBHelper
	private DBHelper mDBHelper;

	public DBReader(DBHelper mDBHelper) {
		super();
		this.mDBHelper = mDBHelper;
	}

	ArrayList<DataFormat> read(int year, int month, int day) {
		SQLiteDatabase db = mDBHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery("SELECT * FROM " + DBHelper.TABLE_NAME
				+ " WHERE year=" + year + " AND month=" + month + " AND day="
				+ day, null);
		ArrayList<DataFormat> result = new ArrayList<DataFormat>();
		while (cursor.moveToNext()) {
			result.add(new DataFormat(cursor.getInt(1), cursor.getInt(2),
					cursor.getDouble(3), cursor.getDouble(4),
					cursor.getLong(5), cursor.getInt(6), cursor.getInt(7),
					cursor.getInt(8), cursor.getString(9)));
		}
		return result;
	}

	ArrayList<DataFormat> read(int year, int month) {
		SQLiteDatabase db = mDBHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery("SELECT * FROM " + DBHelper.TABLE_NAME
				+ " WHERE year=" + year + " AND month=" + month, null);
		ArrayList<DataFormat> result = new ArrayList<DataFormat>();
		while (cursor.moveToNext()) {
			result.add(new DataFormat(cursor.getInt(1), cursor.getInt(2),
					cursor.getDouble(3), cursor.getDouble(4),
					cursor.getLong(5), cursor.getInt(6), cursor.getInt(7),
					cursor.getInt(8), cursor.getString(9)));
		}
		return result;
	}

	ArrayList<DataFormatSchedule> readSchedules(int year, int month) {
		SQLiteDatabase db = mDBHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery("SELECT * FROM "
				+ DBHelper.TABLE_NAME_SCHEDULE + " WHERE schedule_year=" + year
				+ " AND schedule_month=" + month , null);
		ArrayList<DataFormatSchedule> result = new ArrayList<DataFormatSchedule>();
		while (cursor.moveToNext()) {
			result.add(new DataFormatSchedule(cursor.getString(1), cursor
					.getInt(2), cursor.getInt(3), cursor.getInt(4), cursor
					.getInt(5), cursor.getString(6)));
		}
		return result;
	}
}
