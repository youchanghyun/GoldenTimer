package kr.ac.kookmin.cs.schedule;

import java.util.ArrayList;

import kr.ac.kookmin.statistics.cs.TabChartActivity;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Instrumentation;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
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

import com.example.DBWriter.DBManager;
import com.example.DBWriter.DataFormatSchedule;
import com.example.googlemap.MapActivity;
import com.example.googlemap.R;
public class CalendarMonthViewActivity extends Activity {
	private final static String TAG = "CalendarMonthViewActivity";
	private static final int ONE = 1;
	private static final int TWO = 2;
	private static final int THREE = 3;
	private static final int FOR = 4;

	GridView monthView;
	private CalendarMonthAdapter monthViewAdapter;

	private TextView monthText;

	private int curYear = 0;
	private int curMonth = 0;
	private int curDay = 0;

	private int curPosition;
	private EditText scheduleInput;
	private Button saveButton;

	private ListView scheduleList;
	private ScheduleListAdapter scheduleAdapter;
	private ArrayList outScheduleList;

	private boolean scheduleexist = false;
	private Menu menu;

	// 롱터치
	private Handler mHandler = null;

	private DBManager db;
	private ArrayList<DataFormatSchedule> dataSchedule;

	public static final int REQUEST_CODE_SCHEDULE_INPUT = 1001;

	private void setDBToSchedule() {
		db = new DBManager(getApplicationContext());

		dataSchedule = db.readSchedule(curYear, curMonth);

		for (int i = 0; i < dataSchedule.size(); i++) {

			outScheduleList = monthViewAdapter.getSchedule(dataSchedule.get(i)
					.getPosition());
			if (outScheduleList == null) {
				outScheduleList = new ArrayList<ScheduleListItem>();
			}

			outScheduleList.add(new ScheduleListItem(dataSchedule.get(i)
					.getTime(), dataSchedule.get(i).getMemo()));

			monthViewAdapter.putSchedule(dataSchedule.get(i).getYear(),
					dataSchedule.get(i).getMonth(), dataSchedule.get(i)
							.getPosition(), outScheduleList);
		}
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_calendar);

		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		monthView = (GridView) findViewById(R.id.monthView);
		monthViewAdapter = new CalendarMonthAdapter(this, dm);
		monthView.setAdapter(monthViewAdapter);

		// 승준
		monthText = (TextView) findViewById(R.id.monthText);
		setMonthText();
		setDBToSchedule(); 
		
		// set listener
		monthView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				new Thread(new Runnable() {
					public void run() {
						KeyEvent event = new KeyEvent(KeyEvent.ACTION_DOWN,
								KeyEvent.KEYCODE_MENU);
						new Instrumentation().sendKeySync(event);
						KeyEvent event2 = new KeyEvent(KeyEvent.ACTION_UP,
								KeyEvent.KEYCODE_MENU);
						new Instrumentation().sendKeySync(event2);

					}
				}).start();

				MonthItem curItem = (MonthItem) monthViewAdapter
						.getItem(position);
				curDay = curItem.getDay();
				
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
						.setMessage(
								((ScheduleListItem) scheduleAdapter.getItem(ps))
										.getListItem())
						.setCancelable(false)
						.setPositiveButton("확인",
								new DialogInterface.OnClickListener() {

									// 확인 버튼 클릭시 설정
									public void onClick(DialogInterface dialog,
											int whichButton) {
										
										ArrayList<DataFormatSchedule> tempDataSchedule = dataSchedule;

										ScheduleListItem item = (ScheduleListItem) scheduleAdapter
												.getItem(ps);

										scheduleAdapter.removeItem(item);

										scheduleAdapter.notifyDataSetChanged();
										
										db.deleteSchedule(curYear, curMonth, curPosition, ps);
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

		if (scheduleAdapter.getScheduleList().size() == 0) {
			menu.add(ONE, ONE, Menu.NONE, "일정 추가");
			menu.add(FOR, FOR, Menu.NONE, "Map");
			menu.add(TWO, TWO, Menu.NONE, "통계 분석");
		} else {
			menu.add(ONE, ONE, Menu.NONE, "일정 추가");
			menu.add(THREE, THREE, Menu.NONE, "일정 삭제");
			menu.add(FOR, FOR, Menu.NONE, "Map");
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
		case FOR:
			showMap();
			return true;
		default:
			break;
		}
		return false;
	}
	
	private void showMap(){
		if (curDay != 0) {
			Intent intent = new Intent(CalendarMonthViewActivity.this,
					MapActivity.class);
			intent.putExtra("curyear", this.curYear);
			intent.putExtra("curmonth", this.curMonth + 1);
			intent.putExtra("curday", this.curDay);
			startActivity(intent);
		} else {
			Toast.makeText(getApplicationContext(), "날짜를 선택하세요.", 1000).show();
		}
	}

	private void deleteSchedule() {
		scheduleAdapter.getScheduleList().clear();

		scheduleAdapter.notifyDataSetChanged();

		db.deleteSchedule(curYear, curMonth, curPosition);
	}

	private void showScheduleInput() {
		if (curDay != 0) {
			Intent intent = new Intent(CalendarMonthViewActivity.this,
					ScheduleInputActivity.class);
			startActivityForResult(intent, REQUEST_CODE_SCHEDULE_INPUT);
		} else {
			Toast.makeText(getApplicationContext(), "날짜를 선택하세요.", 1000).show();
		}
	}

	private void showChartActivity() {
		if (curDay != 0) {
			Intent intent = new Intent(CalendarMonthViewActivity.this,
					TabChartActivity.class);
			intent.putExtra("curyear", this.curYear);
			intent.putExtra("curmonth", this.curMonth + 1);
			intent.putExtra("curday", this.curDay);
			startActivity(intent);
		} else {
			Toast.makeText(getApplicationContext(), "날짜를 선택하세요.", 1000).show();
		}
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
					
					db.insertSchedule(new DataFormatSchedule(time, curYear, curMonth, curPosition, outScheduleList.size()-1, message));
				}
			} else if (resultCode == Activity.RESULT_CANCELED) {
				Toast.makeText(getBaseContext(), "Cancerllation",
						Toast.LENGTH_SHORT).show();
			}
		}

	}

}