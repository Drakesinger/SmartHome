package ch.hearc.smarthome.notes;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

import ch.hearc.smarthome.FileUtil;
import ch.hearc.smarthome.notes.NoteActivity;
import ch.hearc.smarthome.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


public class AjouterNote extends Activity {
	
	public String strDestinataire;
	public String strSujet;
	public String strDetail;
	public String date;
	public String save;
	
	// Save Directory
		static File MAIN_DIR = new File(Environment.getExternalStorageDirectory()
				.getAbsolutePath()
				+ File.separator
				+ "data"
				+ File.separator
				+ "SmartHome" + File.separator);

		static File NOTE_DIR = new File(MAIN_DIR.getAbsolutePath()
				+ File.separator + "Note" + File.separator);
		static String SAVE_NAME = "Note.txt";
		static File SAVE_FILEPATH = new File(NOTE_DIR.getAbsolutePath()
				+ File.separator + SAVE_NAME);

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
		        save += strDestinataire + ";" + strSujet + ";" + date + ";" + strDetail + "\n";
		        WriteSettings(getBaseContext());
		        Intent intent = new Intent(AjouterNote.this, NoteActivity.class);
		    	startActivity(intent);
	        }
		}      
	}
	public void WriteSettings(Context context){  
      //Modify the file
      		if (FileUtil.isMediaMounted()) {
      			try {
      				FileUtil.writeTextFile(SAVE_FILEPATH, save, false);
      			} catch (IOException e) {
      				Toast.makeText(getApplicationContext(),
      						"File writing error: " + e.getMessage(),
      						Toast.LENGTH_SHORT).show();
      			}
      		} 
       }
	
}
