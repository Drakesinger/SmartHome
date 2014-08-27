package ch.hearc.smarthome.notes;

import java.util.Calendar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

public class AjouterNote extends Activity {
	
	public EditText strDestinataire;
	public EditText strSujet;
	public EditText strDetail;
	public String date;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ajouter_note);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.ajouter_note, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void AddNote(View view)
	{
		EditText destinataire =(EditText) findViewById(R.id.destinataire);
		String strDestinataire = destinataire.getText().toString();
		destinataire.setText("");
		
		EditText sujet = (EditText) findViewById(R.id.sujet);
		String strSujet = sujet.getText().toString();
		sujet.setText("");
		
		EditText detail = (EditText) findViewById(R.id.detail);
		String strDetail = detail.getText().toString();
		detail.setText("");
		
		Calendar rightNow = Calendar.getInstance();
        date = rightNow.get(Calendar.DAY_OF_MONTH)+"/"+rightNow.get(Calendar.MONTH)+"/"+rightNow.get(Calendar.YEAR);
		
        
        Intent intent = new Intent(AjouterNote.this, NoteActivity.class);
		intent.putExtra("destinataire", strDestinataire);
		intent.putExtra("sujet", strSujet);
		intent.putExtra("detail", strDetail);
		intent.putExtra("date", date);
		
		startActivity(intent);
        
	}
}
