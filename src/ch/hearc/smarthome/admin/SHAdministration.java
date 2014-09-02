package ch.hearc.smarthome.admin;

import java.util.ArrayList;
import java.util.Hashtable;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import ch.hearc.smarthome.R;
import ch.hearc.smarthome.bluetooth.SHBluetoothActivity;
import ch.hearc.smarthome.bluetooth.SHBluetoothNetworkManager;
import ch.hearc.smarthome.bluetooth.SHDevice;
import ch.hearc.smarthome.bluetooth.SHDeviceListBaseAdapter;
import ch.hearc.smarthome.bluetooth.SHDeviceSelectActivity;
import ch.hearc.smarthome.networktester.SHCommunicationProtocol;
import ch.hearc.smarthome.notes.NoteActivity;

public class SHAdministration extends SHBluetoothActivity
{

	private Hashtable<String , String>	users;

	private String						getUser		= "a get user";

	private SHCommunicationProtocol		protocol;

	// View components
	private ArrayList<SHUser>			mUserList	= new ArrayList<SHUser>( );
	private ListView					lv_UserList;
	private SHUserListBaseAdapter		mUserListBaseAdapter;
	private Button						b_getUsers;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.administration_screen);

		protocol = new SHCommunicationProtocol( );
		users = new Hashtable<String , String>( );

		/* Only for debugging */
		mUserList.add(new SHUser("Horia", "abcd"));
		mUserList.add(new SHUser("Thomas", "from"));
		mUserList.add(new SHUser("Sal", "1234"));
		
		lv_UserList = (ListView) findViewById(R.id.ad_lv_user_list);
		b_getUsers = (Button) findViewById(R.id.ad_b_get_users);
		
		
		mUserListBaseAdapter = new SHUserListBaseAdapter(this, mUserList);
		lv_UserList.setAdapter(mUserListBaseAdapter);
		lv_UserList.setOnItemLongClickListener(mUserClickListener);
	}

	public void getUsers(View _view)
	{
		String dataToSend = protocol.generateDataToSend(getUser, "all");
		// write(dataToSend);
		notifyUser("called");
	}

	@Override
	public boolean handleMessage(Message _msg)
	{
		if(_msg.what == SHBluetoothNetworkManager.MSG_READ)
		{
			String data = ((String) _msg.obj);
			// Check if code for user sending has been sent
			if(data.contains("users:"))
			{
				mUserList.clear( );
				
				// data contains the whole list of users and their passwords
				String[ ] lines = data.split("\n");
				for(String l : lines)
				{
					String[ ] comp = l.split(",");
					users.put(comp[0], comp[1]);
					mUserList.add(new SHUser(comp[0], comp[1]));
				}
				notifyUser("Data received.");
				mUserListBaseAdapter.notifyDataSetChanged( );
			}

		}
		return super.handleMessage(_msg);
	}

	
	/** The on-click listener for all devices in the ListViews */
    private OnItemLongClickListener mUserClickListener = new OnItemLongClickListener() {
        public boolean onItemLongClick(AdapterView<?> _parent, View _view, int _position, long _id) {
        	
        	/*
        	//HashMap<String, String> map = (HashMap<String, String>) myListView.getItemAtPosition(position);
    		//on créer une boite de dialogue
    		AlertDialog.Builder adb = new AlertDialog.Builder(SHAdministration.this);
    		//on attribut un titre à notre boite de dialogue
    		adb.setTitle("Manage user");
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
            adb.show();*/
		return true;
    	}}
    ;
	
	
	
}
