package com.example.googlemap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.example.googlemap.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class HomeRegistActivity extends FragmentActivity implements
		OnMapClickListener {
	private static final String TAG = "LogTest";
	LocationManager manager;
	Location loca;
	public String add;
	public GoogleMap mGoogleMap;
	public ArrayList<MarkerOptions> marker = new ArrayList<MarkerOptions>();
	LatLng loc = new LatLng(37.58660, 126.94352); // LatLng는 위도 경도 갖는 클래스
	CameraPosition cp = new CameraPosition.Builder().target((loc)).zoom(18)
			.build();

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		// TODO Auto-generated method stub
		mGoogleMap = ((SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map_home)).getMap(); // 화면에 구글맵 표시

		mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cp));
		Log.v(TAG,"ffff");
		
		mGoogleMap.setOnMapClickListener(this);
	}

	public void onMapClick(final LatLng point) {
		// 현재 위도와 경도에서 화면 포인트를 알려준다
		Point screenPt = mGoogleMap.getProjection().toScreenLocation(point);

		// 현재 화면에 찍힌 포인트로 부터 위도와 경도를 알려준다.
		LatLng latLng = mGoogleMap.getProjection().fromScreenLocation(screenPt);

		Log.d(TAG, "좌표: 위도(" + String.valueOf(point.latitude) + "),경도("
				+ String.valueOf(point.longitude) + ")");
		Log.d(TAG,
				"화면좌표: X(" + String.valueOf(screenPt.x) + "),	Y("
						+ String.valueOf(screenPt.y) + ")");

		add = findAddress(Double.parseDouble(String.valueOf(point.latitude)),
				Double.parseDouble(String.valueOf(point.longitude)));

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("위치 저장 메세지");
		builder.setMessage("선택하신 위치는 "+add+"입니다. 집으로 등록하시겠습니까?");
		builder.setNegativeButton("취소",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
		
					}

				});
		builder.setPositiveButton("등록",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = new Intent();
						intent.putExtra("latitude", point.latitude);
						intent.putExtra("longitude", point.longitude);
						setResult(Activity.RESULT_OK, intent);
						finish();
					}

				});
		builder.show();
	}

	private String findAddress(double lat, double lng) {
		String bf = new String();
		Geocoder geocoder = new Geocoder(this, Locale.KOREAN);
		List<Address> address = null;

		try {
			// 세번째 인수는 최대결과값인데 하나만 리턴받도록 설정했다
			address = geocoder.getFromLocation(lat, lng, 1);
			// 설정한 데이터로 주소가 리턴된 데이터가 있으면
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
		}
		return bf;
	}

}
