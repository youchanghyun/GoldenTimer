package com.example.googlemaptest;

import java.io.IOException;
import java.sql.Date;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

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

	public int count = 0; // 몇번째 정보인지 저장하는 index
	LocationManager manager;
	Location loca;
	public GoogleMap mGoogleMap;
	Polyline polyline;
	DecimalFormat df = new DecimalFormat();

	long time;
	int currentDay;

	ArrayList<Data> data = new ArrayList<Data>(); // data클래스 ArrayList 생성
	public ArrayList<Time> list_time = new ArrayList<Time>();
	public ArrayList<LatLng> list_LatLng = new ArrayList<LatLng>();
	public ArrayList<PolylineOptions> ops = new ArrayList<PolylineOptions>();

	Data firstData;
	Data secondData;

	DB_OPEN db_open;
	SQLiteDatabase db;
	public int index = 0;
	public int _index = 0;
	public int markIndex = 0;
	public int num_db;
	public int end_num;

	MyLocationListener listener = new MyLocationListener();

	LatLng loc = new LatLng(37.58660, 126.94352); // LatLng는 위도 경도 갖는 클래스
	CameraPosition cp = new CameraPosition.Builder().target((loc)).zoom(18)
			.build();
	public ArrayList<MarkerOptions> marker = new ArrayList<MarkerOptions>();

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);

		Calendar cal = Calendar.getInstance();
		currentDay = cal.get(Calendar.DAY_OF_MONTH);
		marker.add(new MarkerOptions());
		db_open = new DB_OPEN(this);
		db = db_open.getWritableDatabase();

		mGoogleMap = ((SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map)).getMap(); // 화면에 구글맵 표시

		mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cp)); // 지정위치로
																				// 이동
		ops.clear();

		// DB에 저장된 value들 받아오기
		Cursor c_id = db.rawQuery("SELECT user_id FROM sensor_data WHERE day ="
				+ currentDay, null);
		c_id.moveToFirst();
		c_id.getCount();

		for (int i = 0; i < c_id.getCount(); i++) {
			data.add(new Data());
		}

		for (int i = 0; i < c_id.getCount(); i++) {
			data.get(i).id = c_id.getInt(0);
			Log.v(TAG, "id" + " " + data.get(i).id);
			c_id.moveToNext();
		}
		Log.v(TAG, "처음 id 받아오기");

		Cursor c_lat = db.rawQuery(
				"SELECT latitude FROM sensor_data WHERE day =" + currentDay,
				null);
		c_lat.moveToFirst();
		c_lat.getCount();

		for (int i = 0; i < c_lat.getCount(); i++) {
			data.get(i)._lat = c_lat.getDouble(0);
			Log.v(TAG, "lat[" + i + "] : " + " " + data.get(i)._lat);
			c_lat.moveToNext();
		}
		Log.v(TAG, "처음 lat 받아오기");

		Cursor c_lng = db.rawQuery(
				"SELECT longitude FROM sensor_data WHERE day =" + currentDay,
				null);
		c_lng.moveToFirst();
		c_lng.getCount();

		for (int i = 0; i < c_lng.getCount(); i++) {
			data.get(i)._lng = c_lng.getDouble(0);
			Log.v(TAG, "lng[" + i + "] : " + " " + data.get(i)._lng);
			c_lng.moveToNext();
		}
		Log.v(TAG, "처음 lng 받아오기");

		Log.e(TAG, c_lng.getCount() + "");

		for (int i = 0; i < c_lng.getCount(); i++) {
			Log.e(TAG,
					i + " id : " + data.get(i).id + " " + "lat[i] : "
							+ data.get(i)._lat + " " + "lng[i] : "
							+ data.get(i)._lng);
		}

		num_db = c_lng.getCount();

		Cursor c_month = db.rawQuery(
				"SELECT month FROM sensor_data WHERE day =" + currentDay, null);
		c_month.moveToFirst();
		c_month.getCount();

		for (int i = 0; i < c_month.getCount(); i++) {
			data.get(i).month = c_month.getInt(0);
			Log.v(TAG, "month[" + i + "] : " + " " + data.get(i).month);
			c_month.moveToNext();
		}
		Log.v(TAG, "처음 month 받아오기");

		Cursor c_day = db.rawQuery("SELECT day FROM sensor_data WHERE day ="
				+ currentDay, null);
		c_day.moveToFirst();
		c_day.getCount();

		for (int i = 0; i < c_day.getCount(); i++) {
			data.get(i).day = c_day.getInt(0);
			Log.v(TAG, "day[" + i + "] : " + " " + data.get(i).day);
			c_day.moveToNext();
		}
		Log.v(TAG, "처음 day 받아오기");

		Cursor c_hour = db.rawQuery("SELECT hour FROM sensor_data WHERE day ="
				+ currentDay, null);
		c_hour.moveToFirst();
		c_hour.getCount();

		for (int i = 0; i < c_hour.getCount(); i++) {
			data.get(i).hour = c_hour.getInt(0);
			Log.v(TAG, "hour[" + i + "] : " + " " + data.get(i).hour);
			c_hour.moveToNext();
		}
		Log.v(TAG, "처음 hour 받아오기");

		Cursor c_minute = db
				.rawQuery("SELECT minute FROM sensor_data WHERE day ="
						+ currentDay, null);
		c_minute.moveToFirst();
		c_minute.getCount();

		for (int i = 0; i < c_minute.getCount(); i++) {
			data.get(i).minute = c_minute.getInt(0);
			Log.v(TAG, "minute[" + i + "] : " + " " + data.get(i).minute);
			c_minute.moveToNext();
		}
		Log.v(TAG, "처음 minute 받아오기");

		Cursor c_second = db
				.rawQuery("SELECT second FROM sensor_data WHERE day ="
						+ currentDay, null);
		c_second.moveToFirst();
		c_second.getCount();

		for (int i = 0; i < c_second.getCount(); i++) {
			data.get(i).second = c_second.getInt(0);
			Log.v(TAG, "second[" + i + "] : " + " " + data.get(i).second);
			c_second.moveToNext();
		}
		Log.v(TAG, "처음 second 받아오기");

		int time_ = 0;

		marker.add(new MarkerOptions()); // marker Icon을 위한 개체 추가
		for (int i = 0; i < c_lng.getCount(); i++) {
			if (i == 0 || i == 1) {
				if (i == 0) 
					firstData = data.get(i);
				else {
					if (data.get(i).id != data.get(i - 1).id) {
						secondData = data.get(i);
						time_ = diffTime(firstData, secondData);

						switch (data.get(i).id) {
						// 걷기로 행동이 바꼈을 때 아이콘 출력
						case 1:
							marker.get(markIndex)
									.position(
											new LatLng(data.get(i)._lat, data
													.get(i)._lng))
									.title("걷기")
									.snippet(time_ + "초 간 이동")
									.icon(BitmapDescriptorFactory
											.fromResource(R.drawable.walking));
							mGoogleMap.addMarker(marker.get(markIndex));
							// marker.add(new MarkerOptions());
							break;
						// 뛰기로 행동이 바꼈을 때 아이콘 출력
						case 2:
							marker.get(markIndex)
									.position(
											new LatLng(data.get(i)._lat, data
													.get(i)._lng))
									.title("뛰기")
									.snippet(time_ + "초 간 이동")
									.icon(BitmapDescriptorFactory
											.fromResource(R.drawable.running));
							mGoogleMap.addMarker(marker.get(markIndex));
							// marker.add(new MarkerOptions());
							break;
						// 수면 시에 반경을 표시
						default:
							mGoogleMap.addCircle(new CircleOptions()
									.center(new LatLng(data.get(i)._lat, data
											.get(i)._lng)).radius(20)
									.fillColor(0x3380F5FF)
									.strokeColor(0x5580F5FF));
							break;
						}
						
						firstData = data.get(i);
					} else
						continue;
				}
				continue;
			} else {
				if (data.get(i - 1).id == data.get(i - 2).id)
					continue;
				else {

					secondData = data.get(i - 1);
					time_ = diffTime(firstData, secondData);

					marker.add(new MarkerOptions());
					// 걷기,뛰기,서기 에 대한 행동별 이벤트 구현
					switch (data.get(i - 1).id) {
					// 걷기로 행동이 바꼈을 때 아이콘 출력
					case 1:
						marker.get(markIndex)
								.position(
										new LatLng(data.get(i - 1)._lat, data
												.get(i - 1)._lng))
								.title("걷기")
								.snippet(time_ + "초 간 이동")
								.icon(BitmapDescriptorFactory
										.fromResource(R.drawable.walking));
						mGoogleMap.addMarker(marker.get(markIndex));
						// marker.add(new MarkerOptions());
						break;
					// 뛰기로 행동이 바꼈을 때 아이콘 출력
					case 2:
						marker.get(markIndex)
								.position(
										new LatLng(data.get(i - 1)._lat, data
												.get(i - 1)._lng))
								.title("뛰기")
								.snippet(time_ + "초 간 이동")
								.icon(BitmapDescriptorFactory
										.fromResource(R.drawable.running));
						mGoogleMap.addMarker(marker.get(markIndex));
						// marker.add(new MarkerOptions());
						break;
					// 수면 시에 반경을 표시
					default:
						mGoogleMap.addCircle(new CircleOptions()
								.center(new LatLng(data.get(i - 1)._lat, data
										.get(i - 1)._lng)).radius(20)
								.fillColor(0x3380F5FF).strokeColor(0x5580F5FF));
						break;
					}
				}
				markIndex++;

				firstData = data.get(i - 1);
			}
		}

		ops.add(new PolylineOptions());
		// 행동별로 선을 그려주는 부분
		if (data.size() > 1 && count == 0) {
			for (int i = 0; i < num_db; i++) {
				if (data.get(i).id == 1) {
					ops.add(new PolylineOptions());
					if (i != 0) {
						mGoogleMap.addPolyline(ops
								.get(index)
								.add(new LatLng(data.get(i - 1)._lat, data
										.get(i - 1)._lng)).color(Color.RED));
					}
					mGoogleMap
							.addPolyline(ops
									.get(index)
									.add(new LatLng(data.get(i)._lat, data
											.get(i)._lng)).color(Color.RED));
					Log.e(TAG, index + " 걷기 red 로 추가");
				} else {
					if (data.get(i).id == 3) {
						if (data.get(i - 1).id == 1) {
							ops.add(new PolylineOptions());
							if (i != 0) {
								mGoogleMap.addPolyline(ops
										.get(index)
										.add(new LatLng(data.get(i - 1)._lat, data
												.get(i - 1)._lng))
										.color(Color.RED));
							}
							mGoogleMap.addPolyline(ops
									.get(index)
									.add(new LatLng(data.get(i)._lat,
											data.get(i)._lng)).color(Color.RED));
						} else {
							ops.add(new PolylineOptions());
							if (i != 0) {
								mGoogleMap.addPolyline(ops
										.get(index)
										.add(new LatLng(data.get(i - 1)._lat, data
												.get(i - 1)._lng))
										.color(Color.BLUE));
							}
							mGoogleMap.addPolyline(ops
									.get(index)
									.add(new LatLng(data.get(i)._lat,
											data.get(i)._lng)).color(Color.BLUE));
						}
					} else {
						ops.add(new PolylineOptions());
						if (i != 0) {
							mGoogleMap.addPolyline(ops
									.get(index)
									.add(new LatLng(data.get(i - 1)._lat, data
											.get(i - 1)._lng))
									.color(Color.BLUE));
						}
						mGoogleMap.addPolyline(ops
								.get(index)
								.add(new LatLng(data.get(i)._lat,
										data.get(i)._lng)).color(Color.BLUE));
						Log.e(TAG, index + " 뛰기 blue 로 추가");
					}
				}
				index++;
			}
		}
		end_num = index;

		 getMyLocation();
	}

	private void getMyLocation() {
		if (manager == null) {
			manager = (LocationManager) this
					.getSystemService(Context.LOCATION_SERVICE);
		}
		// provider 기지국||GPS 를 통해서 받을건지 알려주는 Stirng 변수
		// minTime 최소한 얼마만의 시간이 흐른후 위치정보를 받을건지 시간간격을 설정 설정하는 변수
		// minDistance 얼마만의 거리가 떨어지면 위치정보를 받을건지 설정하는 변수
		// manager.requestLocationUpdates(provider, minTime, minDistance,
		// listener);

		// 3초
		long minTime = 3000;
		// 거리는 0
		float minDistance = 0;

		Criteria criteria = new Criteria();
		// criteria.setAccuracy(Criteria.ACCURACY_FINE);

		String provider = manager.getBestProvider(criteria, true);

		Toast.makeText(MapActivity.this, provider, Toast.LENGTH_SHORT).show();

		manager.requestLocationUpdates(provider, minTime, minDistance, listener);
		loca = manager.getLastKnownLocation(provider);
	}

	class MyLocationListener implements LocationListener {
		// 위치정보는 아래 메서드를 통해서 전달된다.
		@Override
		public void onLocationChanged(Location location) {
			double latitude = location.getLatitude();
			double longitude = location.getLongitude();
			long nowTime;
			String st;

			nowTime = System.currentTimeMillis();
			int act_num=0;

			st = "" + nowTime;

			list_time.add(new Time());

			Calendar calendar = Calendar.getInstance();
			list_time.get(count).month = calendar.get(Calendar.MONTH) + 1;
			list_time.get(count).day = calendar.get(Calendar.DAY_OF_MONTH);
			list_time.get(count).hour = calendar.get(Calendar.HOUR);
			list_time.get(count).minute = calendar.get(Calendar.MINUTE);
			list_time.get(count).second = calendar.get(Calendar.SECOND);

			Log.v(TAG, list_time.get(count).month + "월"
					+ list_time.get(count).day + "일"
					+ list_time.get(count).hour + "시"
					+ list_time.get(count).minute + "분"
					+ list_time.get(count).second + "초");

			// list_LatLng.add(new LatLng(latitude, longitude));

			list_LatLng.add(new LatLng(latitude + count * 0.1
					* (count * 0.00074), longitude + (count * 0.00042)));

			if (count % 5 == 0) {
				act_num = 1;
				Log.v(TAG, "insert 걷기");
			} else {
				if (count % 3 == 0) {
					act_num = 2;
					Log.v(TAG, "insert 수면");
				} else {
					act_num = 3;
					Log.v(TAG, "insert 뛰기");
				}
			}
			
			db.execSQL("INSERT INTO sensor_data"
					+ "(user_id, latitude, longitude,month,day,hour,minute,second)"
					+ " VALUES (" + act_num + ","
					+ list_LatLng.get(count).latitude + ","
					+ list_LatLng.get(count).longitude + ","
					+ list_time.get(count).month + ","
					+ list_time.get(count).day + ","
					+ list_time.get(count).hour + ","
					+ list_time.get(count).minute + ","
					+ list_time.get(count).second + ")");
			
			CameraPosition cp = new CameraPosition.Builder()
					.target(new LatLng(list_LatLng.get(count).latitude,
							list_LatLng.get(count).longitude)).zoom(17).build();

			mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cp));
			count++;
		}

		@Override
		public void onProviderDisabled(String provider) {
		}

		@Override
		public void onProviderEnabled(String provider) {
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	}

	// 실주소 가져오는 함수
	private String findAddress(double lat, double lng) {
		String bf = new String();
		Geocoder geocoder = new Geocoder(this, Locale.KOREAN);
		List<Address> address = null;
		Log.v(TAG, lat + " " + lng);

		try {
			// 세번째 인수는 최대결과값인데 하나만 리턴받도록 설정했다
			address = geocoder.getFromLocation(lat, lng, 1);
			// 설정한 데이터로 주소가 리턴된 데이터가 있으면
			Log.e(TAG, "try");
		} catch (IOException e) {
			Toast.makeText(this, "주소취득 실패", Toast.LENGTH_LONG).show();
			Log.e(TAG, "catch");
			e.printStackTrace();
		}

		if (address == null) {
			Log.e(TAG, "null값 반환");
			return null;
		}
		if (address.size() > 0) {
			Address addr = address.get(0);
			bf = addr.getCountryName() + " " + " " + addr.getLocality() + " "
					+ addr.getThoroughfare() + " " + addr.getFeatureName();
			Log.e(TAG, bf);
		}
		return bf;
	}

	public int diffTime(Data a, Data b) {
		int result = 0;
		if (a.hour <= b.hour) {
			if (a.minute < b.minute) {
				if (b.second > a.second)
					result = (b.minute - a.minute) * 60 + (b.second - a.second);
				else {
					result = (b.minute - a.minute - 1) * 60
							+ (a.second - b.second);
				}
			} else {
				if (b.second > a.second)
					result = (b.minute - a.minute) * 60 + (b.second - a.second);
			}
		}
		return result;
	}
}