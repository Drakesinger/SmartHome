package ch.hearc.smarthome.notes;

import java.util.ArrayList;
import java.util.HashMap;

import ch.hearc.smarthome.R;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import java.util.ArrayList;
import java.util.HashMap;

import ch.hearc.smarthome.FileUtil;
import ch.hearc.smarthome.notes.NoteActivity;

import android.app.Activity;
import android.app.AlertDialog;

import android.content.DialogInterface;

import android.os.Bundle;
import android.os.Environment;

import android.view.View;

import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class NoteActivity extends Activity {
	
	public ListView maListViewPerso;
	public int positionToRemove;
	String Newligne=System.getProperty("line.separator"); 
	
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
				
				/* Checks if external storage is available to at least read */
				public boolean isExternalStorageReadable() {
				    String state = Environment.getExternalStorageState();
				    if (Environment.MEDIA_MOUNTED.equals(state) ||
				        Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
				        return true;
				    }
				    return false;
				}
				
				public String readTextFile(File file) throws IOException {
					BufferedReader reader = new BufferedReader(new FileReader(file));
					StringBuilder text = new StringBuilder();
					String line;
					while ((line = reader.readLine()) != null) {
						text.append(line);
						text.append("\n");
					}
					reader.close();
					return text.toString();
				}
	 
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_note);
 
        //Récupération de la listview créée dans le fichier main.xml
        maListViewPerso = (ListView) findViewById(R.id.listView1);
 
        //Création de la ArrayList qui nous permettra de remplire la listView
        final ArrayList<HashMap<String, String>> listItem = new ArrayList<HashMap<String, String>>();
 
        //On déclare la HashMap qui contiendra les informations pour un item
        HashMap<String, String> map;
 
        //Création d'une HashMap pour insérer les informations du premier item de notre listView
        map = new HashMap<String, String>();
 
        //Création d'un SimpleAdapter qui se chargera de mettre les items présent dans notre list (listItem) dans la vue affichageitem
        SimpleAdapter mSchedule = new SimpleAdapter (this.getBaseContext(), listItem, R.layout.fragment_note,
               new String[] {"auteur", "sujet", "date"}, new int[] {R.id.auteur, R.id.sujet, R.id.date});
        mSchedule.notifyDataSetChanged();
        //On attribut à notre listView l'adapter que l'on vient de créer
        maListViewPerso.setAdapter(mSchedule);
        
        String content = "";

        if (FileUtil.isMediaMounted()) {
			if (NOTE_DIR.exists()) {
				if (SAVE_FILEPATH.exists()) {
					try {
						content = FileUtil.readTextFile(SAVE_FILEPATH);
					} catch (IOException e) {
			        	Toast.makeText(NoteActivity.this, "NOT READING", Toast.LENGTH_SHORT).show();

					}
				} else {
					// Create File and restart function
					FileUtil.createFile(SAVE_FILEPATH);
					return;
				}
			} else {
				// Create Tree and restart function
				FileUtil.createTree(NOTE_DIR);
				return;
			}
		} else {
			Toast.makeText(NoteActivity.this, "NO SD", Toast.LENGTH_SHORT).show();
		}
		if (content.equals("")) {
			Toast.makeText(getApplicationContext(), "No save found",
					Toast.LENGTH_SHORT).show();
		} else {
			String lines[] = content.split("\n"); 
			// Clear 'listItem'
			listItem.clear();
			
			Toast.makeText(getApplicationContext(), "Save found",
					Toast.LENGTH_SHORT).show();
			// Update 'listItem' array with the new list content
			for (String l : lines) {
				String s[] = l.split(";");
				map.put("auteur",s[0]);
				map.put("sujet",s[1]);
				map.put("date",s[2]);
				map.put("detail",s[3]);
				listItem.add(map);
				map = new HashMap<String, String>();
			}
			mSchedule.notifyDataSetChanged();
		}
        
        //Enfin on met un écouteur d'évènement sur notre listView
        maListViewPerso.setOnItemClickListener(new OnItemClickListener() {
			@Override
        	@SuppressWarnings("unchecked")
         	public void onItemClick(AdapterView<?> a, View v, int position, long id) {
				//on récupère la HashMap contenant les infos de notre item (titre, description)
        		HashMap<String, String> map = (HashMap<String, String>) maListViewPerso.getItemAtPosition(position);
        		//on créer une boite de dialogue
        		AlertDialog.Builder adb = new AlertDialog.Builder(NoteActivity.this);
        		//on attribut un titre à notre boite de dialogue
        		adb.setTitle("Sélection Item");
        		//on insère un message à notre boite de dialogue, et ici on affiche le titre de l'item cliqué
        		adb.setMessage("Détails du message : " +Newligne+map.get("detail") +Newligne+Newligne);
        		//on indique que l'on veut le bouton ok à notre boite de dialogue
        		adb.setPositiveButton("OK", null);
        		
        		//on affiche la boite de dialogue
        		adb.show();
        	}
         });
        

        maListViewPerso.setOnItemLongClickListener(new OnItemLongClickListener() {
        	@Override
      		public boolean onItemLongClick(AdapterView<?> a, View v, int position, long id) {
        		//HashMap<String, String> map = (HashMap<String, String>) maListViewPerso.getItemAtPosition(position);
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
                    	try {
                            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(SAVE_FILEPATH)));
                 
                            StringBuffer sb = new StringBuffer(); 
                            String line;    
                            int nbLinesRead = 0;       
                            while ((line = reader.readLine()) != null) {
                                if (nbLinesRead != positionToRemove) {
                                    sb.append(line + "\n");
                                }
                                nbLinesRead++;
                            }
                            reader.close();
                            BufferedWriter out = new BufferedWriter(new FileWriter(SAVE_FILEPATH));
                            out.write(sb.toString());
                            out.close();
                 
                        } catch (Exception e) {
                        	Toast.makeText(getApplicationContext(),
            						"Deleting error " + e.getMessage(),
            						Toast.LENGTH_SHORT).show();
                        }
                    }});
                adb.show();
    		return true;
    		
        }
        });
        
    }
}
