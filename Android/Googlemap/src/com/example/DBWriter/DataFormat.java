package com.example.DBWriter;

public class DataFormat {
	private int MotionType;
	private int IsSleep;
	private double Latitude;
	private double Longitude;
	private long CurSysTime;
	private int Year;
	private int Month;
	private int Day;
	private String Address;
	public DataFormat(int motionType, int isSleep, double latitude,
			double longitude, long curSysTime, int year, int month, int day,
			String address) {
		super();
		MotionType = motionType;
		IsSleep = isSleep;
		Latitude = latitude;
		Longitude = longitude;
		CurSysTime = curSysTime;
		Year = year;
		Month = month;
		Day = day;
		Address = address;
	}
	public int getMotionType() {
		return MotionType;
	}
	public void setMotionType(int motionType) {
		MotionType = motionType;
	}
	public int getIsSleep() {
		return IsSleep;
	}
	public void setIsSleep(int isSleep) {
		IsSleep = isSleep;
	}
	public double getLatitude() {
		return Latitude;
	}
	public void setLatitude(double latitude) {
		Latitude = latitude;
	}
	public double getLongitude() {
		return Longitude;
	}
	public void setLongitude(double longitude) {
		Longitude = longitude;
	}
	public long getCurSysTime() {
		return CurSysTime;
	}
	public void setCurSysTime(long curSysTime) {
		CurSysTime = curSysTime;
	}
	public int getYear() {
		return Year;
	}
	public void setYear(int year) {
		Year = year;
	}
	public int getMonth() {
		return Month;
	}
	public void setMonth(int month) {
		Month = month;
	}
	public int getDay() {
		return Day;
	}
	public void setDay(int day) {
		Day = day;
	}
	public String getAddress() {
		return Address;
	}
	public void setAddress(String address) {
		Address = address;
	}
	
}
