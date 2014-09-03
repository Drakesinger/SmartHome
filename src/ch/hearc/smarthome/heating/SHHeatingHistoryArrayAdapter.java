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
import android.widget.LinearLayout;
import android.widget.TextView;
import ch.hearc.smarthome.R;

public class SHHeatingHistoryArrayAdapter extends
		ArrayAdapter<SHHeatingHistoryObject> {

	Context context;
	int resource;
	ArrayList<SHHeatingHistoryObject> history = null;
	
	int ITERATOR = 0;

	public SHHeatingHistoryArrayAdapter(Context context, int resource,
			ArrayList<SHHeatingHistoryObject> history) {
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
			holder.graphLayout = (LinearLayout) row.findViewById(R.id.heating_history_list_graph);

			row.setTag(holder);
		} else {
			holder = (HistoryHolder) row.getTag();
		}
		
		
		
		// Getting the history object
		final SHHeatingHistoryObject historyObject = history.get(position);
		
		// Creating the chart
		XYSeries test = historyObject.getTemps();
		
		XYSeriesRenderer rendererOut = new XYSeriesRenderer();
		rendererOut.setLineWidth(4);
		rendererOut.setColor(((Activity) context).getResources().getColor(R.color.blue08));
		// Include low and max value
		rendererOut.setDisplayBoundingPoints(true);
		// we add point markers
		rendererOut.setPointStyle(PointStyle.CIRCLE);
		rendererOut.setPointStrokeWidth(5);
		

		XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();
		mRenderer.addSeriesRenderer(rendererOut);
		XYMultipleSeriesDataset mDataset = new XYMultipleSeriesDataset();
		mDataset.addSeries(test);

		// We want to avoid black border
		int[] margins = { 100, 100, 100, 100 };
		mRenderer.setMargins(margins);
		mRenderer.setMarginsColor(Color.argb(0x00, 0xff, 0x00, 0x00));
		mRenderer.setDisplayValues(true);
		// Disable Pan on two axis
		mRenderer.setPanEnabled(false, false);
		mRenderer.setYAxisMax(35);
		mRenderer.setYAxisMin(10);
		mRenderer.setShowGrid(true); // we show the grid

		GraphicalView gv = ChartFactory.getLineChartView(((Activity) context), mDataset, mRenderer);
	    holder.graphLayout.addView(gv);
		
		holder.date.setText(historyObject.getDate());
		holder.temp.setText("Midi: " + historyObject.getTemps().getY(1) + "° C");
		
		return row;
	}

	static class HistoryHolder {
		TextView date, temp;
		LinearLayout graphLayout;
	}

}
