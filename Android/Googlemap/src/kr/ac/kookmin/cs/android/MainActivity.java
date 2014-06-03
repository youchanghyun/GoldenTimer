package kr.ac.kookmin.cs.android;

import java.util.ArrayList;

import kr.ac.kookmin.cs.schedule.CalendarMonthViewActivity;
import kr.ac.kookmin.statistics.cs.TabChartActivity;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.example.DBWriter.DBManager;
import com.example.DBWriter.DataFormat;
import com.example.googlemap.HomeRegistActivity;
import com.example.googlemap.MapActivity;
import com.example.googlemap.R;

public class MainActivity extends Activity {

	private static final String TAG = "MainActivity";

	private Button mMapBtn;
	private Button mCalendarBtn;
	private Button mStatisticsBtn;
	private Button mSaveMyLocBtn;
	private Button mStartBtn;
	private Button mDemoBtn;
	private boolean mIsService;

	public boolean isServiceRunningCheck(String name) {
		ActivityManager manager = (ActivityManager) this
				.getSystemService(Activity.ACTIVITY_SERVICE);
		for (RunningServiceInfo service : manager
				.getRunningServices(Integer.MAX_VALUE)) {
			if (name.equals(service.service.getClassName())) {
				return true;
			}
		}
		return false;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		Log.v(TAG,"requestCode ="+requestCode);
		Log.v(TAG,"resultCode ="+resultCode);
		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == 20083259) {
			
				
				Bundle bundle = data.getExtras();
				Log.v(TAG,"전달받은 HomeX : "+bundle.getDouble("latitude") + ", Home Y : " + bundle.getDouble("longitude"));
				writeSettingOptionFile(getApplicationContext(),
						bundle.getDouble("latitude"),
						bundle.getDouble("longitude"));
			}
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		DBManager db = new DBManager(this);
		ArrayList<DataFormat> dataList = db.read(2014, 5, 23);
		for (int i = 0; i < dataList.size(); i++) {
			DataFormat data = dataList.get(i);
			Log.d(TAG,
					"모션 : " + data.getMotionType() + ", 월 : " + data.getMonth());
		}

		mMapBtn = (Button) findViewById(R.id.menu_map_btn);
		mCalendarBtn = (Button) findViewById(R.id.menu_calendar_btn);
		mStatisticsBtn = (Button) findViewById(R.id.menu_statistics_btn);
		mSaveMyLocBtn = (Button) findViewById(R.id.save_my_loc_btn);
		mStartBtn = (Button) findViewById(R.id.start_btn);
		mDemoBtn = (Button) findViewById(R.id.demo_btn);

		// 서비스 검사
		mIsService = isServiceRunningCheck("kr.ac.kookmin.cs.android.BackgroundCollectorService");

		if (mIsService == false) {
			mStartBtn.setBackgroundResource(R.drawable.start_btn_selector);
		} else {
			mStartBtn.setBackgroundResource(R.drawable.stop_btn_selector);
		}

		mMapBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(),
						MapActivity.class);
	
				startActivity(intent);
			}
		});

		mCalendarBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(),
						CalendarMonthViewActivity.class);
				startActivity(intent);
			}
		});

		mStatisticsBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(),
						TabChartActivity.class);
				startActivity(intent);
			}
		});

		mSaveMyLocBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(),
						HomeRegistActivity.class);
				startActivityForResult(intent, 20083259);

			}
		});
		mDemoBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(),
						DemoActivity.class);
				startActivity(intent);
			}
		});
		mStartBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mIsService = isServiceRunningCheck("kr.ac.kookmin.cs.android.BackgroundCollectorService");
				Log.d(TAG, "mIsService : " + mIsService);
				if (mIsService) {
					stopService(new Intent(getApplicationContext(),
							BackgroundCollectorService.class));
					mStartBtn
							.setBackgroundResource(R.drawable.start_btn_selector);

				} else {
					startService(new Intent(getApplicationContext(),
							BackgroundCollectorService.class));
					mStartBtn
							.setBackgroundResource(R.drawable.stop_btn_selector);
				}
			}
		});
	}

	public static void writeSettingOptionFile(final Context context,
			final double latitude, final double longitude) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				SharedPreferences prefs = PreferenceManager
						.getDefaultSharedPreferences(context);
				SharedPreferences.Editor edit = prefs.edit();
				edit.putFloat("latitude", (float) latitude);
				edit.putFloat("longitude", (float) longitude);
				edit.commit();
			}
		}).start();
	}

}

