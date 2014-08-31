package ch.hearc.smarthome.notes;

import java.util.Calendar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import ch.hearc.smarthome.FileUtil;
import ch.hearc.smarthome.R;
import ch.hearc.smarthome.bluetooth.SHBluetoothActivity;


public class AjouterNote extends SHBluetoothActivity {
	
	public 	String 		strDestinataire;
	public 	String 		strSujet;
	public 	String 		strDetail;
	public 	String 		date;
	public 	String 		save;
	public 	String 		currentUser;

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
		strDestinataire = destinataire.getText().toString();
		destinataire.setText("");
		
		EditText sujet = (EditText) findViewById(R.id.sujet);
		strSujet = sujet.getText().toString();
		sujet.setText("");
		
		EditText detail = (EditText) findViewById(R.id.detail);
		strDetail = detail.getText().toString();
		detail.setText("");
		
		Calendar rightNow = Calendar.getInstance();
        date = rightNow.get(Calendar.DAY_OF_MONTH)+"/"+rightNow.get(Calendar.MONTH)+"/"+rightNow.get(Calendar.YEAR);
        
        if(strDestinataire.matches("") || strSujet.matches("") || strDetail.matches(""))
		{
        	Toast.makeText(AjouterNote.this, "Veuillez remplir tous les champs !", Toast.LENGTH_SHORT).show();
		}
		else
		{		
	        if(FileUtil.isExternalStorageWritable() == false)
	        {
	        	Toast.makeText(AjouterNote.this, "No MEDIA", Toast.LENGTH_SHORT).show();
	        }
	        else
	        {
		        save += strDestinataire + ";" + strSujet + ";" + date + ";" + strDetail;
		        
		        //Writing to the pick
		        write(save);
		        
		        Intent intent = new Intent(AjouterNote.this, NoteActivity.class);
		        preventCancel = true;
		    	startActivity(intent);
	        }
		}      
	}
}
