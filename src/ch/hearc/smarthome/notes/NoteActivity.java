package ch.hearc.smarthome.notes;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import ch.hearc.smarthome.CredentialManager;
import ch.hearc.smarthome.R;
import ch.hearc.smarthome.bluetooth.SHBluetoothActivity;
import ch.hearc.smarthome.bluetooth.SHBluetoothNetworkManager;

public class NoteActivity extends SHBluetoothActivity {
	
	public 	ListView 		myListView;
	public 	int 			positionToRemove;
	public 	Message 		msg;
	public 	String 			MessageRead;
	public 	String 			currentUser;
	public 	String 			l1;
	public 	int 			nbLinesRead;
	String 	newLine=System.getProperty("line.separator"); 
			 
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_note);
        
        //Récupération de la listview créée dans le fichier main.xml
        myListView = (ListView) findViewById(R.id.listView1);
 
        //Création de la ArrayList qui nous permettra de remplire la listView
        final ArrayList<HashMap<String, String>> listItem = new ArrayList<HashMap<String, String>>();
 
        //On déclare la HashMap qui contiendra les informations pour un item
        HashMap<String, String> map;
 
        //Création d'une HashMap pour insérer les informations du premier item de notre listView
        map = new HashMap<String, String>();
 
        //Création d'un SimpleAdapter qui se chargera de mettre les items présent dans notre list (listItem) dans la vue affichageitem
        final SimpleAdapter mSchedule = new SimpleAdapter (this.getBaseContext(), listItem, R.layout.fragment_note,
               new String[] {"sujet", "date"}, new int[] {R.id.sujet, R.id.date});
        mSchedule.notifyDataSetChanged();
        //On attribut à notre listView l'adapter que l'on vient de créer
        myListView.setAdapter(mSchedule);
        /*
        	Reading message receive from Pick and receive user
        */     
        currentUser = CredentialManager.getActualUser();
        handleMessage(msg);
        if (msg.what == SHBluetoothNetworkManager.MSG_READ)
        {
        	MessageRead = ((String) msg.obj).toLowerCase();
        }
        String lines[] = MessageRead.split("\r"); 
        listItem.clear();
        Toast.makeText(getApplicationContext(), "Save found",
				Toast.LENGTH_SHORT).show();
		// Update 'listItem' array with the new list content
		for (String l : lines) 
		{
			String s[] = l.split(";");
			if(s[0] == currentUser || s[0] == "public")
			{
				map.put("sujet",s[1]);
				map.put("date",s[2]);
				map.put("detail",s[3]);
				listItem.add(map);
				map = new HashMap<String, String>();
			}
		}
		mSchedule.notifyDataSetChanged();
        
        //Enfin on met un écouteur d'évènement sur notre listView
        myListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
        	@SuppressWarnings("unchecked")
         	public void onItemClick(AdapterView<?> a, View v, int position, long id) {
				//on récupère la HashMap contenant les infos de notre item (titre, description)
        		HashMap<String, String> map = (HashMap<String, String>) myListView.getItemAtPosition(position);
        		//on créer une boite de dialogue
        		AlertDialog.Builder adb = new AlertDialog.Builder(NoteActivity.this);
        		//on attribut un titre à notre boite de dialogue
        		adb.setTitle("Sélection Item");
        		//on insère un message à notre boite de dialogue, et ici on affiche le titre de l'item cliqué
        		adb.setMessage("Détails du message : " +newLine+map.get("detail") +newLine+newLine);
        		//on indique que l'on veut le bouton ok à notre boite de dialogue
        		adb.setPositiveButton("OK", null);       		
        		//on affiche la boite de dialogue
        		adb.show();
        	}
         });

        myListView.setOnItemLongClickListener(new OnItemLongClickListener() {
        	@Override
      		public boolean onItemLongClick(AdapterView<?> a, View v, int position, long id) {
        		//HashMap<String, String> map = (HashMap<String, String>) myListView.getItemAtPosition(position);
        		//on créer une boite de dialogue
        		AlertDialog.Builder adb = new AlertDialog.Builder(NoteActivity.this);
        		//on attribut un titre à notre boite de dialogue
        		adb.setTitle("Suppression");
        		//on insère un message à notre boite de dialogue, et ici on affiche le titre de l'item cliqué
        		adb.setMessage("Voulez-vous le supprimer ?");
        		final int positionToRemove = position;
        		//on indique que l'on veut le bouton ok à notre boite de dialogue
        		adb.setPositiveButton("Supprimer", new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    	listItem.remove(positionToRemove);
                    	mSchedule.notifyDataSetChanged();
                    	//On supprime la ligne dans le fichier texte, en le réecrivant sans la ligne en question.
                    	try 
                    	{
                            nbLinesRead = 0;    
                            String lines[] = MessageRead.split("\r"); 
                            listItem.clear();
                            while (lines != null)
                            {
                            	if (nbLinesRead != positionToRemove)
                            	{
                            		l1 += lines[nbLinesRead];
                            	}
                            	nbLinesRead++;
                            }
                            write(l1);
                        }
                    	catch (Exception e)
                        {
                        	Toast.makeText(getApplicationContext(),
            						"Deleting error " + e.getMessage(),
            						Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                adb.show();
    		return true;
        	}
        });
    }
    public void onBackPressed()
    {
    	preventCancel = true;
    }
}
