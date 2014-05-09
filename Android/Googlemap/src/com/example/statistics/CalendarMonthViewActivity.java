package com.example.statistics;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.googlemaptest.*;

public class CalendarMonthViewActivity extends Activity {
	private static final int ONE = 1;
	private static final int TWO = 2;
	private static final int THREE = 3;

	GridView monthView;
	CalendarMonthAdapter monthViewAdapter;

	TextView monthText;

	int curYear;
	int curMonth;

	int curPosition;
	EditText scheduleInput;
	Button saveButton;

	ListView scheduleList;
	ScheduleListAdapter scheduleAdapter;
	ArrayList outScheduleList;

	boolean scheduleexist = false;
	Menu menu;

	// 롱터치
	private Handler mHandler = null;

	public static final int REQUEST_CODE_SCHEDULE_INPUT = 1001;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		
		monthView = (GridView) findViewById(R.id.monthView);
		monthViewAdapter = new CalendarMonthAdapter(this);
		monthView.setAdapter(monthViewAdapter);

		// set listener
		monthView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				MonthItem curItem = (MonthItem) monthViewAdapter
						.getItem(position);
				int day = curItem.getDay();

				Toast.makeText(getApplicationContext(), day + "일이 선택되었습니다.",
						1000).show();

				monthViewAdapter.setSelectedPosition(position);
				monthViewAdapter.notifyDataSetChanged();

				// set schedule to the TextView
				curPosition = position;

				outScheduleList = monthViewAdapter.getSchedule(position);
				if (outScheduleList == null) {
					outScheduleList = new ArrayList<ScheduleListItem>();
				}
				scheduleAdapter.setScheduleList(outScheduleList);

				scheduleAdapter.notifyDataSetChanged();

			}
		});

		monthText = (TextView) findViewById(R.id.monthText);
		setMonthText();

		Button monthPrevious = (Button) findViewById(R.id.monthPrevious);
		monthPrevious.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				monthViewAdapter.setPreviousMonth();
				monthViewAdapter.notifyDataSetChanged();

				setMonthText();
			}
		});

		Button monthNext = (Button) findViewById(R.id.monthNext);
		monthNext.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				monthViewAdapter.setNextMonth();
				monthViewAdapter.notifyDataSetChanged();

				setMonthText();
			}
		});

		curPosition = -1;

		scheduleList = (ListView) findViewById(R.id.scheduleList);
		scheduleAdapter = new ScheduleListAdapter(this);
		scheduleList.setAdapter(scheduleAdapter);

		scheduleList.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				AlertDialog.Builder builder = new AlertDialog.Builder(
						CalendarMonthViewActivity.this);
				
				final int ps = position;
				
				builder.setTitle("일정 삭제")
						.setMessage( ((ScheduleListItem)scheduleAdapter.getItem(ps)).getListItem() )
						.setCancelable(false)
						.setPositiveButton("확인",
								new DialogInterface.OnClickListener() {
							
									// 확인 버튼 클릭시 설정
									public void onClick(DialogInterface dialog,
											int whichButton) {
										
										ScheduleListItem item = (ScheduleListItem)scheduleAdapter.getItem(ps);
										
										scheduleAdapter.removeItem(item);

										scheduleAdapter.notifyDataSetChanged();
										
									}
								})
						.setNegativeButton("취소",
								new DialogInterface.OnClickListener() {
							
									// 취소 버튼 클릭시 설정
									public void onClick(DialogInterface dialog,
											int whichButton) {
										dialog.cancel();
									}
								});

				AlertDialog dialog = builder.create(); 
				dialog.show(); 

				return false;
			}
		});
	}

	private void setMonthText() {
		curYear = monthViewAdapter.getCurYear();
		curMonth = monthViewAdapter.getCurMonth();

		monthText.setText(curYear + "년 " + (curMonth + 1) + "월");
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		// addOptionMenuItems(menu);

		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		addOptionMenuItems(menu);
		return super.onPrepareOptionsMenu(menu);
	}

	private void addOptionMenuItems(Menu menu) {
		menu.clear();

		if (scheduleAdapter.getScheduleList().size()==0) {
			menu.add(ONE, ONE, Menu.NONE, "일정 추가");
			menu.add(TWO, TWO, Menu.NONE, "통계 분석");
		} else {
			menu.add(ONE, ONE, Menu.NONE, "일정 추가");
			menu.add(THREE, THREE, Menu.NONE, "일정 삭제");
			menu.add(TWO, TWO, Menu.NONE, "통계 분석");
		}
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case ONE:
			showScheduleInput();
			return true;
		case TWO:
			showChartActivity();
			return true;
		case THREE:
			deleteSchedule();
			return true;
		default:
			break;
		}
		return false;
	}

	private void deleteSchedule() {
		scheduleAdapter.getScheduleList().clear();

		scheduleAdapter.notifyDataSetChanged();

		Toast.makeText(getBaseContext(), "schedule delete", Toast.LENGTH_SHORT)
				.show();

	}

	private void showScheduleInput() {
		Intent intent = new Intent(this, ScheduleInputActivity.class);
		startActivityForResult(intent, REQUEST_CODE_SCHEDULE_INPUT);
	}

	private void showChartActivity() {
	//	Intent intent = new Intent(this, ChartActivity.class);
	//	startActivityForResult(intent, REQUEST_CODE_SCHEDULE_INPUT);
	}

	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);

		if (requestCode == REQUEST_CODE_SCHEDULE_INPUT) {
			if (resultCode == Activity.RESULT_OK) {
				String time = intent.getStringExtra("time");
				String message = intent.getStringExtra("message");

				if (message != null) {
					Toast toast = Toast.makeText(getBaseContext(),
							"result code : " + resultCode + ", time : " + time
									+ ", message : " + message,
							Toast.LENGTH_SHORT);
					toast.show();

					ScheduleListItem aItem = new ScheduleListItem(time, message);

					if (outScheduleList == null) {
						outScheduleList = new ArrayList();
					}
					outScheduleList.add(aItem);

					monthViewAdapter.putSchedule(curPosition, outScheduleList);

					scheduleAdapter.setScheduleList(outScheduleList);

					scheduleAdapter.notifyDataSetChanged();
				}
			} else if (resultCode == Activity.RESULT_CANCELED) {
				Toast.makeText(getBaseContext(), "Cancerllation",
						Toast.LENGTH_SHORT).show();
			}
		}

	}

}