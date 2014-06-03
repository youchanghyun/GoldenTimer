package kr.ac.kookmin.cs.motion;

import android.hardware.SensorManager;

public class MotionOption {

	// 유의미 데이터 임계값
	public static final double PREPROCESSING_CRITICAL_VALUE = 10.0;
	public static final int ACCEL_PERIOD_TIME = 12000;
	// 센서 학습데이터 수집 시간
	public static final int COLLECT_DATA_TIME = 30000;
	// 분류 데이터 수집 시간
	public static final int COLLECT_CLASSIFICATION_DATA_TIME = 3000;
	// 시작 누르고 센서 수집 전까지 딜레이
	public static final int LEARN_DELAY_TIME = 4000;
	//public static final int LEARN_DATA_TIME = 1500;
	public static final int SENSOR_DELAY = SensorManager.SENSOR_DELAY_FASTEST;
	public static final int MOVING_FILTER_PERIOD = 5;
}
