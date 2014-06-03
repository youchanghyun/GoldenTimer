package kr.ac.kookmin.cs.motion;

import android.hardware.SensorManager;

public class MotionOption {

	// ���ǹ� ������ �Ӱ谪
	public static final double PREPROCESSING_CRITICAL_VALUE = 10.0;
	public static final int ACCEL_PERIOD_TIME = 12000;
	// ���� �н������� ���� �ð�
	public static final int COLLECT_DATA_TIME = 30000;
	// �з� ������ ���� �ð�
	public static final int COLLECT_CLASSIFICATION_DATA_TIME = 3000;
	// ���� ������ ���� ���� ������ ������
	public static final int LEARN_DELAY_TIME = 4000;
	//public static final int LEARN_DATA_TIME = 1500;
	public static final int SENSOR_DELAY = SensorManager.SENSOR_DELAY_FASTEST;
	public static final int MOVING_FILTER_PERIOD = 5;
}
