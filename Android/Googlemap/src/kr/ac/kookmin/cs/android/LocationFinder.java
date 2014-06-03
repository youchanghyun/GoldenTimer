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
		// LocationListener �ڵ�
		mLocManager = (LocationManager) mContext
				.getSystemService(Context.LOCATION_SERVICE);
		// �ּҸ� �������� ���� ���� - KOREA, KOREAN ��� ����
		mGeoCoder = new Geocoder(mContext, Locale.KOREA);

		mBestProvider = mLocManager.getBestProvider(new Criteria(), true);
		if (mBestProvider == null) {
			// ask user to enable atleast one of the Location Providers
			Log.d(TAG, "�˸��� ��ġ������ ����");
		} else {
			Log.d(TAG, "�˸��� ��ġ ������ ã�Ҵ�! best provider : " + mBestProvider);
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
				// ����,�浵�� �̿��Ͽ� ���� ��ġ�� �ּҸ� �����´�.
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
						//Log.d(TAG, "�ּ�[" + i + "] : " + addr.getAddressLine(i));
						
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
		Log.d(TAG, "onLocationChanged ȣ��");
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		Log.d(TAG, "onStatusChanged ȣ��");
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		Log.d(TAG, "onProviderEnabled ȣ��");
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		Log.d(TAG, "onProviderDisabled ȣ��");
	}
}
