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

	// 파일 매니저
	private FileManager mFileManager;
	// SVM 매니저
	private SVMManager mSVMManger;

	// DB
	private DBManager mDBManager;

	// 위지
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
		Log.d(TAG, "타이머 호출...");
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

	// 핸들링
	private void handleData(final String fileName) {
		Log.d(TAG, "handleData 호출");
		Log.d(TAG, "센서 측정 시작");

		mSm.registerListener(mMotionLearner,
				mSm.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION),
				MotionOption.SENSOR_DELAY);

		new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {

			@Override
			public void run() {
				Log.d(TAG, "센서 측정 완료");
				// DB 넣을 데이터
				Calendar calendar = Calendar.getInstance();

				int motionType, isSleep;
				double latitude, longitude;
				long curSysTime = System.currentTimeMillis();
				int year = calendar.get(Calendar.YEAR), month = calendar
						.get(Calendar.MONTH)+1, day = calendar.get(Calendar.DATE);
				String address;

				LearningData learningData = new LearningData();
				// 센서 해제
				mSm.unregisterListener(mMotionLearner);

				// Queue -> ArrayLise
				ArrayList<Double> arrListData = changeQueueToArrayList(mMotionLearner.mPreprocessedData);

				// 수면 여부 판단
				isSleep = SleepDetector.detect(arrListData);
				Log.d(TAG, "수면 여부 : " + isSleep);

				// 특징 추출
				Log.d(TAG, "전처리된 센서데이터 수 : " + arrListData.size());
				if (arrListData.size() == 0) {
					Toast.makeText(mContext, "센서 에러...", Toast.LENGTH_LONG)
							.show();
				}
				learningData = extractCharacter(arrListData);

				// 파일 저장
				try {
					mFileManager.makeInputFile(fileName, learningData);
				} catch (IOException e1) {
					e1.printStackTrace();
					Toast.makeText(mContext, "파일이 존재하지 않거나 잘못되었습니다.",
							Toast.LENGTH_LONG).show();
				}

				// 데이터 리셋
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
					Toast.makeText(mContext, "파일이 존재하지 않거나 잘못되었습니다.",
							Toast.LENGTH_LONG).show();
				}
				Log.d(TAG, "prediect : " + result.predict);
				for (int i = 0; i < result.labels.length; i++) {
					Log.d(TAG, "label : " + result.labels[i] + ", b : "
							+ result.prob_estimates[i]);
				}

				// 모션 결과
				motionType = (int) result.predict;

				// 위치 조사
				Location loc = mLocFinder.getLocation();
				if(loc != null){
					latitude = loc.getLatitude();
					longitude = loc.getLongitude();
					address = mLocFinder.getGeoLocation(loc).toString();
					Log.d(TAG, "위도 : " + latitude + ", 경도 : " + longitude);
					Log.d(TAG, "주소 : " + address);

					// DB 저장
					Log.d(TAG, "년 : " + year + ", 월 : " + month + ", 일 : " + day);
					mDBManager.insert(new DataFormat(motionType, isSleep, latitude,
							longitude, curSysTime, year, month, day, address));
				}
				else{
					Log.d(TAG, "Location 조사 null...디비 저장안됨");
					Toast.makeText(mContext,"Location 조사 null...디비 저장안됨",Toast.LENGTH_SHORT).show();
				}
				
			}
		}, MotionOption.COLLECT_CLASSIFICATION_DATA_TIME);
	}
}
