package ch.hearc.smarthome;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import ch.hearc.smarthome.bluetooth.SHBluetoothActivity;
import ch.hearc.smarthome.door.DoorActivity;
import ch.hearc.smarthome.heating.HeatingMainActivity;
import ch.hearc.smarthome.networktester.SHBluetoothTesting;
import ch.hearc.smarthome.notes.NoteMenu;

/** Activity containing the links to our 4 other activities */
public class HomeActivity extends SHBluetoothActivity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		TextView textView = new TextView(this);
		textView.setTextSize(35);
		textView.setText("Welcome home");

		setContentView(textView);
		setContentView(R.layout.fragment_home);
	}

	/*
	 * Not needed
	 * @Override
	 * public boolean onCreateOptionsMenu(Menu menu) {
	 * // Inflate the menu; this adds items to the action bar if it is present.
	 * getMenuInflater().inflate(R.menu.login, menu);
	 * return true;
	 * }
	 */

	public void DoorMainActivity(View view)
	{
		Intent intent = new Intent(this, DoorActivity.class);
		preventCancel = true;
		startActivity(intent);
	}

	public void HeatMainActivity(View view)
	{
		Intent intent = new Intent(this, HeatingMainActivity.class);
		preventCancel = true;
		startActivity(intent);
	}

	public void NotesMainActivity(View view)
	{
		Intent intent = new Intent(this, NoteMenu.class);
		preventCancel = true;
		startActivity(intent);
	}

	public void AlarmMainActivity(View view)
	{
		// Intent intent = new Intent(this, AlarmeMainActivity.class);
		// preventCancel = true;
		// startActivity(intent);
	}

	public void OnTesteLeBluetoothMagueule(View v)
	{
		Intent intent = new Intent(this, SHBluetoothTesting.class);
		preventCancel = true;
		startActivity(intent);
	}
}
