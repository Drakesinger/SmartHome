package ch.hearc.smarthome;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class NoteActivity extends Activity {
	
	private ListView maListViewPerso;
	String Newligne=System.getProperty("line.separator"); 
	 
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_note);
 
        //R�cup�ration de la listview cr��e dans le fichier main.xml
        maListViewPerso = (ListView) findViewById(R.id.listView1);
 
        //Cr�ation de la ArrayList qui nous permettra de remplire la listView
        ArrayList<HashMap<String, String>> listItem = new ArrayList<HashMap<String, String>>();
 
        //On d�clare la HashMap qui contiendra les informations pour un item
        HashMap<String, String> map;
 
        //Cr�ation d'une HashMap pour ins�rer les informations du premier item de notre listView
        map = new HashMap<String, String>();
        //on ins�re un �l�ment titre que l'on r�cup�rera dans le textView titre cr�� dans le fichier affichageitem.xml
        map.put("auteur", "Pierre");
        //on ins�re un �l�ment description que l'on r�cup�rera dans le textView description cr�� dans le fichier affichageitem.xml
        map.put("sujet", "Heure de rentrer");
        map.put("detail", "Nous sommes sortis, ce soir tu rentres pour 23h, pas plus tard");
        map.put("date", "02.05.2014");
        //enfin on ajoute cette hashMap dans la arrayList
        listItem.add(map);
 
        //On refait la manip plusieurs fois avec des donn�es diff�rentes pour former les items de notre ListView
 
        map = new HashMap<String, String>();
        map.put("auteur", "Martine");
        map.put("sujet", "Faire les courses");
        map.put("detail", "Salut, il serait sympa d'aller faire les courses � la Coop. Merci.");
        map.put("date", "13.04.2014");
        listItem.add(map);
 
        map = new HashMap<String, String>();
        map.put("auteur", "Lucie");
        map.put("sujet", "Je serai en retard pour le souper");
        map.put("detail", "Commence de souper sans moi je rentre vers 21h.");
        map.put("date", "10.03.2014");
        listItem.add(map);
 
        map = new HashMap<String, String>();
        map.put("auteur", "Pierre");
        map.put("sujet", "Souper sans moi !");
        map.put("detail", "Souper sans moi j'ai beaucoup de travail je rentre tard.");
        map.put("date", "15.02.2014");
        listItem.add(map);
 
        //Cr�ation d'un SimpleAdapter qui se chargera de mettre les items pr�sent dans notre list (listItem) dans la vue affichageitem
        SimpleAdapter mSchedule = new SimpleAdapter (this.getBaseContext(), listItem, R.layout.fragment_note,
               new String[] {"auteur", "sujet", "date"}, new int[] {R.id.auteur, R.id.sujet, R.id.date});
        mSchedule.notifyDataSetChanged();
        //On attribut � notre listView l'adapter que l'on vient de cr�er
        maListViewPerso.setAdapter(mSchedule);
 
        //Enfin on met un �couteur d'�v�nement sur notre listView
        maListViewPerso.setOnItemClickListener(new OnItemClickListener() {
			@Override
        	@SuppressWarnings("unchecked")
         	public void onItemClick(AdapterView<?> a, View v, int position, long id) {
				//on r�cup�re la HashMap contenant les infos de notre item (titre, description)
        		HashMap<String, String> map = (HashMap<String, String>) maListViewPerso.getItemAtPosition(position);
        		//on cr�er une boite de dialogue
        		AlertDialog.Builder adb = new AlertDialog.Builder(NoteActivity.this);
        		//on attribut un titre � notre boite de dialogue
        		adb.setTitle("S�lection Item");
        		//on ins�re un message � notre boite de dialogue, et ici on affiche le titre de l'item cliqu�
        		adb.setMessage("D�tails du message : " +Newligne+map.get("detail") +Newligne+Newligne);
        		//on indique que l'on veut le bouton ok � notre boite de dialogue
        		adb.setPositiveButton("OK", null);
        		
        		//on affiche la boite de dialogue
        		adb.show();
        	}
         });
        

        maListViewPerso.setOnItemLongClickListener(new OnItemLongClickListener() {
        	@Override
      		public boolean onItemLongClick(AdapterView<?> a, View v, int position, long id) {
        		//HashMap<String, String> map = (HashMap<String, String>) maListViewPerso.getItemAtPosition(position);
        		//on cr�er une boite de dialogue
        		AlertDialog.Builder adb = new AlertDialog.Builder(NoteActivity.this);
        		//on attribut un titre � notre boite de dialogue
        		adb.setTitle("Suppression");
        		//on ins�re un message � notre boite de dialogue, et ici on affiche le titre de l'item cliqu�
        		adb.setMessage("Voulez-vous le supprimer ?");
        		//final int positionToRemove = position;
        		//on indique que l'on veut le bouton ok � notre boite de dialogue
        		adb.setPositiveButton("Supprimer", new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //MyDataObject.remove(positionToRemove);
                    	//SimpleAdapter mSchedule = new SimpleAdapter (this, android.R.layout.simple_list_item_1);
                         //mSchedule.notifyDataSetChanged();
                    }});
                adb.show();
        		//maListViewPerso.removeView(a);
    		return true;
    		
        }
        });
        
    }
}
