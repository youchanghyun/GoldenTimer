package kr.ac.kookmin.statistics.cs;

public class MovingPathListData {
	private String pathTime;
	private int pathColor;
	private String pathContent;

	public MovingPathListData(String pathTime, String pathContent) {
		this.pathTime = pathTime;
		this.pathContent = pathContent;
	}
	public MovingPathListData() {
	}
	
	public MovingPathListData(String pathTime, int pathColor, String pathContent) {
		this.pathTime = pathTime;
		this.pathColor = pathColor;
		this.pathContent = pathContent;
	}
	
	public String getPathTime(){
		return this.pathTime;
	}
	
	public int getPathColor(){
		return this.pathColor;
	}
	
	public String getPathContent(){
		return this.pathContent;
	}
	
	public void setPathColor(int pathColor){
		this.pathColor = pathColor;
	}
	
}
