package com.example.googlemap;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.example.DBWriter.DBManager;
import com.example.DBWriter.DataFormat;
import com.example.googlemap.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

public class MapActivity extends FragmentActivity {
	private static final String TAG = "LogTest";
	private static final int act_stop = 1;
	private static final int act_walk = 2;
	private static final int act_run = 3;

	public GoogleMap mGoogleMap;
	Polyline polyline;
	DecimalFormat df = new DecimalFormat();

	DBManager db;

	public int sleepCount = 0;

	ArrayList<DataFormat> result = new ArrayList<DataFormat>();
	public ArrayList<PolylineOptions> ops = new ArrayList<PolylineOptions>();

	public int index = 0;
	public int startIndex = 0;
	public int endIndex = 0;

	public double myHomeX;
	public double myHomeY;
	public double distanceToHome;

	public boolean homeFlag = false;

	LatLng loc = new LatLng(37.58660, 126.94352); // LatLng는 위도 경도 갖는 클래스
	CameraPosition cp = new CameraPosition.Builder().target((loc)).zoom(18)
			.build();
	public ArrayList<MarkerOptions> marker = new ArrayList<MarkerOptions>();

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);

		db = new DBManager(MapActivity.this);

		Calendar cal = Calendar.getInstance();
		marker.add(new MarkerOptions());

		mGoogleMap = ((SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map)).getMap(); // 화면에 구글맵 표시

		mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cp)); // 지정위치로
																				// 이동
		ops.clear(); // 기존에 쌓여있을지 모르는 Polyline clear

		Bundle bundle = getIntent().getExtras();

		// 집주소 읽기
		HomeLocation homeLoc = readSettingOptionFile(getApplicationContext());
		myHomeX = homeLoc.x;
		myHomeY = homeLoc.y;
		Log.v(TAG, "myHomeX= " + myHomeX + "myHomeY= " + myHomeY);

		// Main
