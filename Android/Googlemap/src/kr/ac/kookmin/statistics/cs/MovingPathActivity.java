package kr.ac.kookmin.statistics.cs;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.example.DBWriter.DBManager;
import com.example.DBWriter.DataFormat;
import com.example.googlemap.R;

public class MovingPathActivity extends Activity {
	private static final String TAG = "MovingPathActivity";
	private ArrayList<MovingPathListData> arrayData;
	private ArrayList<DataFormat> datalist = new ArrayList<DataFormat>();
	private MovingPathListData data;
	private MovingPathListAdapter adapter;
	
	private DBManager db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.moving_path_activity);
		
		
		
		db = new DBManager(this);
		makeListView(getIntent());
	}
	
	
	private void makeListView(Intent intent) {
		setArrayData(intent);

		ListView pathList = (ListView) findViewById(R.id.path_list);
		adapter = new MovingPathListAdapter(this,
				android.R.layout.simple_list_item_1, arrayData);
		pathList.setVerticalScrollBarEnabled(true);
		pathList.setAdapter(adapter);
	}
	
	private void setArrayData(Intent intent){
		arrayData = new ArrayList<MovingPathListData>();

		String curAddress = null, nextAddress = null;
		int curMotion = 0, nextMotion = 0;
		
		//Log.v("LogTest","curDate = "+curDate);
		
		long time = System.currentTimeMillis(); 
		SimpleDateFormat dayTime = new SimpleDateFormat("hh:mm");
		String str;
		
		Log.d(TAG, "무비패스  month : " +  intent.getExtras().getInt("curmonth"));
		
		datalist = db.read(intent.getExtras().getInt("curyear"),
				  intent.getExtras().getInt("curmonth"),
				 intent.getExtras().getInt("curday"));
		

		if( datalist.size()>0 ){
			//처음값 받아오고 추가하기
			
			curMotion = datalist.get(0).getMotionType();
			curAddress = datalist.get(0).getAddress();
			
			str = dayTime.format(new Date(datalist.get(0).getCurSysTime()));
			data = new MovingPathListData(str, R.drawable.icon, curAddress);
			arrayData.add(data);
			
			Random r=new Random();
			int color = Color.rgb(r.nextInt(256), r.nextInt(256), r.nextInt(256));
			for( int i=1; i<datalist.size(); i++ ){
				nextAddress = datalist.get(i).getAddress();
				nextMotion = datalist.get(i).getMotionType();
				
				if(!nextAddress.equals(curAddress)){			// 주소가 달라졌을 때(최소단위 OO동)
					color = Color.rgb(r.nextInt(256), r.nextInt(256), r.nextInt(256));
					str = dayTime.format(new Date(datalist.get(i).getCurSysTime()));
					data = new MovingPathListData(str, color, 
							nextAddress);
					arrayData.add(data);
					curAddress = nextAddress;
				} else if(curMotion!=nextMotion){					// 행위가 달려졌을 때
					str = dayTime.format(new Date(datalist.get(i).getCurSysTime()));
					data = new MovingPathListData(str, color, 
							changeIntToMotion(nextMotion));
					arrayData.add(data);
					curMotion = nextMotion;
				}
			}
		} else {
			Log.i("LogTest", "data is not in DB.");
		}
	}
	
	private String changeIntToMotion(int motionNumber){
		String motionAct = null;
		
		switch(motionNumber){
		case 1 : 
			motionAct = "서기";
			break;
		case 2 : 
			motionAct = "걷기";
			break;
		case 3 : 
			motionAct = "뛰기";
			break;
		}
		
		return motionAct;
	}
}
