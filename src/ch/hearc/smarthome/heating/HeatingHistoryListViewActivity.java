package ch.hearc.smarthome.heating;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import ch.hearc.smarthome.FileUtil;
import ch.hearc.smarthome.R;
import ch.hearc.smarthome.bluetooth.SHBluetoothActivity;

public class HeatingHistoryListViewActivity extends SHBluetoothActivity {

	// Dates
	String DATE_FORMAT = "dd.MM.yyyy HH:mm";

	// Save Directory
	static String SAVE_NAME = "history_save.txt";
	static File SAVE_FILEPATH = new File(FileUtil.HEATING_DIR.getAbsolutePath()
			+ File.separator + SAVE_NAME);

	// List View
	private HeatingHistoryArrayAdapter adapterIn, adapterOut;
	private ListView lvHistoryIn, lvHistoryOut;

	// History
	private ArrayList<HeatingHistoryObject> historyIn = new ArrayList<HeatingHistoryObject>();
	private ArrayList<HeatingHistoryObject> historyOut = new ArrayList<HeatingHistoryObject>();
	private ArrayList<HeatingHistoryObject> displayIn = new ArrayList<HeatingHistoryObject>();
	private ArrayList<HeatingHistoryObject> displayOut = new ArrayList<HeatingHistoryObject>();

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.heating_history);

		lvHistoryIn = (ListView) findViewById(R.id.listview_history_indoor);
		lvHistoryOut = (ListView) findViewById(R.id.listview_history_outdoor);
		adapterIn = new HeatingHistoryArrayAdapter(this,
				R.layout.heating_history_list_item, displayIn);
		adapterOut = new HeatingHistoryArrayAdapter(this,
				R.layout.heating_history_list_item, displayOut);
		lvHistoryIn.setAdapter(adapterIn);
		lvHistoryOut.setAdapter(adapterOut);

		historyIn = populate();
		historyOut = populate();

		updateLists();

	}

	public void updateLists() {

		Date firstDate = null;
		Calendar c = new GregorianCalendar();

		try {
			firstDate = stringToDate(historyIn.get(0).getDate(), DATE_FORMAT);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		c.setTime(firstDate);

		String sDate = null;

		for (int i = 1; i <= 28; i += 4) {
			sDate = historyIn.get(i).getDate().split(" ")[0];
			HeatingHistoryObject hhoIn = new HeatingHistoryObject(sDate,
					historyIn.get(i).getTemp());
			HeatingHistoryObject hhoOut = new HeatingHistoryObject(sDate,
					historyOut.get(i).getTemp());
			displayIn.add(hhoIn);
			displayOut.add(hhoOut);
		}

		adapterIn.notifyDataSetChanged();
		adapterOut.notifyDataSetChanged();

	}

	public static Date stringToDate(String sDate, String sFormat)
			throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat(sFormat, Locale.FRANCE);

		return sdf.parse(sDate);
	}

	public static String dateToString(Date dDate, String sFormat)
			throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat(sFormat, Locale.FRANCE);
		return sdf.format(dDate);
	}

	public ArrayList<HeatingHistoryObject> populate() {
		ArrayList<HeatingHistoryObject> datas = new ArrayList<HeatingHistoryObject>();
		Calendar c = new GregorianCalendar();

		// Get 1 week before
		c.add(Calendar.DATE, -7);

		// Fill array w/ random values
		String sRand = null;
		String sDate = null;

		for (int i = 0; i < 28; i++) {
			// Random temp
			sRand = String.valueOf((int) (Math.random() * 15) + 15);
			// Date
			try {
				sDate = dateToString(c.getTime(), DATE_FORMAT);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			datas.add(new HeatingHistoryObject(sDate, sRand));

			c.add(Calendar.HOUR, +6);
		}

		return datas;
	}

	public void showGraphicalView(View v) {
		Intent intent = new Intent(this,
				HeatingHistoryGraphicalViewActivity.class);

		ArrayList<String> dates = new ArrayList<String>();
		ArrayList<String> tempsIndoor = new ArrayList<String>();
		ArrayList<String> tempsOutdoor = new ArrayList<String>();

		int size = historyIn.size();
		for (int i = 0; i < size; i++) {

			dates.add(historyIn.get(i).getDate());
			tempsIndoor.add(historyIn.get(i).getTemp());
			tempsOutdoor.add(historyOut.get(i).getTemp());

		}

		intent.putStringArrayListExtra("dates", dates);
		intent.putStringArrayListExtra("tempsIn", tempsIndoor);
		intent.putStringArrayListExtra("tempsOut", tempsOutdoor);

		// Bluetooth stuff
		preventCancel = true;

		// And then start
		startActivity(intent);
	}

}