// cal.get(Calendar.YEAR)
		if (bundle == null) {
			result = db
					.read(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1,
							cal.get(Calendar.DAY_OF_MONTH));
		} else {
			result = db.read(bundle.getInt("curyear"),
					bundle.getInt("curmonth"), bundle.getInt("curday"));
		}

		// 처음 화면과 Zoom-level을 설정.
		if (result.size() < 10) {
			CameraPosition cp = new CameraPosition.Builder()
					.target((new LatLng(result.get(0).getLatitude(), result
							.get(0).getLongitude()))).zoom(17).build();
			mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cp));
		} else {
			CameraPosition cp = new CameraPosition.Builder()
					.target((new LatLng(result.get(result.size() - 1)
							.getLatitude(), result.get(result.size() - 1)
							.getLongitude()))).zoom(15).build();
			mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cp));
		}
		Log.v(TAG, "result.size() = " + result.size());

		// 선택한 Day의 데이터를 바탕으로 행동별로 선을 그려주는 부분
		ops.add(new PolylineOptions());
		for (int i = 0; i < result.size(); i++) {
			if (i == 0) {
				switch (result.get(i).getMotionType()) {
				case act_walk:
					mGoogleMap.addPolyline(ops
							.get(index)
							.add(new LatLng(result.get(i).getLatitude(), result
									.get(i).getLongitude())).color(0x99FF5A5A));
					break;
				case act_run:
					mGoogleMap.addPolyline(ops
							.get(index)
							.add(new LatLng(result.get(i).getLatitude(), result
									.get(i).getLongitude())).color(0x9900C6ED));
					break;
				case act_stop:
					mGoogleMap.addPolyline(ops.get(index).add(
							new LatLng(result.get(i).getLatitude(), result.get(
									i).getLongitude())));
					break;
				default:
					break;
				}
				index++;
				ops.add(new PolylineOptions());
				continue;
			}
			switch (result.get(i).getMotionType()) {
			case act_walk:
				mGoogleMap.addPolyline(ops
						.get(index)
						.add(new LatLng(result.get(i - 1).getLatitude(), result
								.get(i - 1).getLongitude())).color(0x99FF5A5A));
				mGoogleMap.addPolyline(ops
						.get(index)
						.add(new LatLng(result.get(i).getLatitude(), result
								.get(i).getLongitude())).color(0x99FF5A5A));
				break;
			case act_run:
				mGoogleMap.addPolyline(ops
						.get(index)
						.add(new LatLng(result.get(i - 1).getLatitude(), result
								.get(i - 1).getLongitude())).color(0x9900C6ED));
				mGoogleMap.addPolyline(ops
						.get(index)
						.add(new LatLng(result.get(i).getLatitude(), result
								.get(i).getLongitude())).color(0x9900C6ED));
				break;
			case act_stop:
				mGoogleMap.addPolyline(ops.get(index).add(
						new LatLng(result.get(i - 1).getLatitude(), result.get(
								i - 1).getLongitude())));
				mGoogleMap.addPolyline(ops.get(index).add(
						new LatLng(result.get(i).getLatitude(), result.get(i)
								.getLongitude())));
				break;
			default:
				break;
			}
			ops.add(new PolylineOptions());
			index++;
		}

		// 선택한 Day의 데이터를 바탕으로 행동별로 마커와 이동 시간을 그려주는 부분
		for (int i = 0; i < result.size(); i++) {
			if(result.get(i).getIsSleep() == 1)
				continue;
			if (i == 0) {
				startIndex = 0;
				continue;
			}
			if (i == result.size() - 1) {
				endIndex = i;
				switch (result.get(i - 1).getMotionType()) {
				case act_walk:
					mGoogleMap.addMarker(new MarkerOptions()
							.title("걷기")
							.snippet((endIndex - startIndex)*10 + "초 간 걸으셨습니다.")
							.position(
									new LatLng(result.get(i - 1).getLatitude(),
											result.get(i - 1).getLongitude()))
							.icon(BitmapDescriptorFactory
									.fromResource(R.drawable.walking)));
					break;
				case act_run:
					mGoogleMap.addMarker(new MarkerOptions()
							.title("뛰기")
							.snippet((endIndex - startIndex)*10 + "초 간 뛰셨습니다.")
							.position(
									new LatLng(result.get(i - 1).getLatitude(),
											result.get(i - 1).getLongitude()))
							.icon(BitmapDescriptorFactory
									.fromResource(R.drawable.running)));
					break;
				case act_stop:
					mGoogleMap.addMarker(new MarkerOptions()
							.title("서기")
							.snippet((endIndex - startIndex)*10 + "초 간 서있었습니다.")
							.position(
									new LatLng(result.get(i - 1).getLatitude(),
											result.get(i - 1).getLongitude()))
							.icon(BitmapDescriptorFactory
									.fromResource(R.drawable.stop)));
					break;
				default:
					break;
				}
				startIndex = 0;
				endIndex = 0;
				break;
			}
			if (result.get(i).getMotionType() == result.get(i - 1)
					.getMotionType())
				continue;
			else {
				endIndex = i;
				switch (result.get(i - 1).getMotionType()) {
				case act_walk:
					mGoogleMap.addMarker(new MarkerOptions()
							.title("걷기")
							.snippet((endIndex - startIndex)*10 + "초 간 걸으셨습니다.")
							.position(
									new LatLng(result.get(i - 1).getLatitude(),
											result.get(i - 1).getLongitude()))
							.icon(BitmapDescriptorFactory
									.fromResource(R.drawable.walking)));
					break;
				case act_run:
					mGoogleMap.addMarker(new MarkerOptions()
							.title("뛰기")
							.snippet((endIndex - startIndex)*10 + "초 간 뛰셨습니다.")
							.position(
									new LatLng(result.get(i - 1).getLatitude(),
											result.get(i - 1).getLongitude()))
							.icon(BitmapDescriptorFactory
									.fromResource(R.drawable.running)));
					break;
				case act_stop:
					mGoogleMap.addMarker(new MarkerOptions()
							.title("서기")
							.snippet((endIndex - startIndex)*10 + "초 간 서있었습니다.")
							.position(
									new LatLng(result.get(i - 1).getLatitude(),
											result.get(i - 1).getLongitude()))
							.icon(BitmapDescriptorFactory
									.fromResource(R.drawable.stop)));
					break;
				default:
					break;
				}
				startIndex = i;
			}
		}

		// 마지막 행동에 대한 아이콘
		switch (result.get(result.size() - 1).getMotionType()) {
		case act_walk:
			mGoogleMap.addMarker(new MarkerOptions()
					.title("걷기")
					.snippet((endIndex - startIndex)*10 + "초 간 걸으셨습니다.")
					.position(
							new LatLng(result.get(result.size() - 1)
									.getLatitude(), result.get(
									result.size() - 1).getLongitude()))
					.icon(BitmapDescriptorFactory
							.fromResource(R.drawable.walking)));
			break;
		case act_run:
			mGoogleMap.addMarker(new MarkerOptions()
					.title("뛰기")
					.snippet((endIndex - startIndex)*10 + "초 간 뛰셨습니다.")
					.position(
							new LatLng(result.get(result.size() - 1)
									.getLatitude(), result.get(
									result.size() - 1).getLongitude()))
					.icon(BitmapDescriptorFactory
							.fromResource(R.drawable.running)));
			break;
		case act_stop:
			mGoogleMap
					.addMarker(new MarkerOptions()
							.title("서기")
							.snippet((endIndex - startIndex)*10 + "초 간 서있었습니다.")
							.position(
									new LatLng(result.get(result.size() - 1)
											.getLatitude(), result.get(
											result.size() - 1).getLongitude()))
							.icon(BitmapDescriptorFactory
									.fromResource(R.drawable.stop)));
			break;
		default:
			break;
		}

		// 수면여부 판단을 통해 화면에 그림을 그려주는 부분
		for (int i = 0; i < result.size(); i++) {
			distanceToHome = distance(myHomeX, myHomeY, result.get(i)
					.getLatitude(), result.get(i).getLongitude(), 'k');
			Log.v(TAG, "distanceToHome : " + distanceToHome + ".........");
			if (distanceToHome < 0.4)
				homeFlag = true;
			else
				homeFlag = false;

//			Log.v(TAG, "homeFlag =" + homeFlag);

			if (result.get(i).getIsSleep() == 1 && homeFlag) {
				if (sleepCount == 0)
					sleepCount++;
				else {
					if (result.get(i - 1).getIsSleep() == 1)
						sleepCount++;
				}
				continue;
			}
			Log.e(TAG, "sleepCount = " + sleepCount + ".....");
			if (sleepCount > 10) {
				if (result.get(i-1).getIsSleep() == 1) {
					mGoogleMap.addCircle(new CircleOptions()
							.center(new LatLng(result.get(i - 1).getLatitude(),
									result.get(i - 1).getLongitude()))
							.radius(300).fillColor(0x9900C6ED)
							.strokeColor(0x5580F5FF));
					mGoogleMap.addMarker(new MarkerOptions()
					.title("수면")
					.position(
							new LatLng(result.get(i - 1).getLatitude(),
									result.get(i - 1).getLongitude()))
					.icon(BitmapDescriptorFactory
							.fromResource(R.drawable.sleep)));
//					Log.e(TAG, "수면시 반경표시 .....");
					sleepCount=0;
				}
			}
//			Log.e(TAG, "sleepCount = " + sleepCount + ".....");
		}
	}

	public static HomeLocation readSettingOptionFile(final Context context) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);

		float latitude = prefs.getFloat("latitude", -1.0f);
		float longitude = prefs.getFloat("longitude", -1.0f);

		return new HomeLocation(latitude, longitude);
	}

	public static double distance(double lat1, double lon1, double lat2,
			double lon2, char unit) {
		double theta = lon1 - lon2;
		double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2))
				+ Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2))
				* Math.cos(deg2rad(theta));
		dist = Math.acos(dist);
		dist = rad2deg(dist);
		dist = dist * 60 * 1.1515;
		if (unit == 'K') {
			dist = dist * 1.609344;
		} else if (unit == 'N') {
			dist = dist * 0.8684;
		}
		return (dist);
	}

	/**
	 * This function converts decimal degrees to radians
	 * 
	 * @param deg
	 * @return
	 */
	private static double deg2rad(double deg) {
		return (deg * Math.PI / 180.0);
	}

	/**
	 * This function converts radians to decimal degrees
	 * 
	 * @param rad
	 * @return
	 */
	private static double rad2deg(double rad) {
		return (rad * 180.0 / Math.PI);
	}
}

class HomeLocation {
	double x, y;

	HomeLocation(double x, double y) {
		this.x = x;
		this.y = y;
	}
}
