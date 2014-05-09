package com.example.googlemaptest;

import android.app.ActivityGroup;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.widget.TabHost;
import com.example.statistics.*;
// 최초 실행되는 Activity
public class MainActivity extends ActivityGroup {

	private TabHost tabHost;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		tabHost = (TabHost) findViewById(R.id.tabHost);
		
		tabHost.setup(getLocalActivityManager());
		
		tabHost.addTab(tabHost.newTabSpec("Tab_MAP").setIndicator("Map")
				.setContent(new Intent(this, MapActivity.class)));

		tabHost.addTab(tabHost.newTabSpec("Tab_STATISTIC")
				.setIndicator("Statistic")
				.setContent(new Intent(this, CalendarMonthViewActivity.class)));
		tabHost.setCurrentTab(0);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
}
