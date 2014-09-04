package ch.hearc.smarthome.notes;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import ch.hearc.smarthome.R;
import ch.hearc.smarthome.bluetooth.SHBluetoothActivity;

public class SHNoteMenu extends SHBluetoothActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_note_menu);
	}

	public void SupprimerNoteActivity(View view) {
		Intent intent = new Intent(SHNoteMenu.this, SHNoteActivity.class);
		preventCancel = true;
		startActivity(intent);
	}

	public void AjouterNoteActivity(View view) {
		Intent intent = new Intent(SHNoteMenu.this, SHAddNote.class);
		preventCancel = true;
		startActivity(intent);
	}
}
