package ch.hearc.smarthome.admin;

import java.util.ArrayList;
import java.util.Hashtable;

import android.app.DialogFragment;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ListView;
import ch.hearc.smarthome.R;
import ch.hearc.smarthome.bluetooth.SHBluetoothActivity;
import ch.hearc.smarthome.bluetooth.SHBluetoothNetworkManager;
import ch.hearc.smarthome.bluetooth.SHCommunicationProtocol;

public class SHAdministration extends SHBluetoothActivity implements SHManageUserFragment.NoticeDialogListener, SHAddUserFragment.NoticeDialogListener, OnItemLongClickListener
{

	// Debugging
	private static final String			TAG				= "SHAdministration";
	

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
	private Button						b_addUser;
	private ListView					lv_UserList;

	// List components
	private ArrayList<SHUser>			mUserList		= new ArrayList<SHUser>( );
	private SHUserListBaseAdapter		mUserListBaseAdapter;

	// Data that is sent from SHManageUserFragment and onItemLongClickListener
	private int							mUserChosen;
	private String						newUsername;
	private String						newPassword;

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

		b_addUser = (Button) findViewById(R.id.ad_b_add_user);
		b_addUser.setVisibility(View.GONE);
		b_addUser.setClickable(false);

		mUserListBaseAdapter = new SHUserListBaseAdapter(this, mUserList);
		lv_UserList.setAdapter(mUserListBaseAdapter);
		lv_UserList.setOnItemLongClickListener(this);
	}

	public void getUsers(View _view)
	{
		String dataToSend = protocol.generateDataToSend(getUser, "all");
		if(write(dataToSend))
		{
			notifyUser("Retrieving user list.");
			b_getUsers.setVisibility(View.GONE);
			b_getUsers.setClickable(false);
		}
		else
		{
			notifyUser("Could not retrieve user list.\nConnection may be down.");
		}

		if(DEBUG_ONLY) getStuff( ); 		// for debugging purposes
	}

	public void addUser(View _view)
	{
		// Launch a new dialog to insert username and password
		DialogFragment addUserFragment = new SHAddUserFragment( );
		addUserFragment.show(getFragmentManager( ), "AddUser");
	}

	/** Only for debugging */
	private void getStuff( )
	{
		lv_UserList.setVisibility(View.VISIBLE);
		mUserList.clear( );

		if(DEBUG_ONLY)
		{

			String data = "users:\nHoriaMut,mdk11\nThomas R,leRoux\nTom ik,123 Jack\n";
			// Check if code for user sending has been sent
			if(data.contains("users:"))
			{
				mUserList.clear( );
				lv_UserList.setVisibility(View.VISIBLE);

				// data contains the whole list of users and their passwords
				String[ ] lines = data.split("\n");
				for(int i = 1; i < lines.length; i++)
				{
					String[ ] comp = lines[i].split(",");
					users.put(comp[0], comp[1]);
					mUserList.add(new SHUser(comp[0], comp[1]));
				}
				notifyUser("Data received.");
				mUserListBaseAdapter.notifyDataSetChanged( );

				b_addUser.setVisibility(View.VISIBLE);
				b_addUser.setClickable(true);
			}

		}

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
				lv_UserList.setVisibility(View.VISIBLE);

				// data contains the whole list of users and their passwords
				String[ ] lines = data.split("\n");
				for(int i = 1; i < lines.length; i++)
				{

					String[ ] comp = lines[i].split(",");
					users.put(comp[0], comp[1]);
					mUserList.add(new SHUser(comp[0], comp[1]));
				}
				notifyUser("Data received.");
				mUserListBaseAdapter.notifyDataSetChanged( );

				b_addUser.setVisibility(View.VISIBLE);
				b_addUser.setClickable(true);
			}

			if(data.contains("username change ok"))
			{
				notifyUser("Username was changed sucessfully.");
			}
			else if(data.contains("password change ok"))
			{
				notifyUser("Password was changed sucessfully.");

				if(!DEBUG_ONLY)
				{
					// update
					users.remove(newUsername);
					mUserList.set(mUserChosen, (SHUser) new SHUser(newUsername, newPassword));
					mUserListBaseAdapter.notifyDataSetChanged( );
				}
			}

			if(data.contains("add user ok"))
			{
				notifyUser("User was created sucessfuly.");

				if(!DEBUG_ONLY)
				{
					// update
					users.put(newUsername, newPassword);
					mUserList.add((SHUser) new SHUser(newUsername, newPassword));
					mUserListBaseAdapter.notifyDataSetChanged( );
				}
			}

		}
		return super.handleMessage(_msg);
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id)
	{
		mUserChosen = position;

		SHUser actualUserChosen = mUserList.get(mUserChosen);
		String actualUserName = actualUserChosen.getUserName( );
		String actualUserPass = actualUserChosen.getPassword( );

		// Instantiate a new dialogFragment containing the manage user screen.
		DialogFragment manageUserFragment = new SHManageUserFragment( );
		((SHManageUserFragment) manageUserFragment).showPreviousData(actualUserName, actualUserPass);
		manageUserFragment.show(getFragmentManager( ), "ManageUser");

		return false;
	}

	@Override
	public void onManageUserDialogModifyClick(DialogFragment _dialogFragment)
	{
		// TODO Auto-generated method stub

		// Retrive the changed data
		newUsername = ((SHManageUserFragment) _dialogFragment).getUserName( );
		newPassword = ((SHManageUserFragment) _dialogFragment).getUserPass( );

		if(DEBUG_ONLY)
		{
			users.remove(newUsername);
			mUserList.set(mUserChosen, (SHUser) new SHUser(newUsername, newPassword));
			mUserListBaseAdapter.notifyDataSetChanged( );
		}

		String dataToSend;

		// now send the new information about the user
		dataToSend = protocol.generateDataToSend(changeUserName, newUsername);
		write(dataToSend);

		String params = newUsername + "," + newPassword;
		dataToSend = protocol.generateDataToSend(changeUserPass, params);
		write(dataToSend);
	}

	@Override
	public void onManageUserDialogRemoveClick(DialogFragment _dialogFragment)
	{
		// TODO Auto-generated method stub
		notifyUser("Ok clicked.");
		SHUser userToRemove = mUserList.get(mUserChosen);

		if(DEBUG_ONLY)
		{
			mUserList.remove(mUserChosen);
			mUserListBaseAdapter.notifyDataSetChanged( );
		}
		String dataToSend = protocol.generateDataToSend(removeUser, userToRemove.getUserName( ));
		write(dataToSend);
	}

	@Override
	public void onAddUserDialogOkClick(DialogFragment _dialogFragment)
	{
		// TODO Auto-generated method stub
		// Retrive the changed data
		newUsername = ((SHAddUserFragment) _dialogFragment).getUserName( );
		newPassword = ((SHAddUserFragment) _dialogFragment).getUserPass( );

		if(DEBUG_ONLY)
		{
			users.put(newUsername, newPassword);
			mUserList.add(new SHUser(newUsername, newPassword));
			mUserListBaseAdapter.notifyDataSetChanged( );
		}

		String dataToSend;

		// now send the new information about the new user
		String params = newUsername + "," + newPassword;
		dataToSend = protocol.generateDataToSend(addUser, newUsername);
		write(dataToSend);
	}

}
