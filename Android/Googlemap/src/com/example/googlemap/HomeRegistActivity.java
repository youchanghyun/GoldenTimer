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
	LatLng loc = new LatLng(37.58660, 126.94352); // LatLng�� ���� �浵 ���� Ŭ����
	CameraPosition cp = new CameraPosition.Builder().target((loc)).zoom(18)
			.build();

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		// TODO Auto-generated method stub
		mGoogleMap = ((SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map_home)).getMap(); // ȭ�鿡 ���۸� ǥ��

		mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cp));
		Log.v(TAG,"ffff");
		
		mGoogleMap.setOnMapClickListener(this);
	}

	public void onMapClick(final LatLng point) {
		// ���� ������ �浵���� ȭ�� ����Ʈ�� �˷��ش�
		Point screenPt = mGoogleMap.getProjection().toScreenLocation(point);

		// ���� ȭ�鿡 ���� ����Ʈ�� ���� ������ �浵�� �˷��ش�.
		LatLng latLng = mGoogleMap.getProjection().fromScreenLocation(screenPt);

		Log.d(TAG, "��ǥ: ����(" + String.valueOf(point.latitude) + "),�浵("
				+ String.valueOf(point.longitude) + ")");
		Log.d(TAG,
				"ȭ����ǥ: X(" + String.valueOf(screenPt.x) + "),	Y("
						+ String.valueOf(screenPt.y) + ")");

		add = findAddress(Double.parseDouble(String.valueOf(point.latitude)),
				Double.parseDouble(String.valueOf(point.longitude)));

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("��ġ ���� �޼���");
		builder.setMessage("�����Ͻ� ��ġ�� "+add+"�Դϴ�. ������ ����Ͻðڽ��ϱ�?");
		builder.setNegativeButton("���",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
		
					}

				});
		builder.setPositiveButton("���",
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
			// ����° �μ��� �ִ������ε� �ϳ��� ���Ϲ޵��� �����ߴ�
			address = geocoder.getFromLocation(lat, lng, 1);
			// ������ �����ͷ� �ּҰ� ���ϵ� �����Ͱ� ������
		} catch (IOException e) {
			Toast.makeText(this, "�ּ���� ����", Toast.LENGTH_LONG).show();
			Log.e(TAG, "catch");
			e.printStackTrace();
		}

		if (address == null) {
			Log.e(TAG, "null�� ��ȯ");
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
