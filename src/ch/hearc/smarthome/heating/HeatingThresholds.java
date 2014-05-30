package ch.hearc.smarthome.heating;

import ch.hearc.smarthome.R;
import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class HeatingThresholds extends Activity {
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.heating_thresholds);
	}
	
	public void saveThresholds(View v){
		Toast.makeText(getApplicationContext(), "Thresholds saved !", Toast.LENGTH_SHORT).show();
		finish();
	}
	
	@Override
	public void onBackPressed() {
	   DialogFragment dialog = new HeatingThresholdsSaveDialogFragment();
	   dialog.show(getFragmentManager(), "DialogThresholdsNotSaved");
	}
}
