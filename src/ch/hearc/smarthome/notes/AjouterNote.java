package ch.hearc.smarthome.notes;

import java.util.Calendar;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import ch.hearc.smarthome.R;
import ch.hearc.smarthome.bluetooth.SHBluetoothActivity;
import ch.hearc.smarthome.bluetooth.SHCommunicationProtocol;


public class AjouterNote extends SHBluetoothActivity {
	
	public 	String 		strDestinataire;
	public 	String 		strSujet;
	public 	String 		strDetail;
	public 	String 		date;
	public 	String 		save;
	public 	String 		currentUser;
	public	String		newDetail;
	public	String		newSujet;
	public	String		newDestinataire;
	public 	static int	kMaxLength = 8; 
	// Functions of this activity
	private String sendpost = "send post-it";
	
	private static SHCommunicationProtocol protocol;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ajouter_note);
		protocol = new SHCommunicationProtocol( );
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
		newDestinataire = completeLength(strDestinataire);
		
		EditText sujet = (EditText) findViewById(R.id.sujet);
		strSujet = sujet.getText().toString();
		sujet.setText("");
		newSujet = completeLength(strSujet);
		
		EditText detail = (EditText) findViewById(R.id.detail);
		strDetail = detail.getText().toString();
		newDetail = strDetail.replace(" ", "_");
		detail.setText("");
		
		Calendar rightNow = Calendar.getInstance();
        date = rightNow.get(Calendar.DAY_OF_MONTH)+"."+rightNow.get(Calendar.MONTH)+"."+rightNow.get(Calendar.YEAR);
                
        if(strDestinataire.matches("") || strSujet.matches("") || strDetail.matches(""))
		{
        	Toast.makeText(AjouterNote.this, "Veuillez remplir tous les champs !", Toast.LENGTH_SHORT).show();
		}
		else
		{		
	        save = newSujet + "," + newDetail + "," + date + "," + newDestinataire + "\r";
	        //CredentialManager.getActualUser()+","+save);
	        write(protocol.generateDataToSend(sendpost,save));
	        //Writing to the pick
	        //write(save);
		}      
	}
	public String completeLength (String _complete)
	{
		String toComplete = new String(_complete);
		for(int i = 0; i < (kMaxLength - _complete.length( )); i++)
		{
			toComplete += "*";
		}
		return toComplete;
	}
}
