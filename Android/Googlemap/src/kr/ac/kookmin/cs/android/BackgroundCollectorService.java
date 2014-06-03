package kr.ac.kookmin.cs.android;

import java.util.Date;
import java.util.Timer;

import kr.ac.kookmin.cs.motion.MotionClassifier;
import kr.ac.kookmin.cs.motion.MotionOption;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.util.Log;

import com.example.googlemap.R;

public class BackgroundCollectorService extends Service {
	public final static String TAG = "BackgroundCollector";
	
	
	// 노티 넘버
	private final static int NOTI_ID = 1111;

	// 가속도 수집기
	private MotionClassifier mAccelCollector;

	// 센서 매니저
	private SensorManager mSm;
	
	// 통지
	private Notification mNoti;

	
	private Timer mTimer;

	public BackgroundCollectorService() {
		super();
		Log.d(TAG, "BackgroundCollector() 호출");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d(TAG, "onStartCommand 호출");

		// 생성
		mAccelCollector = new MotionClassifier();
		mSm = (SensorManager) getSystemService(SENSOR_SERVICE);
		mNoti = new Notification(R.drawable.motion_diary_logo, "Motion Diary...", System.currentTimeMillis());
		mNoti.flags = Notification.FLAG_ONGOING_EVENT;
		mNoti.defaults = Notification.DEFAULT_SOUND;
		mNoti.number = 13;
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
		mNoti.setLatestEventInfo(this, "Motion Diary", "Going...", pendingIntent);
		
		
		
		
		// 통지
		NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		manager.notify(NOTI_ID, mNoti);
		
		// 스레드 실행
		BackgroundTimer timeTask = new BackgroundTimer(getApplicationContext() );
		mTimer = new Timer();
		mTimer.scheduleAtFixedRate(timeTask, new Date(), 10000);

		// 센서 등록
		mSm.registerListener(mAccelCollector,
				mSm.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION),
				MotionOption.SENSOR_DELAY);

		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		Log.d(TAG, "BackgroundCollector onDestroy 호출");
		
		// 타이머 정지
		mTimer.cancel();
		
		// Noti 해제
		NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		manager.cancel(NOTI_ID);
		
		// 센서 해제
		mSm.unregisterListener(mAccelCollector);

		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

}
