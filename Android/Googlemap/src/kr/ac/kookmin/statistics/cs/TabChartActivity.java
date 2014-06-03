package kr.ac.kookmin.statistics.cs;

import java.util.Calendar;

import android.app.ActivityGroup;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.TabHost;

import com.example.googlemap.R;

public class TabChartActivity extends ActivityGroup {
	public final static String TAG = "TabChartActivity";
	private TabHost tabHost;
	private Intent pathIntent;
	private Intent dayIntent;
	private Intent monthIntent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tab_chart_activity);
		Intent getIntent = getIntent();
		
		setDataInIntent(getIntent);

		tabHost = (TabHost) findViewById(R.id.tab_host);

		tabHost.setup(getLocalActivityManager());

		tabHost.addTab(tabHost.newTabSpec("tab1").setIndicator("이동경로")
				.setContent(pathIntent));

		tabHost.addTab(tabHost.newTabSpec("tab2").setIndicator("일일")
				.setContent(dayIntent));

		tabHost.addTab(tabHost.newTabSpec("tab3").setIndicator("월간")
				.setContent(monthIntent));

		DisplayMetrics dm = new  DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int height = dm.heightPixels;
		for (int i = 0; i < 3; ++i) {
			tabHost.getTabWidget().getChildAt(i).getLayoutParams().height = (int) (height*0.08);
		}

		tabHost.setCurrentTab(0);
	}
	
	private void setDataInIntent(Intent getIntent){
		Calendar calendar = Calendar.getInstance();
		pathIntent = new Intent(this, MovingPathActivity.class);
		
		dayIntent = new Intent(this, DayChartActivity.class);
		
		monthIntent = new Intent(this, MonthChartActivity.class);
		
		Bundle bundle = getIntent.getExtras();
		if( getIntent.getExtras()!=null ){
			
			pathIntent.putExtra("curyear", bundle.getInt("curyear"));
			pathIntent.putExtra("curmonth", bundle.getInt("curmonth"));
			pathIntent.putExtra("curday", bundle.getInt("curday"));

			dayIntent.putExtra("curyear", bundle.getInt("curyear"));
			dayIntent.putExtra("curmonth", bundle.getInt("curmonth"));
			dayIntent.putExtra("curday", bundle.getInt("curday"));

			monthIntent.putExtra("curyear", bundle.getInt("curyear"));
			monthIntent.putExtra("curmonth", bundle.getInt("curmonth"));
		} else{
			pathIntent.putExtra("curyear", calendar.get(Calendar.YEAR));
			pathIntent.putExtra("curmonth", calendar.get(Calendar.MONTH)+1);
			pathIntent.putExtra("curday", calendar.get(Calendar.DATE));

			dayIntent.putExtra("curyear", calendar.get(Calendar.YEAR));
			dayIntent.putExtra("curmonth", calendar.get(Calendar.MONTH)+1);
			dayIntent.putExtra("curday", calendar.get(Calendar.DATE));

			monthIntent.putExtra("curyear", calendar.get(Calendar.YEAR));
			monthIntent.putExtra("curmonth", calendar.get(Calendar.MONTH)+1);
		}
	}
}