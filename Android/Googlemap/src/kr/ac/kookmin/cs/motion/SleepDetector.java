package kr.ac.kookmin.cs.motion;

import java.util.ArrayList;

import android.util.Log;

public class SleepDetector {
	private final static String TAG = "SleepDetector";
	private final static double CRITICAL_VALUES = 200;

	public static int detect(ArrayList<Double> data) {
		int result = 0;

		double sum = 0;
		int size = data.size();
		for (int i = 0; i < size; i++) {
			sum += data.get(i);
			
		}
		Log.d(TAG, "수면여부 Sum : " + sum);
		if (sum < CRITICAL_VALUES)
			result = 1;

		return result;

	}
}
