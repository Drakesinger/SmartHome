package ch.hearc.smarthome.heating;

import java.util.ArrayList;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import ch.hearc.smarthome.R;

public class SHHeatingMainAdapter extends ArrayAdapter<SHHeatingMainObject> {

	Context context;
	int resource;
	ArrayList<SHHeatingMainObject> titles;

	public SHHeatingMainAdapter(Context context, int resource,
			ArrayList<SHHeatingMainObject> titles) {
		super(context, resource, titles);

		this.context = context;
		this.resource = resource;
		this.titles = titles;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View row = convertView;
		HistoryHolder holder = null;

		if (row == null) {
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			row = inflater.inflate(resource, parent, false);

			holder = new HistoryHolder();
			holder.icon = (ImageView) row
					.findViewById(R.id.heating_main_list_icon);
			holder.title = (TextView) row
					.findViewById(R.id.heating_main_list_title);

			row.setTag(holder);
		} else {
			holder = (HistoryHolder) row.getTag();
		}

		// Getting the history object
		final SHHeatingMainObject t = titles.get(position);

		holder.icon.setImageResource(t.getDrawable());
		holder.title.setText(t.getTitle());

		return row;
	}

	static class HistoryHolder {
		ImageView icon;
		TextView title;
	}

}
