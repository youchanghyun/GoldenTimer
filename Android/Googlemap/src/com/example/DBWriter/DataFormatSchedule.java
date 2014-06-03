package com.example.DBWriter;
public class DataFormatSchedule {
	private String time;
	private int year;
	private int month;
	private int position;
	private int listPosition;
	private String memo;

	public DataFormatSchedule(String time, int year, int month, int position,
			int listPosition, String memo) {
		this.time = time;
		this.year = year;
		this.month = month;
		this.position = position;
		this.listPosition = listPosition;
		this.memo = memo;
	}

	public String getTime() {
		return this.time;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}
	
	public int getListPosition() {
		return this.listPosition;
	}

	public void setListPosition(int listPosition) {
		this.listPosition = listPosition;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public void setTime(String time) {
		this.time = time;
	}

}