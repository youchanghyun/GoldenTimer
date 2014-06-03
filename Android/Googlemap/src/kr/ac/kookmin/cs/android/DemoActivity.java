package kr.ac.kookmin.cs.android;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Queue;

import kr.ac.kookmin.cs.motion.CharacterExtractor;
import kr.ac.kookmin.cs.motion.MotionLearner;
import kr.ac.kookmin.cs.motion.MotionOption;
import kr.ac.kookmin.cs.svm.FileManager;
import kr.ac.kookmin.cs.svm.LearningData;
import kr.ac.kookmin.cs.svm.PredictOutputFormat;
import kr.ac.kookmin.cs.svm.SVMManager;
import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.googlemap.R;
import com.jjoe64.graphview.BarGraphView;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphViewDataInterface;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.GraphViewSeries.GraphViewSeriesStyle;
import com.jjoe64.graphview.ValueDependentColor;

public class DemoActivity extends Activity {
	public final static String TAG = "ClassificationActivity";
	private Button mStartClassificationBtn;

	private GraphView mMotionGraphView;
	private GraphViewSeries mMotionSerires;
	private GraphViewSeriesStyle mGraphStyle;

	// 센서
	private MotionLearner mMotionLearner;
	private SensorManager mSm;

	// SVM 매니저
	private SVMManager mSVMManger;

	// 파일 매니저
	private FileManager mFileManager;
	// 프로그레스 다이얼로그
	private ProgressDialog mMotionProgress;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_demo);

		mStartClassificationBtn = (Button) findViewById(R.id.classify_motion_btn);
		// mShowResultTv = (TextView)
		// findViewById(R.id.show_classified_motion_tv);
		mSVMManger = new SVMManager();
		mFileManager = new FileManager();
		mMotionProgress = new ProgressDialog(this);
		mSm = (SensorManager) getSystemService(SENSOR_SERVICE);
		mMotionLearner = new MotionLearner();

		// 프로그레스 세팅
		mMotionProgress.setTitle("분류 진행중");
		mMotionProgress.setMessage("분류가 진행중 입니다.");
		mMotionProgress.setIndeterminate(false);
		mMotionProgress.setCancelable(false);

		mStartClassificationBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mMotionGraphView.removeAllSeries();

				mMotionProgress.show();

				// 특징 추출
				Log.d(TAG, "특징 추출전");
				handleData(FileManager.CLASSIFICATION_MOTION_DATA_FILE);

				mMotionGraphView.addSeries(mMotionSerires);

			}
		});

		mMotionGraphView = new BarGraphView(this // context
				, "Motion Graph" // heading
		);
		mMotionGraphView.setManualYAxisBounds(1, 0);
		GraphViewSeriesStyle seriesStyle = new GraphViewSeriesStyle();
		seriesStyle.setValueDependentColor(new ValueDependentColor() {
			@Override
			public int get(GraphViewDataInterface data) {
				// the higher the more red
				return Color.rgb((int) (150 + ((data.getY() / 3) * 100)),
						(int) (150 - ((data.getY() / 3) * 150)),
						(int) (150 - ((data.getY() / 3) * 150)));
			}
		});
		mMotionSerires = new GraphViewSeries("aaa", seriesStyle,
				new GraphViewData[] { new GraphViewData(1, 0),
						new GraphViewData(2, 0), new GraphViewData(3, 0) });
		mMotionGraphView.addSeries(mMotionSerires);

		mMotionGraphView.setHorizontalLabels(new String[] { "Standing",
				"Walking", "Running" });
		LinearLayout layout = (LinearLayout) findViewById(R.id.motion_graph);
		layout.addView(mMotionGraphView);
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

	private LearningData extractCharacter(Queue<Double> data) {
		ArrayList<Double> array = changeQueueToArrayList(data);
		LearningData result = new LearningData();
		result.Average = CharacterExtractor.average(array);
		result.AverageDeviation = CharacterExtractor.averageDeviation(array);
		result.Rms = CharacterExtractor.rms(array);
		result.StandardDeviation = CharacterExtractor.standardDeviation(array,
				0);
		result.MaxMin = CharacterExtractor.subtractMaxMin(array);
		result.Median = CharacterExtractor.getMedian(array);

		return result;
	}

	// 핸들링
	private void handleData(final String fileName) {
		Log.d(TAG, "handleData 호출");
		// 3초 후 3초간 데이터 수집
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				Log.d(TAG, "센서 측정 시작");

				boolean sensorError = mSm.registerListener(mMotionLearner,
						mSm.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION),
						MotionOption.SENSOR_DELAY);

				Log.d(TAG, "sensorError : " + !sensorError);

				new Handler().postDelayed(new Runnable() {

					@Override
					public void run() {
						Log.d(TAG, "센서 측정 완료");
						LearningData learningData = new LearningData();
						// 센서 해제
						mSm.unregisterListener(mMotionLearner);

						// 특징 추출
						Log.d(TAG, "전처리된 센서데이터 수 : "
								+ mMotionLearner.mPreprocessedData.size());
						if (mMotionLearner.mPreprocessedData.size() == 0) {
							Toast.makeText(getApplicationContext(), "센서 에러...",
									Toast.LENGTH_LONG).show();
						}
						learningData = extractCharacter(mMotionLearner.mPreprocessedData);

						// 파일 저장
						try {
							mFileManager.makeInputFile(fileName, learningData);
						} catch (IOException e1) {
							e1.printStackTrace();
							Toast.makeText(getApplicationContext(),
									"파일이 존재하지 않거나 잘못되었습니다.",
									Toast.LENGTH_LONG).show();
						}

						// 데이터 리셋
						mMotionLearner.resetData();

						// 프로그래스 해제
						mMotionProgress.dismiss();

						PredictOutputFormat result = new PredictOutputFormat();
						try {
							mSVMManger
									.scale(FileManager.CLASSIFICATION_MOTION_DATA_FILE,
											FileManager.RESTORE_MOTION_DATA_FILE,
											null,
											FileManager.SCALED_CLASSIFICATION_MOTION_DATA_FILE);
							result = mSVMManger
									.predict(
											FileManager.SCALED_CLASSIFICATION_MOTION_DATA_FILE,
											FileManager.SVM_TRAIN_MODEL_FILE);
						} catch (IOException e) {
							e.printStackTrace();
							Toast.makeText(getApplicationContext(),
									"파일이 존재하지 않거나 잘못되었습니다.",
									Toast.LENGTH_LONG).show();
						}
						Log.d(TAG, "prediect : " + result.predict);
						for (int i = 0; i < result.labels.length; i++) {
							Log.d(TAG, "label : " + result.labels[i] + ", b : "
									+ result.prob_estimates[i]);
						}

						GraphViewData standingData = null, walkingData = null, runningData = null;

						for (int i = 0; i < result.labels.length; i++) {
							switch (result.labels[i]) {
							case 1:
								standingData = new GraphViewData(2,
										result.prob_estimates[i]);
								break;
							case 2:
								walkingData = new GraphViewData(2,
										result.prob_estimates[i]);
								break;
							case 3:
								runningData = new GraphViewData(2,
										result.prob_estimates[i]);
								break;

							}
						}
						mMotionSerires.resetData(new GraphViewData[] {
								standingData, walkingData, runningData });

					}
				}, MotionOption.COLLECT_CLASSIFICATION_DATA_TIME);
			}
		}, MotionOption.LEARN_DELAY_TIME);
	}



}
