package com.example.DBWriter;

import java.util.ArrayList;

import android.content.Context;

public class DBManager {
	// DB Helper
	private DBHelper mDBHelper;
	

	// DB Control
	private DBWriter mDBWriter;
	private DBReader mDBReader;
	

	public DBManager(Context context) {
		mDBHelper = new DBHelper(context, null);
		mDBWriter = new DBWriter(mDBHelper);
		mDBReader = new DBReader(mDBHelper);
	}

	public ArrayList<DataFormat> read(int year, int month, int day) {
		return mDBReader.read(year, month, day);
	}

	public ArrayList<DataFormat> read(int year, int month) {
		return mDBReader.read(year, month);
	}

	public void insert(DataFormat data) {
		mDBWriter.insert(data);
	}
	
	public void insertSchedule(DataFormatSchedule data){
		mDBWriter.insertSchedule(data);
	}
	
	public ArrayList<DataFormatSchedule> readSchedule(int year, int month){
		return mDBReader.readSchedules(year, month);
	}
	
	public void deleteSchedule(int year, int month, int position){
		mDBWriter.deleteSchedule(year, month, position);
	}
	
	public void deleteSchedule(int year, int month, int position, int listPosition){
		mDBWriter.deleteSchedule(year, month, position, listPosition);
	}
}
