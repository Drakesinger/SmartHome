package ch.hearc.smarthome;

import ch.hearc.smarthome.door.DoorActivity;
import ch.hearc.smarthome.heating.HeatingMainActivity;
import ch.hearc.smarthome.notes.NoteActivity;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;


public class HomeActivity extends Activity {
	public final static String EXTRA_MESSAGENOTE = "com.example.myfirstapp.MESSAGE";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		 
		 TextView textView = new TextView(this);
		 textView.setTextSize(35);
		 textView.setText("Bienvenue à la maison ");

		 setContentView(textView);
		 setContentView(R.layout.fragment_home);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}
	
	public void DoorMainActivity(View view)
	{
		Intent intent = new Intent(this, DoorActivity.class);
		startActivity(intent);
	}	
	
	public void HeatMainActivity(View view)
	{
		Intent intent = new Intent(this, HeatingMainActivity.class);
		startActivity(intent);
	}
	
	public void NotesMainActivity(View view)
	{
		Intent intent = new Intent(this, NoteActivity.class);
		startActivity(intent);
	}
	
	public void AlarmMainActivity(View view)
	{
		//Intent intent = new Intent(this, AlarmeMainActivity.class);
	}
}
