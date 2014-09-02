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

public class HeatingHistoryGraphicalViewActivity extends Activity {

	GraphicalView chartView;

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);

		ArrayList<String> dates = getIntent().getExtras().getStringArrayList(
				"dates");
		ArrayList<String> tempsIn = getIntent().getExtras().getStringArrayList(
				"tempsIn");
		ArrayList<String> tempsOut = getIntent().getExtras()
				.getStringArrayList("tempsOut");

		ArrayList<HeatingHistoryObject> historyIn = new ArrayList<HeatingHistoryObject>();
		ArrayList<HeatingHistoryObject> historyOut = new ArrayList<HeatingHistoryObject>();

		for (int i = 0; i < dates.size(); i++) {
			historyIn
					.add(new HeatingHistoryObject(dates.get(i), tempsIn.get(i)));
			historyOut.add(new HeatingHistoryObject(dates.get(i), tempsOut
					.get(i)));
		}

		chartView = createGraphicalView(historyIn, historyOut);

		setContentView(chartView);
	}

	public GraphicalView createGraphicalView(
			ArrayList<HeatingHistoryObject> indoor,
			ArrayList<HeatingHistoryObject> outdoor) {

		XYSeries tempsIn = new XYSeries("Indoor temps");
		XYSeries tempsOut = new XYSeries("Outdoor temps");

		for (int i = 0; i < indoor.size(); i++) {
			tempsIn.add(i, Integer.parseInt(indoor.get(i).getTemp()));
			tempsOut.add(i, Integer.parseInt(outdoor.get(i).getTemp()));
		}

		ArrayList<XYSeries> series = new ArrayList<XYSeries>();
		series.add(tempsIn);
		series.add(tempsOut);

		// Now we create the renderer
		XYSeriesRenderer rendererIn = new XYSeriesRenderer();
		rendererIn.setLineWidth(4);
		rendererIn.setColor(Color.RED);
		// Include low and max value
		rendererIn.setDisplayBoundingPoints(true);
		// we add point markers
		rendererIn.setPointStyle(PointStyle.CIRCLE);
		rendererIn.setPointStrokeWidth(20);
		
		
		XYSeriesRenderer rendererOut = new XYSeriesRenderer();
		rendererOut.setLineWidth(4);
		rendererOut.setColor(Color.BLUE);
		// Include low and max value
		rendererOut.setDisplayBoundingPoints(true);
		// we add point markers
		rendererOut.setPointStyle(PointStyle.CIRCLE);
		rendererOut.setPointStrokeWidth(20);
		

		XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();
		mRenderer.addSeriesRenderer(0, rendererIn);
		mRenderer.addSeriesRenderer(1, rendererOut);
		XYMultipleSeriesDataset mDataset = new XYMultipleSeriesDataset();
		mDataset.addAllSeries(series);

		// We want to avoid black border
		int[] margins = { 100, 100, 100, 100 };
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
