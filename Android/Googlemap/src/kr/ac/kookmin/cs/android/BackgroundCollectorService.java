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
	
	
	// ��Ƽ �ѹ�
	private final static int NOTI_ID = 1111;

	// ���ӵ� ������
	private MotionClassifier mAccelCollector;

	// ���� �Ŵ���
	private SensorManager mSm;
	
	// ����
	private Notification mNoti;

	
	private Timer mTimer;

	public BackgroundCollectorService() {
		super();
		Log.d(TAG, "BackgroundCollector() ȣ��");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d(TAG, "onStartCommand ȣ��");

		// ����
		mAccelCollector = new MotionClassifier();
		mSm = (SensorManager) getSystemService(SENSOR_SERVICE);
		mNoti = new Notification(R.drawable.motion_diary_logo, "Motion Diary...", System.currentTimeMillis());
		mNoti.flags = Notification.FLAG_ONGOING_EVENT;
		mNoti.defaults = Notification.DEFAULT_SOUND;
		mNoti.number = 13;
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
		mNoti.setLatestEventInfo(this, "Motion Diary", "Going...", pendingIntent);
		
		
		
		
		// ����
		NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		manager.notify(NOTI_ID, mNoti);
		
		// ������ ����
		BackgroundTimer timeTask = new BackgroundTimer(getApplicationContext() );
		mTimer = new Timer();
		mTimer.scheduleAtFixedRate(timeTask, new Date(), 10000);

		// ���� ���
		mSm.registerListener(mAccelCollector,
				mSm.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION),
				MotionOption.SENSOR_DELAY);

		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		Log.d(TAG, "BackgroundCollector onDestroy ȣ��");
		
		// Ÿ�̸� ����
		mTimer.cancel();
		
		// Noti ����
		NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		manager.cancel(NOTI_ID);
		
		// ���� ����
		mSm.unregisterListener(mAccelCollector);

		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

}
