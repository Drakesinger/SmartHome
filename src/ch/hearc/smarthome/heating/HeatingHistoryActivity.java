package ch.hearc.smarthome.heating;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;
import ch.hearc.smarthome.FileUtil;
import ch.hearc.smarthome.R;

public class HeatingHistoryActivity extends Activity {

	// Save Directory
	static String SAVE_NAME = "history_save.txt";
	static File SAVE_FILEPATH = new File(FileUtil.HEATING_DIR.getAbsolutePath()
			+ File.separator + SAVE_NAME);
	
	ListView historyListView;
	ArrayList<HeatingHistoryObject> history = new ArrayList<HeatingHistoryObject>();
	HeatingHistoryArrayAdapter adapter;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.heating_history);

		historyListView = (ListView) findViewById(R.id.listViewHistory);
		adapter = new HeatingHistoryArrayAdapter(this,
				R.layout.heating_history_list_item, history);
		historyListView.setAdapter(adapter);
		
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
			history.clear();

			// Update 'datas' array with the new list content
			for (String l : lines) {
				String s[] = l.split(";");
				history.add(new HeatingHistoryObject(s[0], s[1]));
			}

			adapter.notifyDataSetChanged();
		}

	}

	public void showPastWeek(View v) {
		Toast.makeText(getApplicationContext(), "No save found !",
				Toast.LENGTH_SHORT).show();
	}

}