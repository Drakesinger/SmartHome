package ch.hearc.smarthome.heating;


import ch.hearc.smarthome.R;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

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
			i = new Intent(c, HeatingScheduling.class);
		} else if (v.getId() == R.id.buttonThresholds) {
			// TODO if THRESHOLDS
			i = new Intent(c, HeatingThresholds.class);
		} else if (v.getId() == R.id.buttonHistory) {
			// TODO if HISTORY
			i = new Intent(c, HeatingHistory.class);
		}

		// Intent to the Activity chosen
		startActivity(i);

	}

}
