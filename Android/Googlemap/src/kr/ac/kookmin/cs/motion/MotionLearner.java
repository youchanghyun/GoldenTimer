package kr.ac.kookmin.cs.motion;

import java.util.LinkedList;
import java.util.Queue;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Handler;
import android.util.Log;

public class MotionLearner implements SensorEventListener {
	public final static String TAG = "MotionLearner";
	// ���ӵ� ������
	private AccelData mAccelData;

	// ��ó�� ������
	public Queue<Double> mPreprocessedData;

	// Ÿ�� �ڵ鷯
	private Handler mTimeHandler;

	public MotionLearner() {
		mTimeHandler = new Handler();
		mAccelData = new AccelData(MotionOption.MOVING_FILTER_PERIOD);
		mPreprocessedData = new LinkedList<Double>();
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		Log.d(TAG, "onAccuracyChanged ȣ��");
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		//Log.d(TAG, "onSensorChanged ȣ��");
		float[] v = event.values;

		switch (event.sensor.getType()) {
		case Sensor.TYPE_LINEAR_ACCELERATION:

			// Log.d(TAG, "MotionLearner onSensorChanged ȣ��");
			// x, y, z �� �̵���� ���� ����
			mAccelData.x.newNum(v[0]);
			mAccelData.y.newNum(v[1]);
			mAccelData.z.newNum(v[2]);

			// �̵���� ������
			float filteredX = mAccelData.x.getAvg();
			float filteredY = mAccelData.y.getAvg();
			float filteredZ = mAccelData.z.getAvg();

			// ������ ��ó��
			//Log.d(TAG, "MotionLearner x : " + filteredX + ", y : " + filteredY + ", z : " + filteredZ);
			try {
				mPreprocessedData.add(DataPreprocessor.proprocessData(
						filteredX, filteredY, filteredZ));
			} catch (Exception e) {
				e.printStackTrace();
			}

			break;

		}
	}

	// ������ ����
	public void resetData() {
		mPreprocessedData.clear();
		mAccelData.x.clear();
		mAccelData.y.clear();
		mAccelData.z.clear();

	}
}
