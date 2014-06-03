package kr.ac.kookmin.statistics.cs;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.googlemap.R;

public class MovingPathListAdapter extends ArrayAdapter<MovingPathListData> {
	private ArrayList<MovingPathListData> items;

	public MovingPathListAdapter(Context context, int textViewResourceId,
			ArrayList<MovingPathListData> items) {
		super(context, textViewResourceId, items);
		this.items = items;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if (v == null) {
			LayoutInflater vi = (LayoutInflater) getContext().getSystemService(
					Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.path_layout, null);
		}
		MovingPathListData customListData = items.get(position);

		if (customListData != null) {
			// 하나의 이미지뷰와 2개의 텍스트뷰 정보를 받아온다.
			TextView pathTime = (TextView) v.findViewById(R.id.path_time);
			TextView pathImage = (TextView) v.findViewById(R.id.path_image);
			TextView pathcontent = (TextView) v.findViewById(R.id.path_content);

			// 현재 item의 position에 맞는 이미지와 글을 넣어준다.
			pathTime.setText(customListData.getPathTime());
			pathImage.setBackgroundColor(customListData.getPathColor());
			pathcontent.setText(customListData.getPathContent());
		}

		return v;
	}
}
