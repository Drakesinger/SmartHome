package ch.hearc.smarthome.heating;

import ch.hearc.smarthome.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class HeatingHistory extends Activity{

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.heating_history);
	}
	
	public void showPastWeek(View v){
		
		Toast.makeText(getApplicationContext(), "No save found !" , Toast.LENGTH_SHORT).show();
		
	}
	
}
