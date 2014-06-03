package kr.ac.kookmin.cs.android;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.util.Log;

public class LocationFinder implements LocationListener {
	private final static String TAG = "LocationFinder";
	private LocationManager mLocManager;
	private Geocoder mGeoCoder;
	private Context mContext;
	private String mBestProvider;

	public LocationFinder(Context mContext) {
		super();
		this.mContext = mContext;
		// LocationListener 핸들
		mLocManager = (LocationManager) mContext
				.getSystemService(Context.LOCATION_SERVICE);
		// 주소를 가져오기 위해 설정 - KOREA, KOREAN 모두 가능
		mGeoCoder = new Geocoder(mContext, Locale.KOREA);

		mBestProvider = mLocManager.getBestProvider(new Criteria(), true);
		if (mBestProvider == null) {
			// ask user to enable atleast one of the Location Providers
			Log.d(TAG, "알맞은 위치제공자 없음");
		} else {
			Log.d(TAG, "알맞은 위치 제공자 찾았다! best provider : " + mBestProvider);
		}
		mLocManager.requestLocationUpdates(mBestProvider, 0, 0, this);
		//mLocManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, this, null);

	}


	public Location getLocation() {
		Location result = null;
		mBestProvider = mLocManager.getBestProvider(new Criteria(), true);
		result = mLocManager.getLastKnownLocation(mBestProvider);

		return result;
	}

	public StringBuffer getGeoLocation(Location loc) {
		StringBuffer mAddress = new StringBuffer();
		if (loc != null) {
			try {
				// 위도,경도를 이용하여 현재 위치의 주소를 가져온다.
				List<Address> addresses = null;
				
				addresses = mGeoCoder.getFromLocation(loc.getLatitude(),
						loc.getLongitude(), 1);
				Address addr = addresses.get(0);
				mAddress.append(addr.getCountryName());
				mAddress.append(" ");
				mAddress.append(addr.getAdminArea());
				mAddress.append(" ");
				mAddress.append(addr.getLocality());
				//mAddress.append(" ");
				//mAddress.append(addr.getThoroughfare());

				/*
				for (Address addr : addresses) {
					int index = addr.getMaxAddressLineIndex();
					for (int i = 0; i <= index; i++) {
						//Log.d(TAG, "주소[" + i + "] : " + addr.getAddressLine(i));
						
						mAddress.append(addr.getAddressLine(i));
						mAddress.append(" ");
					}
					mAddress.append("\n");
				}
				*/
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		return mAddress;
	}

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		Log.d(TAG, "onLocationChanged 호출");
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		Log.d(TAG, "onStatusChanged 호출");
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		Log.d(TAG, "onProviderEnabled 호출");
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		Log.d(TAG, "onProviderDisabled 호출");
	}
}
