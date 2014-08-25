package ch.hearc.smarthome.heating;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Toast;
import ch.hearc.smarthome.R;

public class HeatingSchedulingsActivity extends FragmentActivity {

	final File SAVE_DIR = new File(Environment
			.getExternalStorageDirectory().getAbsolutePath()
			+ File.separator
			+ "data"
			+ File.separator
			+ "SmartHome"
			+ File.separator
			+ "Heating");

	ListView schedulingListView;
	ArrayList<CheckBox> checkboxes = new ArrayList<CheckBox>();
	ArrayList<HeatingScheduling> datas = new ArrayList<HeatingScheduling>();
	HeatingSchedulingsArrayAdapter adapter;
	FragmentManager fm = getSupportFragmentManager();

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.heating_scheduling);

		ArrayList<String> namesList = new ArrayList<String>();
		ArrayList<String> datesList = new ArrayList<String>();
		ArrayList<String> tempsList = new ArrayList<String>();

		/* Get Scheduling save */
		String state = Environment.getExternalStorageState();
		if (!state.equals(Environment.MEDIA_MOUNTED)) {
			Toast.makeText(getApplicationContext(),
					"No external storage mounted", Toast.LENGTH_SHORT).show();
		} else {
			SAVE_DIR.mkdir();
			File textFile = new File(SAVE_DIR + File.separator
					+ "schedulings_save.txt");

			// Check if a save exist
			if (!textFile.exists()) {

				Toast.makeText(getApplicationContext(),
						"There isn't any scheduled heating", Toast.LENGTH_SHORT)
						.show();

			} else {
				try {
					// Reading file
					String fileContent = readTextFile(textFile);
					String lines[] = fileContent.split("\n");
					// List<String> list = new ArrayList<String>();
					for (int i = 0; i < lines.length; i++) {
						String line[] = lines[i].split(";");
						namesList.add(line[0]);
						datesList.add(line[1]);
						tempsList.add(line[2]);
					}

				} catch (IOException e) {
					Toast.makeText(getApplicationContext(),
							"Something went wrong! " + e.getMessage(),
							Toast.LENGTH_SHORT).show();

				}
			}
		}

		// Put the datas in the ArrayList
		for (int i = 0; i < namesList.size(); i++) {
			datas.add(new HeatingScheduling(namesList.get(i), datesList.get(i),
					tempsList.get(i)));
		}

		// ListAdapter -> ListView
		schedulingListView = (ListView) findViewById(R.id.listViewScheduling);
		adapter = new HeatingSchedulingsArrayAdapter(this,
				R.layout.heating_scheduling_list_item, datas);
		schedulingListView.setAdapter(adapter);

	}

	/*
	 * addScheduling (View v) add a new heating scheduling to the array => save
	 * in the XML => Then sync
	 */
	public void addSchedulingDialog(View v) {

		/*
		 * DialogFragment dialog = new HeatingThresholdsSaveDialogFragment();
		 * dialog.show(getFragmentManager(), "AddSchedulingDialogFragment");
		 */

		HeatingSchedulingsAddDialogFragment dFragment = new HeatingSchedulingsAddDialogFragment();
		// Set Target
		dFragment.setTargetFragment(dFragment, 1);
		// Show DialogFragment
		dFragment.show(fm, "Dialog Fragment");

	}

	/*
	 * deleteSelection(View v)
	 * 
	 * This deletes the checked items
	 */
	public void deleteSelection(View v) {

		int size = datas.size();
		for (int i = size - 1; i >= 0; i--) {
			if (datas.get(i).state) {
				datas.remove(i);
			}
		}

		for (int i = 0; i < datas.size(); i++) {
			datas.get(i).state = false;
		}

		adapter.notifyDataSetChanged();

		Toast.makeText(getApplicationContext(), "Deleting", Toast.LENGTH_SHORT)
				.show();
		// Log.d("CheckState", s);
	}

	/**** Reading Text File ****/
	private String readTextFile(File file) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(file));
		StringBuilder text = new StringBuilder();
		String line;
		while ((line = reader.readLine()) != null) {
			text.append(line);
			text.append("\n");
		}
		reader.close();
		return text.toString();
	}

}
