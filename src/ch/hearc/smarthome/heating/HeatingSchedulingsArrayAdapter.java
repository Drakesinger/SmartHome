package ch.hearc.smarthome.heating;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import ch.hearc.smarthome.R;

/* StableArrayAdapter
 * 
 * This class extends ArrayAdapter
 * 
 * This is used to create the custom ListView, with my own items
 * 
 */
public class HeatingSchedulingsArrayAdapter extends ArrayAdapter<HeatingScheduling> {

	// CONSTS
	public boolean CHECKED = true, UNCHECKED = false;

	Context context;
	int layoutResourceId;
	ArrayList<HeatingScheduling> data = null;

	public HeatingSchedulingsArrayAdapter(Context context, int layoutResourceId,
			ArrayList<HeatingScheduling> data) {

		super(context, layoutResourceId, data);

		this.context = context;
		this.layoutResourceId = layoutResourceId;
		this.data = data;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View row = convertView;
		SchedulingHolder holder = null;

		if (row == null) {
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			row = inflater.inflate(layoutResourceId, parent, false);

			holder = new SchedulingHolder();
			holder.name = (TextView) row
					.findViewById(R.id.scheduling_list_item_name);
			holder.date = (TextView) row
					.findViewById(R.id.scheduling_list_item_date);
			holder.temp = (TextView) row
					.findViewById(R.id.scheduling_list_item_temp);
			holder.select = (CheckBox) row
					.findViewById(R.id.scheduling_list_item_checkbox);

			row.setTag(holder);
		} else {
			holder = (SchedulingHolder) row.getTag();
		}

		final HeatingScheduling scheduling = data.get(position);
		holder.name.setText(scheduling.getName());
		holder.date.setText(scheduling.getDate());
		holder.temp.setText(scheduling.getTemp() + "°");

		holder.select.setChecked(false);

		holder.select.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (scheduling.getState() == CHECKED)
					scheduling.setState(UNCHECKED);
				else
					scheduling.setState(CHECKED);

			}
		});

		return row;

	}

	static class SchedulingHolder {
		TextView name, date, temp;
		CheckBox select;
	}

}