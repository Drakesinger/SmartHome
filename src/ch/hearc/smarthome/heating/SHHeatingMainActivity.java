package ch.hearc.smarthome.heating;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import ch.hearc.smarthome.R;
import ch.hearc.smarthome.bluetooth.SHBluetoothActivity;

public class SHHeatingMainActivity extends SHBluetoothActivity {

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.heating_main_activity);
		
	}

	public void intentMenu(View v) {

		Intent i = new Intent();
		Context c = SHHeatingMainActivity.this;

		if (v.getId() == R.id.buttonScheduling) {
			// TODO if SCHEDULING
			i = new Intent(c, SHHeatingSchedulingsActivity.class);
		} else if (v.getId() == R.id.buttonThresholds) {
			// TODO if THRESHOLDS
			i = new Intent(c, SHHeatingThresholdsActivity.class);
		} else if (v.getId() == R.id.buttonHistory) {
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
