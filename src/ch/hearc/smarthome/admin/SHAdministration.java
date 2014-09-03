package ch.hearc.smarthome.admin;

import java.util.ArrayList;
import java.util.Hashtable;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ListView;
import ch.hearc.smarthome.R;
import ch.hearc.smarthome.bluetooth.SHBluetoothActivity;
import ch.hearc.smarthome.bluetooth.SHBluetoothNetworkManager;
import ch.hearc.smarthome.networktester.SHCommunicationProtocol;

public class SHAdministration extends SHBluetoothActivity
{

	private Hashtable<String , String>	users;

	// Functions
	private String						getUser			= "a get user";

	private String						removeUser		= "a del user";
	private String						addUser			= "a add user";
	private String						changeUserPass	= "a change pass";
	private String						changeUserName	= "a change username";

	private SHCommunicationProtocol		protocol;

	// View components
	private Button						b_getUsers;
	private ListView					lv_UserList;
	// List components
	private ArrayList<SHUser>			mUserList		= new ArrayList<SHUser>( );
	private SHUserListBaseAdapter		mUserListBaseAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.administration_screen);

		protocol = new SHCommunicationProtocol( );
		users = new Hashtable<String , String>( );

		lv_UserList = (ListView) findViewById(R.id.ad_lv_user_list);
		lv_UserList.setVisibility(View.GONE);

		b_getUsers = (Button) findViewById(R.id.ad_b_get_users);
		b_getUsers.setVisibility(View.VISIBLE);
		b_getUsers.setClickable(true);

		mUserListBaseAdapter = new SHUserListBaseAdapter(this, mUserList);
		lv_UserList.setAdapter(mUserListBaseAdapter);
		lv_UserList.setOnItemLongClickListener(mUserClickListener);
	}

	public void getUsers(View _view)
	{
		String dataToSend = protocol.generateDataToSend(getUser, "all");
		// write(dataToSend);
		notifyUser("Retrieving user list.");
		b_getUsers.setVisibility(View.GONE);
		b_getUsers.setClickable(false);
		// for debugging purpouses
		getStuff( );
	}

	/** Only for debugging */
	private void getStuff( )
	{
		lv_UserList.setVisibility(View.VISIBLE);
		// mUserList.clear( );
		mUserList.add(new SHUser("Horia", "abcd"));
		mUserList.add(new SHUser("Thomas", "from"));
		mUserList.add(new SHUser("Sal", "1234"));
		mUserListBaseAdapter.notifyDataSetChanged( );

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

	//@formatter:off
	/** The on-click listener for all devices in the ListViews */
    private OnItemLongClickListener mUserClickListener = new OnItemLongClickListener() {

		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id)
		{
			/// Instantiate the AlertDialog.Builder
        	final AlertDialog.Builder adb = new AlertDialog.Builder(SHAdministration.this);
    		
    		adb.setTitle("Manage user");
    		adb.setMessage("What do you want to do?");
    		final int userChosen = position;
    		
    		adb.setPositiveButton("Manage", new AlertDialog.OnClickListener() {
    	           public void onClick(DialogInterface dialog, int id) {
    	               // should open a new dialog with the username and password
    	        	   
    	        	   DialogFragment newFragment = new ManageUser();
    	        	    newFragment.show(getFragmentManager(), "missiles");
    	           }
    	       });
    		adb.setNeutralButton("Cancel", new AlertDialog.OnClickListener( )
				{
					public void onClick(DialogInterface dialog, int which)
					{
						// Do nothing
					}
				});
    		adb.setNegativeButton("Delete", new AlertDialog.OnClickListener( )
				{
					public void onClick(DialogInterface dialog, int which)
					{
						SHUser userToRemove = mUserList.get(userChosen);
						mUserList.remove(userChosen);
						
						String dataToSend = protocol.generateDataToSend(removeUser, userToRemove.getUserName( ));
						write(dataToSend);;
					}
				});
            adb.show();
			return false;
		}
    	
    	
    };
    /* WORKS */
    public static class ManageUser extends DialogFragment {
    	@Override
    	public Dialog onCreateDialog(Bundle savedInstanceState) {
    	    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    	    // Get the layout inflater
    	    LayoutInflater inflater = getActivity().getLayoutInflater();

    	    // Inflate and set the layout for the dialog
    	    // Pass null as the parent view because its going in the dialog layout
    	    builder.setView(inflater.inflate(R.layout.administration_modify_dialog, null))
    	    // Add action buttons
    	           .setPositiveButton("Modify", new DialogInterface.OnClickListener() {
    	               @Override
    	               public void onClick(DialogInterface dialog, int id) {
    	                   // sign in the user ...
    	               }
    	           })
    	           .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
    	               public void onClick(DialogInterface dialog, int id) {
    	            	   ManageUser.this.getDialog().cancel();
    	               }
    	           });      
    	    return builder.create();
    	}
    }
    
	//@formatter:on

}
