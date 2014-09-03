package ch.hearc.smarthome;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import ch.hearc.smarthome.bluetooth.SHBluetoothActivity;
import ch.hearc.smarthome.debuging.SHBluetoothDebugger;
import ch.hearc.smarthome.door.DoorActivity;
import ch.hearc.smarthome.heating.SHHeatingMainActivity;
import ch.hearc.smarthome.notes.SHNoteMenu;

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
			i.setClass(this, SHHeatingMainActivity.class);
		} else if (v.getId() == R.id.main_note_button) {
			i.setClass(this, SHNoteMenu.class);
		} else if (v.getId() == R.id.main_alarm_button) {

			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Title");
			builder.setMessage("Message")
					.setPositiveButton(R.string.yes,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									// TODO if YES
								}
							})
					.setNegativeButton(R.string.no_quit,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									// TODO if NO
								}
							});
			AlertDialog dialog = builder.create();
			dialog.show();

		} else if (v.getId() == R.id.main_bluetooth_button) {
			i.setClass(this, SHBluetoothDebugger.class);
		}

		// Delete this condition when alarm activity is created
		if (v.getId() != R.id.main_alarm_button) {
			preventCancel = true;
			startActivity(i);
		}
	}
}
