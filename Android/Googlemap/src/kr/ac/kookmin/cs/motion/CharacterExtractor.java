package kr.ac.kookmin.cs.motion;

import java.util.ArrayList;

import android.util.Log;

public class CharacterExtractor {
	public final static String TAG = "CharacterExtractor";
	public static final int LPC_COEFFICIENT_SIZE = 8;
	public static final int DATA_SEPARATATION_SIZE = 10;
	public static final int DOWN_SAMPLING_PERIOD = 2;

	private static void bubbleSort(ArrayList<Double> array) {
		double temp;
		int arraySize = array.size();
		for (int i = 0; i < arraySize; i++) {
			for (int j = 0; j < arraySize - i - 1; j++) {
				if (array.get(j) > array.get(j + 1)) // i번째 데이터와 j+1번재 데이터를 비교해서
				// j+1번째 데이터가 크면 교환
				{
					temp = array.get(j);
					array.set(i, array.get(j + 1));
					array.set(j + 1, temp);
				}

			}
		}
	}

	public static double getMedian(ArrayList<Double> array) {
		bubbleSort(array);
		int arraySize = array.size();
		if (arraySize == 0)
			return Double.NaN; // 빈 배열은 에러 반환(NaN은 숫자가 아니라는 뜻)
		int center = arraySize / 2; // 요소 개수의 절반값 구하기
		if (arraySize % 2 == 1) { // 요소 개수가 홀수면
			return array.get(center); // 홀수 개수인 배열에서는 중간 요소를 그대로 반환
		} else {
			return (array.get(center - 1) + array.get(center)) / 2.0; // 짝수 개
																		// 요소는,
																		// 중간 두
			// 수의 평균 반환
		}
	}

	public static double[] averageDownSampling(ArrayList<Double> array) {
		int arraySize = array.size();
		int size = arraySize / DOWN_SAMPLING_PERIOD;
		// Log.d(TAG, "arraySize : " + arraySize);
		double[] result = new double[size];
		int count = 0;
		double sum = 0.0;

		for (int i = 0; i < arraySize; i++) {
			++count;
			sum += array.get(i);
			if (count == DOWN_SAMPLING_PERIOD) {
				result[i / DOWN_SAMPLING_PERIOD] = sum / DOWN_SAMPLING_PERIOD;
				count = 0;
				sum = 0;
			}
		}
		//for (int i = 0; i < size; i++)
			//Log.d(TAG, "SamplingData[" + i + "] : " + result[i]);
		return result;
	}

	public static double subtractMaxMin(ArrayList<Double> array) {
		double result = 0.0;
		double max = -1000000000;
		double min = 1000000000;
		double temp = 0.0;
		int arraySize = array.size();

		for (int i = 0; i < arraySize; i++) {
			temp = array.get(i);
			if (max < temp)
				max = temp;
			if (min > temp)
				min = temp;
		}
		result = max - min;
		return result;
	}

	public static double standardDeviation(ArrayList<Double> array, int option) {
		int size = array.size();
		if (size < 2)
			return Double.NaN;
		double sum = 0.0;
		double sd = 0.0;
		double diff;
		double meanValue = average(array);
		for (int i = 0; i < size; i++) {
			diff = array.get(i) - meanValue;
			sum += diff * diff;
		}
		sd = Math.sqrt(sum / (size - option));
		return sd;
	}

	public static double averageDeviation(ArrayList<Double> array) {
		int size = array.size();
		if (size < 2)
			return Double.NaN;
		double sum = 0.0f;
		double sd = 0.0f;
		double diff;
		double meanValue = average(array);
		for (int i = 0; i < size; i++) {
			diff = array.get(i) - meanValue;
			sum += diff * diff;
		}
		sd = sum / size;
		return sd;
	}

	public static double variance(ArrayList<Double> array, int option) {
		int size = array.size();
		if (size < 2)
			return Double.NaN;
		double sum = 0.0f;
		double sd = 0.0f;
		double diff;
		double meanValue = average(array);
		for (int i = 0; i < size; i++) {
			diff = array.get(i) - meanValue;
			sum += diff * diff;
		}
		sd = sum / (size - option);
		return sd;
	}

	public static double sum(ArrayList<Double> array) {
		double sum = 0.0f;
		int size = array.size();
		for (int i = 0; i < size; i++)
			sum += array.get(i);
		return sum;
	}

	public static double average(ArrayList<Double> array) { // 산술 평균 구하기
		float sum = 0.0f;
		int size = array.size();
		for (int i = 0; i < size; i++)
			sum += array.get(i);
		return sum / size;
	}

	public static double rms(ArrayList<Double> array) {
		double ms = 0;
		int size = array.size();
		for (int i = 0; i < size; i++)
			ms += array.get(i) * array.get(i);
		ms /= size;
		return Math.sqrt(ms);
	}

	public static double[] getMfccCoefficients(ArrayList<Double> array) {
		double[] result = new double[MfccAnalyzer.mBinSize];
		for (int i = 0; i < MfccAnalyzer.mBinSize; i++) {
			result[i] = MfccAnalyzer.GetCoefficient(array, i);
		}
		return result;
	}

	public static double[] getCoefficients(int p, ArrayList<Double> array) {
		double r[] = new double[p + 1]; // size = 11
		int N = array.size(); // size = 256
		for (int T = 0; T < r.length; T++) {
			for (int t = 0; t < N - T; t++) {
				r[T] += array.get(t) * array.get(t + T);
			}
		}
		double e = r[0];
		double e1 = 0.0f;
		double k = 0.0f;
		double alpha_new[] = new double[p + 1];
		double alpha_old[] = new double[p + 1];
		alpha_new[0] = 1.0f;
		alpha_old[0] = 1.0f;
		for (int h = 1; h <= p; h++) {
			alpha_new[h] = 0.0f;
			alpha_old[h] = 0.0f;
		}
		double sum = 0.0f;
		for (int i = 1; i <= p; i++) {
			sum = 0;
			for (int j = 1; j <= i - 1; j++) {
				sum += alpha_old[j] * (r[i - j]);
			}
			k = ((r[i]) - sum) / e;
			alpha_new[i] = k;
			for (int c = 1; c <= i - 1; c++) {
				alpha_new[c] = alpha_old[c] - (k * alpha_old[i - c]);
			}
			e1 = (1 - (k * k)) * e;
			for (int g = 0; g <= i; g++) {
				alpha_old[g] = alpha_new[g];
			}
			e = e1;
		}
		for (int a = 1; a < alpha_new.length; a++)
			alpha_new[a] = -1 * alpha_new[a];
		return alpha_new;
	}

}
