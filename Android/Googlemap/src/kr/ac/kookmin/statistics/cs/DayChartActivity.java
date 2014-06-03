package kr.ac.kookmin.statistics.cs;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.DBWriter.DBManager;
import com.example.DBWriter.DataFormat;
import com.example.googlemap.R;
import com.jjoe64.graphview.BarGraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphViewDataInterface;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.GraphViewSeries.GraphViewSeriesStyle;
import com.jjoe64.graphview.ValueDependentColor;

public class DayChartActivity extends Activity {
	private DBManager db;
	private ArrayList<DataFormat> dataSet;
	
	private TextView mTv;

	public void initDB() {
		db = new DBManager(getApplicationContext());

		Intent intent = getIntent();

		Bundle bundle = intent.getExtras();
		int month = bundle.getInt("curmonth");
		int day = bundle.getInt("curday");
		mTv = (TextView) findViewById(R.id.graph_title_2);
		mTv.setText(month +"Ώω " + day + "ΐΟ ΗΰµΏ Ελ°θ");

		  dataSet = db.read(bundle.getInt("curyear"),
				  month, day);
		  
		  
		 
		

	}
	int findMax(int standing, int walking, int running){
	      int max = standing;
	      if(max < walking){
	         max = walking;
	      }
	      if(max < running)
	         max = running;
	      return max;
	   }
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.week_chart);

		int[] motionType = new int[3];
		motionType[0] = 0; // Ό­±β
		motionType[1] = 0; // °Θ±β
		motionType[2] = 0; // ¶Ω±β

		initDB();

		for (int i = 0; i < dataSet.size(); i++) {
			switch (dataSet.get(i).getMotionType()) {
			case 1:
				motionType[0]++;
				break;
			case 2:
				motionType[1]++;
				break;
			case 3:
				motionType[2]++;
				break;
			}
		}

		// init example series data
		GraphViewSeriesStyle seriesStyle = new GraphViewSeriesStyle();

		seriesStyle.setValueDependentColor(new ValueDependentColor() {
			@Override
			public int get(GraphViewDataInterface data) {
				// the higher the more red
				return Color.rgb((int) (150 + ((data.getX() / 3) * 100)),
						(int) (150 - ((data.getX() / 3) * 150)),
						(int) (150 - ((data.getX() / 3) * 150)));
			}
		});

		GraphViewSeries exampleSeries = new GraphViewSeries("", seriesStyle,
				new GraphViewData[] { new GraphViewData(1, motionType[0]),
						new GraphViewData(2, motionType[1]),
						new GraphViewData(3, motionType[2]) });

		BarGraphView graphView = new BarGraphView(this, "");
		//graphView.setDrawValuesOnTop(true);

		graphView.setHorizontalLabels(new String[] { "Ό­±β", "°Θ±β", "¶Ω±β" });
		graphView.setManualYAxisBounds(findMax(motionType[0], motionType[1], motionType[2]), 0);

		//graphView.setHorizontalGravity(200);

		graphView.addSeries(exampleSeries); // data

		graphView.setBackgroundResource(R.drawable.day_chart_background);
		//graphView.setBackgroundColor(Color.BLACK);

		LinearLayout layout = (LinearLayout) findViewById(R.id.graph_2);
		layout.addView(graphView);
	}
}
