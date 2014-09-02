package ch.hearc.smarthome.heating;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import ch.hearc.smarthome.R;

public class HeatingHistoryArrayAdapter extends
		ArrayAdapter<HeatingHistoryObject> {

	Context context;
	int resource;
	ArrayList<HeatingHistoryObject> history = null;

	public HeatingHistoryArrayAdapter(Context context, int resource,
			ArrayList<HeatingHistoryObject> history) {
		super(context, resource, history);

		this.context = context;
		this.resource = resource;
		this.history = history;

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View row = convertView;
		HistoryHolder holder = null;

		if (row == null) {
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			row = inflater.inflate(resource, parent, false);

			holder = new HistoryHolder();
			holder.date = (TextView) row
					.findViewById(R.id.heating_history_list_item_date);
			holder.temp = (TextView) row
					.findViewById(R.id.heating_history_list_item_temp);

			row.setTag(holder);
		} else {
			holder = (HistoryHolder) row.getTag();
		}

		final HeatingHistoryObject historyObject = history.get(position);
		holder.date.setText(historyObject.getDate());
		holder.temp.setText("Midi: " + historyObject.getTemp() + "° C");

		return row;
	}

	static class HistoryHolder {
		TextView date, temp;
	}

}
