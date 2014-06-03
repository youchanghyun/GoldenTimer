package kr.ac.kookmin.cs.android;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Queue;
import java.util.TimerTask;

import kr.ac.kookmin.cs.motion.CharacterExtractor;
import kr.ac.kookmin.cs.motion.MotionLearner;
import kr.ac.kookmin.cs.motion.MotionOption;
import kr.ac.kookmin.cs.motion.SleepDetector;
import kr.ac.kookmin.cs.svm.FileManager;
import kr.ac.kookmin.cs.svm.LearningData;
import kr.ac.kookmin.cs.svm.PredictOutputFormat;
import kr.ac.kookmin.cs.svm.SVMManager;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.example.DBWriter.DBManager;
import com.example.DBWriter.DataFormat;

public class BackgroundTimer extends TimerTask {
	private final static String TAG = "BackgroundTimer";
	private SensorManager mSm;
	private MotionLearner mMotionLearner;
	private Context mContext;

	// ���� �Ŵ���
	private FileManager mFileManager;
	// SVM �Ŵ���
	private SVMManager mSVMManger;

	// DB
	private DBManager mDBManager;

	// ����
	private LocationFinder mLocFinder;

	public BackgroundTimer(Context context) {
		mContext = context;
		mSm = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
		mMotionLearner = new MotionLearner();
		mFileManager = new FileManager();
		mSVMManger = new SVMManager();
		mLocFinder = new LocationFinder(context);
		mDBManager = new DBManager(context);
	}

	@Override
	public void run() {
		Log.d(TAG, "Ÿ�̸� ȣ��...");
		handleData(FileManager.CLASSIFICATION_MOTION_DATA_FILE);

	}

	// Queue -> ArrayList
	private ArrayList<Double> changeQueueToArrayList(Queue<Double> data) {
		ArrayList<Double> result = new ArrayList<Double>();

		while (data.isEmpty() == false) {

			try {
				result.add(data.remove());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	private LearningData extractCharacter(ArrayList<Double> data) {
		LearningData result = new LearningData();
		result.Average = CharacterExtractor.average(data);
		result.AverageDeviation = CharacterExtractor.averageDeviation(data);
		result.Rms = CharacterExtractor.rms(data);
		result.StandardDeviation = CharacterExtractor
				.standardDeviation(data, 0);
		result.MaxMin = CharacterExtractor.subtractMaxMin(data);
		result.Median = CharacterExtractor.getMedian(data);

		return result;
	}

	// �ڵ鸵
	private void handleData(final String fileName) {
		Log.d(TAG, "handleData ȣ��");
		Log.d(TAG, "���� ���� ����");

		mSm.registerListener(mMotionLearner,
				mSm.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION),
				MotionOption.SENSOR_DELAY);

		new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {

			@Override
			public void run() {
				Log.d(TAG, "���� ���� �Ϸ�");
				// DB ���� ������
				Calendar calendar = Calendar.getInstance();

				int motionType, isSleep;
				double latitude, longitude;
				long curSysTime = System.currentTimeMillis();
				int year = calendar.get(Calendar.YEAR), month = calendar
						.get(Calendar.MONTH)+1, day = calendar.get(Calendar.DATE);
				String address;

				LearningData learningData = new LearningData();
				// ���� ����
				mSm.unregisterListener(mMotionLearner);

				// Queue -> ArrayLise
				ArrayList<Double> arrListData = changeQueueToArrayList(mMotionLearner.mPreprocessedData);

				// ���� ���� �Ǵ�
				isSleep = SleepDetector.detect(arrListData);
				Log.d(TAG, "���� ���� : " + isSleep);

				// Ư¡ ����
				Log.d(TAG, "��ó���� ���������� �� : " + arrListData.size());
				if (arrListData.size() == 0) {
					Toast.makeText(mContext, "���� ����...", Toast.LENGTH_LONG)
							.show();
				}
				learningData = extractCharacter(arrListData);

				// ���� ����
				try {
					mFileManager.makeInputFile(fileName, learningData);
				} catch (IOException e1) {
					e1.printStackTrace();
					Toast.makeText(mContext, "������ �������� �ʰų� �߸��Ǿ����ϴ�.",
							Toast.LENGTH_LONG).show();
				}

				// ������ ����
				mMotionLearner.resetData();

				PredictOutputFormat result = new PredictOutputFormat();
				try {
					mSVMManger.scale(
							FileManager.CLASSIFICATION_MOTION_DATA_FILE,
							FileManager.RESTORE_MOTION_DATA_FILE, null,
							FileManager.SCALED_CLASSIFICATION_MOTION_DATA_FILE);
					result = mSVMManger.predict(
							FileManager.SCALED_CLASSIFICATION_MOTION_DATA_FILE,
							FileManager.SVM_TRAIN_MODEL_FILE);
				} catch (IOException e) {
					e.printStackTrace();
					Toast.makeText(mContext, "������ �������� �ʰų� �߸��Ǿ����ϴ�.",
							Toast.LENGTH_LONG).show();
				}
				Log.d(TAG, "prediect : " + result.predict);
				for (int i = 0; i < result.labels.length; i++) {
					Log.d(TAG, "label : " + result.labels[i] + ", b : "
							+ result.prob_estimates[i]);
				}

				// ��� ���
				motionType = (int) result.predict;

				// ��ġ ����
				Location loc = mLocFinder.getLocation();
				if(loc != null){
					latitude = loc.getLatitude();
					longitude = loc.getLongitude();
					address = mLocFinder.getGeoLocation(loc).toString();
					Log.d(TAG, "���� : " + latitude + ", �浵 : " + longitude);
					Log.d(TAG, "�ּ� : " + address);

					// DB ����
					Log.d(TAG, "�� : " + year + ", �� : " + month + ", �� : " + day);
					mDBManager.insert(new DataFormat(motionType, isSleep, latitude,
							longitude, curSysTime, year, month, day, address));
				}
				else{
					Log.d(TAG, "Location ���� null...��� ����ȵ�");
					Toast.makeText(mContext,"Location ���� null...��� ����ȵ�",Toast.LENGTH_SHORT).show();
				}
				
			}
		}, MotionOption.COLLECT_CLASSIFICATION_DATA_TIME);
	}
}
