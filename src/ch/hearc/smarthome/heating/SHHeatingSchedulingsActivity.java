package ch.hearc.smarthome.heating;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Toast;
import ch.hearc.smarthome.FileUtil;
import ch.hearc.smarthome.R;
import ch.hearc.smarthome.bluetooth.SHBluetoothNetworkManager;
import ch.hearc.smarthome.networktester.SHCommunicationProtocol;
import ch.hearc.smarthome.heating.SHBluetoothFragmentActivity;

public class SHHeatingSchedulingsActivity extends SHBluetoothFragmentActivity {

	// Save Directory
	static String SAVE_NAME = "schedulings_save.txt";
	static File SAVE_FILEPATH = new File(FileUtil.HEATING_DIR.getAbsolutePath()
			+ File.separator + SAVE_NAME);

	ListView schedulingListView;

	ArrayList<CheckBox> checkboxes = new ArrayList<CheckBox>();
	ArrayList<SHHeatingSchedulingObject> datas = new ArrayList<SHHeatingSchedulingObject>();
	SHHeatingSchedulingsArrayAdapter adapter;
	FragmentManager fm = getSupportFragmentManager();
	
	//Bluetooth
	private static SHCommunicationProtocol protocol;
	public String sBluetoothMessage;
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);

		//Bluetooth
		protocol = new SHCommunicationProtocol();
		
		setContentView(R.layout.heating_scheduling);

		// ListAdapter -> ListView
		schedulingListView = (ListView) findViewById(R.id.listViewScheduling);

		adapter = new SHHeatingSchedulingsArrayAdapter(this,
				R.layout.heating_scheduling_list_item, datas);
		schedulingListView.setAdapter(adapter);

		updateList();

	}

	/*
	 * addScheduling (View v) add a new heating scheduling to the array => save
	 * in the XML => Then sync
	 */
	public void addSchedulingDialog(View v) {

		// Create DialogFragment
		SHHeatingSchedulingsAddDialogFragment dFragment = new SHHeatingSchedulingsAddDialogFragment();
		// Show DialogFragment
		dFragment.show(fm, "Dialog Fragment");
	}

	/*
	 * deleteSelection(View v)
	 * 
	 * This deletes the checked items, called from XML
	 */
	public void deleteSelection(View v) {

		String save = "";
		int count = 0;

		// Remove selected items from list
		for (int i = datas.size() - 1; i >= 0; i--) {
			if (datas.get(i).getState()) {
				datas.remove(i);
				count++;
			}
		}

		Toast.makeText(getApplicationContext(), count + "Item(s) deleted",
				Toast.LENGTH_SHORT).show();

		// Create the String to save
		for (SHHeatingSchedulingObject s : datas) {
			save += s.getName() + ";" + s.getDate() + ";" + s.getTemp() + "\n";
		}
		
		// Sending to bluetooth
		write(protocol.generateDataToSend("h del scheduling", save));

		// Modify the file
		if (FileUtil.isMediaMounted()) {
			try {
				FileUtil.writeTextFile(SAVE_FILEPATH, save, false);
			} catch (IOException e) {
				Toast.makeText(getApplicationContext(),
						"File writing error: " + e.getMessage(),
						Toast.LENGTH_SHORT).show();
			}
		}

		// Update the listview with the modified file
		updateList();

	}

	public void updateList() {

		String content = null, info = "";

		if (FileUtil.isMediaMounted()) {
			if (FileUtil.HEATING_DIR.exists()) {
				if (SAVE_FILEPATH.exists()) {
					try {
						content = FileUtil.readTextFile(SAVE_FILEPATH);
					} catch (IOException e) {
						info = "File reading error: " + e.getMessage();
					}
				} else {
					// Create File and restart function
					FileUtil.createFile(SAVE_FILEPATH);
					updateList();
					return;
				}
			} else {
				// Create Tree and restart function
				FileUtil.createTree(FileUtil.HEATING_DIR);
				updateList();
				return;
			}
		} else {
			info = "No SD found";
		}

		if (!info.equals("")) {
			Toast.makeText(getApplicationContext(), info, Toast.LENGTH_SHORT)
					.show();
		}

		if (content.equals("")) {
			Toast.makeText(getApplicationContext(), "No save found",
					Toast.LENGTH_SHORT).show();
		} else {
			String lines[] = content.split("\n");
			// Clear 'datas'
			datas.clear();

			// Update 'datas' array with the new list content
			for (String l : lines) {
				String s[] = l.split(";");
				datas.add(new SHHeatingSchedulingObject(s[0], s[1], s[2]));
			}

			adapter.notifyDataSetChanged();
		}

	}

	@Override
	public boolean handleMessage(Message _msg) {
		
		if (_msg.what == SHBluetoothNetworkManager.MSG_READ) {
			sBluetoothMessage = ((String) _msg.obj).toLowerCase( );
		}
		return false;
	}
	
}
