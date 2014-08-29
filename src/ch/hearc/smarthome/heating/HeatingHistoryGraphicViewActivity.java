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
import android.graphics.Color;
import android.os.Bundle;

public class HeatingHistoryGraphicViewActivity extends Activity {

GraphicalView chartView;
	
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);

		ArrayList<String> dates = getIntent().getExtras().getStringArrayList("dates");
		ArrayList<String> temps = getIntent().getExtras().getStringArrayList("temps");
		
		ArrayList<HeatingHistoryObject> history = new ArrayList<HeatingHistoryObject>();
		
		for(int i = 0; i < dates.size(); i++){
			history.add(new HeatingHistoryObject(dates.get(i), temps.get(i)));
		}

		chartView = createGraphicalView(history);

		setContentView(chartView);
	}
	
	public GraphicalView createGraphicalView(
			ArrayList<HeatingHistoryObject> datas) {

		XYSeries temps = new XYSeries("Week's temps");

		for (int i = 0; i < datas.size(); i++) {
			temps.add(i, Integer.parseInt(datas.get(i).getTemp()));
		}
		// Now we create the renderer
		XYSeriesRenderer renderer = new XYSeriesRenderer();
		renderer.setLineWidth(4);
		renderer.setColor(Color.RED);
		// Include low and max value
		renderer.setDisplayBoundingPoints(true);
		// we add point markers
		renderer.setPointStyle(PointStyle.CIRCLE);
		renderer.setPointStrokeWidth(20);

		XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();
		mRenderer.addSeriesRenderer(renderer);
		XYMultipleSeriesDataset mDataset = new XYMultipleSeriesDataset();
		mDataset.addSeries(temps);

		// We want to avoid black border
		int[] margins = {100,100,100,100};
		mRenderer.setMargins(margins);
		mRenderer.setMarginsColor(Color.argb(0x00, 0xff, 0x00, 0x00));
		mRenderer.setLegendTextSize(40);
		mRenderer.setXTitle("Days");
		mRenderer.setYTitle("Temperature");
		mRenderer.setDisplayValues(true);
		mRenderer.setAxisTitleTextSize(50);
		// Disable Pan on two axis
		mRenderer.setPanEnabled(false, false);
		mRenderer.setYAxisMax(35);
		mRenderer.setYAxisMin(10);
		mRenderer.setShowGrid(true); // we show the grid

		return ChartFactory.getLineChartView(getApplicationContext(), mDataset,
				mRenderer);

	}
	
}
