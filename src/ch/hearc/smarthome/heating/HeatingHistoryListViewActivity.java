package ch.hearc.smarthome.heating;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ListView;
import ch.hearc.smarthome.FileUtil;
import ch.hearc.smarthome.R;
import ch.hearc.smarthome.bluetooth.SHBluetoothActivity;

public class HeatingHistoryListViewActivity extends SHBluetoothActivity {

	// Save Directory
	static String SAVE_NAME = "history_save.txt";
	static File SAVE_FILEPATH = new File(FileUtil.HEATING_DIR.getAbsolutePath()
			+ File.separator + SAVE_NAME);

	// List View
	private ListView historyListView;
	private ArrayList<HeatingHistoryObject> historyIndoorTemp = new ArrayList<HeatingHistoryObject>();
	private ArrayList<HeatingHistoryObject> historyOutdoorTemp = new ArrayList<HeatingHistoryObject>();
	private HeatingHistoryArrayAdapter adapter;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.heating_history);

		historyListView = (ListView) findViewById(R.id.listViewHistory);
		adapter = new HeatingHistoryArrayAdapter(this,
				R.layout.heating_history_list_item, historyIndoorTemp);
		historyListView.setAdapter(adapter);

		updateList();

	}

	public void updateList() {

		// Update 'datas' array with the new list content
		historyIndoorTemp.clear();
		ArrayList<String> datas = populate();

		Date firstDate = null;
		GregorianCalendar calendar = new java.util.GregorianCalendar();

		try {
			firstDate = stringToDate(datas.get(0), "dd.MM.yy - hh:mm");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		calendar.setTime(firstDate);

		for (int i = 1; i <= 28; i += 4) {
			historyIndoorTemp.add(new HeatingHistoryObject(calendar.getTime()
					.toString(), datas.get(i)));
			calendar.add(GregorianCalendar.DATE, +1);
		}

		adapter.notifyDataSetChanged();

	}

	public static String dateToString(Date dDate, String sFormat)
			throws Exception {

		SimpleDateFormat df = new SimpleDateFormat(sFormat);
		Date date = GregorianCalendar.getInstance().getTime();
		
		return df.format(date);
	}

	public static Date stringToDate(String sDate, String sFormat)
			throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat(sFormat);
		return sdf.parse(sDate);
	}

	public ArrayList<String> populate() {
		ArrayList<String> datas = new ArrayList<String>();

		datas.add("01.09.14 - 18:00");

		int r;

		for (int i = 0; i < 28; i++) {
			r = (int) (Math.random() * 25) + 10;
			datas.add(String.valueOf(r));
		}

		return datas;
	}

	public void showGraphicalView(View v) {
		Intent i = new Intent(this, HeatingHistoryGraphicalViewActivity.class);

		ArrayList<String> dates = new ArrayList<String>();
		ArrayList<String> temps = new ArrayList<String>();

		for (HeatingHistoryObject h : historyIndoorTemp) {
			dates.add(h.getDate());
			temps.add(h.getTemp());
		}

		i.putStringArrayListExtra("dates", dates);
		i.putStringArrayListExtra("temps", temps);

		// Bluetooth stuff
		preventCancel = true;

		// And then start
		startActivity(i);
	}

}
