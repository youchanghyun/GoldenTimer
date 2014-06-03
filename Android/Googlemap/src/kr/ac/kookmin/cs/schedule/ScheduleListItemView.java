package kr.ac.kookmin.cs.schedule;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.googlemap.R;

public class ScheduleListItemView extends LinearLayout {
	private Context mContext;

	private TextView timeText;
	private TextView messageText;

	public ScheduleListItemView(Context context) {
		super(context);

		mContext = context;

		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.schedule_listitem, this, true);

		timeText = (TextView) findViewById(R.id.timeText);
		messageText = (TextView) findViewById(R.id.messageText);

	}

	public void setTime(String timeStr) {
		timeText.setText(timeStr);
	}

	public void setMessage(String messageStr) {
		messageText.setText(messageStr);
	}

}
