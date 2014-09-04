package ch.hearc.smarthome.heating;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import ch.hearc.smarthome.R;
import ch.hearc.smarthome.bluetooth.SHBluetoothActivity;

public class SHHeatingMainActivity extends SHBluetoothActivity {

	ListView lvMenu;
	SHHeatingMainAdapter adapter;
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.heating_main_activity);
		
		lvMenu = (ListView)findViewById(R.id.heating_main_listview);
		
		ArrayList<SHHeatingMainObject> titles = new ArrayList<SHHeatingMainObject>();
		titles.add(new SHHeatingMainObject(R.drawable.scheduling, "Heating Scheduling"));
		titles.add(new SHHeatingMainObject(R.drawable.thresholds, "Thresholds Heat and Light"));
		titles.add(new SHHeatingMainObject(R.drawable.history, "History"));
		
		adapter = new SHHeatingMainAdapter(this, R.layout.heating_main_list_item, titles);

		lvMenu.setAdapter(adapter);
		lvMenu.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				intentMenu(arg2);
			}
			
		});
		
		
	}

	public void intentMenu(int position) {

		Intent i = new Intent();
		Context c = SHHeatingMainActivity.this;

		if (position == 0) {
			// TODO if SCHEDULING
			i = new Intent(c, SHHeatingSchedulingsActivity.class);
		} else if (position == 1) {
			// TODO if THRESHOLDS
			i = new Intent(c, SHHeatingThresholdsActivity.class);
		} else if (position == 2) {
			// TODO if HISTORY
			i = new Intent(c, SHHeatingHistoryListViewActivity.class);
		}

		preventCancel = true;
		// Intent to the Activity chosen
		startActivity(i);

	}

	public void intentBack(View v) {
		finish();
	}

}
