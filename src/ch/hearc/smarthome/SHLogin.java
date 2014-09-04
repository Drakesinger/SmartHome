package ch.hearc.smarthome;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import ch.hearc.smarthome.activitylist.SHActivityList;
import ch.hearc.smarthome.bluetooth.SHBluetoothActivity;
import ch.hearc.smarthome.bluetooth.SHBluetoothNetworkManager;
import ch.hearc.smarthome.bluetooth.SHCommunicationProtocol;

/**
 * Activity called directly after connecting with a device.
 * Asks the user to login. <br>
 * Checks if the local Android logs contain the username entered by the user, if
 * so, the data sent to the module counterpart is different.
 * </br>
 * 
 * @author Horia Mut
 */
public class SHLogin extends SHBluetoothActivity
{

	// View Components
	private EditText						et_userName;
	private EditText						et_password;
	private ProgressDialog					mConnectionProgressDialog;

	// Functions of SHLogin
	private static final String				login		= "login";
	private static final String				createUser	= "create user";

	// Debugging
	private static final String				TAG			= "SHLogin";

	private static String					response	= null;
	public static SHCommunicationProtocol	Protocol;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_screen);
		// Construct the Communication Protocol containing all functions used
		Protocol = new SHCommunicationProtocol( );
		// Update our credentials if there are any written on disk
		SHCredentialManager.update( );
		// First we need to ask the PIC if someone did log in before.

		write("\r"); 	// PIC requires an empty 1st message in order to function
						// correctly

	}

	@Override
	protected void onResume( )
	{
		preventCancel = false;
		super.onResume( );
	}

	/**
	 * Call when the back button is click, to shut down the application and the
	 * toasts
	 */
	@Override
	public void onBackPressed( )
	{
		disconnect( );
		super.onBackPressed( );
	}

	/** Called by the login button */
	public void login(View _view)
	{

		et_userName = (EditText) findViewById(R.id.login_screen_et_username);
		et_password = (EditText) findViewById(R.id.login_screen_et_password);

		String password = et_password.getText( ).toString( );
		String username = et_userName.getText( ).toString( );

		String dataToSend;

		if(SHCredentialManager.bIsValid(username, password))
		{

			if(DEBUG_ONLY)
			{
				notifyUser("Login OK");
				// Now we need to save the Credentials
				SHCredentialManager.saveCredential(SHCredentialManager.getActualUser( ));

				// And start up the Home Activity screen
				Intent intent = new Intent(this, SHActivityList.class);
				preventCancel = true;
				startActivity(intent);
				return;
			}

			SHCredentialManager.setCredential(username, password);
			dataToSend = Protocol.generateDataToSend(username, login, password);

			if(dataToSend.contains("Davy Jones' Locker!"))
			{
				notifyUser(dataToSend);
			}
			else
			{
				sendData(dataToSend);
			}

		}
		else
		{
			notifyUser("Invalid user/password length. Max: " + SHCredentialManager.kPasswordMaxLength + " chars.");
		}
	}

	/**
	 * Send data.
	 * 
	 * @param _data
	 *            [user],[password],[login function ID]
	 * @param _bResponseReceived
	 *            True if a response was received, it this case the function
	 *            just checks the answer
	 */
	private void sendData(String _data)
	{
		if(_data != null)
		{
			write(_data);
			// mConnectionProgressDialog = ProgressDialog.show(SHLogin.this, "",
			// "Sending login request...", false, true);
		}
	}

	@Override
	public boolean handleMessage(Message _msg)
	{
		if(_msg.what == SHBluetoothNetworkManager.MSG_READ)
		{

			response = ((String) _msg.obj).toLowerCase( );
			notifyUser("Received:\n" + response);

			if(response.contains("login ok"))
			{
				notifyUser("Login OK");
				// Now we need to save the Credentials
				SHCredentialManager.saveCredential(SHCredentialManager.getActualUser( ));

				// And start up the Home Activity screen
				Intent intent = new Intent(this, SHActivityList.class);
				preventCancel = true;
				startActivity(intent);
			}
			else if(response.contains("user not found"))
			{
				notifyUser("User not found.\nCreating user.");
				String parameters = Protocol.generate(SHCredentialManager.getActualPass( ));
				String dataToSend = Protocol.generateDataToSend(createUser, parameters);
				write(dataToSend);
			}
			else if(response.contains("user created"))
			{
				notifyUser("User was created sucessfully.");
			}
		}
		return super.handleMessage(_msg);
	}

}
