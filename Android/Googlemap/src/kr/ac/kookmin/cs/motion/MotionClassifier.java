package kr.ac.kookmin.cs.motion;

import java.util.LinkedList;
import java.util.Queue;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Handler;
import android.util.Log;

public class MotionClassifier implements SensorEventListener {
	public final static String TAG = "AccelDataCollector";
	// ���ӵ� ������
	private AccelData mAccelData;

	// ��ó�� ������
	private Queue<Double> mPreprocessedData;


	// ������ ��ó��
	private boolean mIsCheckMeaningful = false;
	private boolean mIsCheckDelay = false;

	// Ÿ�� �ڵ鷯
	private Handler mTimeHandler;

	public MotionClassifier() {
		mTimeHandler = new Handler();
		mAccelData = new AccelData(MotionOption.MOVING_FILTER_PERIOD);
		mPreprocessedData = new LinkedList<Double>();
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		float[] v = event.values;

		switch (event.sensor.getType()) {
		case Sensor.TYPE_LINEAR_ACCELERATION:
			// x, y, z �� �̵���� ���� ����
			mAccelData.x.newNum(v[0]);
			mAccelData.y.newNum(v[1]);
			mAccelData.z.newNum(v[2]);

			// �̵���� ������
			float filteredX = mAccelData.x.getAvg();
			float filteredY = mAccelData.y.getAvg();
			float filteredZ = mAccelData.z.getAvg();

			// ���ǹ� ������ üũ
			if (mIsCheckMeaningful == false && mIsCheckDelay == false) {
				if (DataPreprocessor.checkMeaningfulData(filteredX, filteredY, filteredZ)) {
					Log.d(TAG, "���ǹ� ������ �߰�");
					mIsCheckMeaningful = true;
					mIsCheckDelay = true;
					mTimeHandler.postDelayed(new Runnable() {
						@Override
						public void run() {
							// ������ ��ó�� ������ Ư¡ ����
							mIsCheckMeaningful = false;
							
							// Ư¡ ���� �з��ϱ�
							
							// ������ ����
							resetData();
						}
					}, MotionOption.COLLECT_DATA_TIME);
					mTimeHandler.postDelayed(new Runnable() {
						@Override
						public void run() {
							mIsCheckDelay = false;
						}
					}, MotionOption.ACCEL_PERIOD_TIME);
				}
			}

			// ������ ��ó��
			if (mIsCheckMeaningful == true) {
				Log.d(TAG, "������ ��ó��...");
				mPreprocessedData.add(DataPreprocessor.proprocessData(filteredX, filteredY,
						filteredZ));
			}

			break;

		}
	}
	
	// ������ ����
	private void resetData(){
		mPreprocessedData.clear();
		mAccelData.x.clear();
		mAccelData.y.clear();
		mAccelData.z.clear();
		
	}


}
