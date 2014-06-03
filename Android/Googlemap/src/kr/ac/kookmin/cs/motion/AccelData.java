package kr.ac.kookmin.cs.motion;

public class AccelData {

	// x, y, z�� ������
	public MovingAverageFilter x;
	public MovingAverageFilter y;
	public MovingAverageFilter z;
	public AccelData(int period) {
		x = new MovingAverageFilter(period);
		y = new MovingAverageFilter(period);
		z = new MovingAverageFilter(period);
	}
	
	
}
