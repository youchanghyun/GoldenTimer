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
			// �ϳ��� �̹������ 2���� �ؽ�Ʈ�� ������ �޾ƿ´�.
			TextView pathTime = (TextView) v.findViewById(R.id.path_time);
			TextView pathImage = (TextView) v.findViewById(R.id.path_image);
			TextView pathcontent = (TextView) v.findViewById(R.id.path_content);

			// ���� item�� position�� �´� �̹����� ���� �־��ش�.
			pathTime.setText(customListData.getPathTime());
			pathImage.setBackgroundColor(customListData.getPathColor());
			pathcontent.setText(customListData.getPathContent());
		}

		return v;
	}
}
