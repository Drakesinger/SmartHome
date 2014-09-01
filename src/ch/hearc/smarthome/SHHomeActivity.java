package ch.hearc.smarthome;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import ch.hearc.smarthome.bluetooth.SHBluetoothActivity;
import ch.hearc.smarthome.door.DoorActivity;
import ch.hearc.smarthome.heating.HeatingMainActivity;
import ch.hearc.smarthome.networktester.SHBluetoothTesting;
import ch.hearc.smarthome.notes.NoteMenu;

/** Activity containing the links to our 4 other activities */
public class SHHomeActivity extends SHBluetoothActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		TextView textView = new TextView(this);
		textView.setTextSize(35);
		textView.setText("Welcome home");

		setContentView(textView);
		setContentView(R.layout.home_screen);
	}

	public void startMenu(View v) {
		Intent i = new Intent();

		if (v.getId() == R.id.main_door_button) {
			i.setClass(this, DoorActivity.class);
		} else if (v.getId() == R.id.main_heating_button) {
			i.setClass(this, HeatingMainActivity.class);
		} else if (v.getId() == R.id.main_note_button) {
			i.setClass(this, NoteMenu.class);
		} else if (v.getId() == R.id.main_alarm_button) {
			// nahp
		} else if (v.getId() == R.id.main_bluetooth_button) {
			i.setClass(this, SHBluetoothTesting.class);
		}
		
		
		//Delete this condition when alarm activity is created
		if(v.getId()!=R.id.main_alarm_button){
			preventCancel = true;
			startActivity(i);
		}
	}
}
