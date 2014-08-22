package ch.hearc.smarthome.heating;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import ch.hearc.smarthome.HomeActivity;
import ch.hearc.smarthome.R;

public class HeatingMainActivity extends Activity {

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.heating_main_activity);

	}

	public void intentMenu(View v) {

		Intent i = new Intent();
		Context c = HeatingMainActivity.this;

		if (v.getId() == R.id.buttonScheduling) {
			// TODO if SCHEDULING
			i = new Intent(c, HeatingSchedulingsActivity.class);
		} else if (v.getId() == R.id.buttonThresholds) {
			// TODO if THRESHOLDS
			i = new Intent(c, HeatingThresholdsActivity.class);
		} else if (v.getId() == R.id.buttonHistory) {
			// TODO if HISTORY
			i = new Intent(c, HeatingHistory.class);
		} else if (v.getId() == R.id.buttonBack) {
			
			// this will be a return
			i = new Intent(c, HomeActivity.class);
		}

		// Intent to the Activity chosen
		startActivity(i);

	}

}
