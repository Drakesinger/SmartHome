package ch.hearc.smarthome.heating;

import java.util.ArrayList;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import ch.hearc.smarthome.R;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;

public class SHHeatingHistoryGraphicalViewActivity extends Activity {

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


		XYSeries seriesIn = new XYSeries("Indoor's temperatures");
		XYSeries seriesOut = new XYSeries("Outdoor's temperatures");

		for(int i = 0; i < 28; i++){
			seriesIn.add(i, Double.valueOf(tempsIn.get(i)));
			seriesOut.add(i, Double.valueOf(tempsOut.get(i)));
		}

//		ArrayList<HeatingHistoryObject> historyIn = new ArrayList<HeatingHistoryObject>();
//		ArrayList<HeatingHistoryObject> historyOut = new ArrayList<HeatingHistoryObject>();
		// Init. histories
//		for (int i = 0; i < 7; i++) {
//			seriesIn.add(new XYSeries("Indoor temps"));
//			seriesOut.add(new XYSeries("Outdoor temps"));
//			for (int j = 0; j < 4; j++) {
//				double tempIn = Double.valueOf(tempsIn.get((i * 4) + j));
//				double tempOut = Double.valueOf(tempsOut.get((i * 4) + j));
//				seriesOut.get(i).add(j, tempIn);
//				seriesOut.get(i).add(j, tempOut);
//			}
//			historyIn.add(new HeatingHistoryObject(dates.get(i), seriesIn
//					.get(i)));
//			historyOut.add(new HeatingHistoryObject(dates.get(i), seriesOut
//					.get(i)));
//		}

		chartView = createGraphicalView(seriesIn, seriesOut);

		setContentView(chartView);
	}

	public GraphicalView createGraphicalView(XYSeries indoor, XYSeries outdoor) {

		ArrayList<XYSeries> series = new ArrayList<XYSeries>();
		series.add(indoor);
		series.add(outdoor);

		// Now we create the renderer
		XYSeriesRenderer rendererIn = new XYSeriesRenderer();
		rendererIn.setLineWidth(4);
		rendererIn.setColor(getResources().getColor(R.color.blue01));
		// Include low and max value
		rendererIn.setDisplayBoundingPoints(true);
		// we add point markers
		rendererIn.setPointStyle(PointStyle.CIRCLE);
		rendererIn.setPointStrokeWidth(20);

		XYSeriesRenderer rendererOut = new XYSeriesRenderer();
		rendererOut.setLineWidth(4);
		rendererOut.setColor(getResources().getColor(R.color.blue08));
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
