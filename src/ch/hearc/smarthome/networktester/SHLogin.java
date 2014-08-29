package ch.hearc.smarthome.networktester;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import ch.hearc.smarthome.CredentialManager;
import ch.hearc.smarthome.HomeActivity;
import ch.hearc.smarthome.R;

/**
 * Activity called directly after connecting with a device.
 * Asks the user to login.
 * Checks if the local Android logs contain the username entered by the user, if
 * so, the data sent to the module counterpart is different.
 * 
 * @author Horia Mut
 */
public class SHLogin extends SHBluetoothActivity
{

	// View Components
	private EditText			et_userName;
	private EditText			et_password;
	public final static String	EXTRA_MESSAGE	= "extra";

	public static short			kfirstUse		= 1;

	// Functions of SHLogin
	private static final String	login			= "login";

	// Debugging
	private static final String	TAG				= "SHLogin";

	private static String		response		= null;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_login);
	}
	
	@Override
	protected void onResume( )
	{
		notifyUser("onResume " + TAG);
		preventCancel = false;
		super.onResume( );
	}
	
	/** Call when the back button is click, to shut down the application and the toast */
	public void onBackPressed()
	{
		System.exit(0);
	}
	
	/** Called by the login button */
	public void login(View _view)
	{

		et_userName = (EditText) findViewById(R.id.editText1);
		et_password = (EditText) findViewById(R.id.editText2);

		String password = et_password.getText( ).toString( );
		String username = et_userName.getText( ).toString( );

		write("\r");

		if(CredentialManager.bIsValid(username, password))
		{
			if(kfirstUse == 1)
			{
				// First use of our application. So we login with the details
				// entered and send them to the other device.

				if(SHBluetoothNetworkManager.DEBUG) Log.d(TAG, "Login. First use");

				CredentialManager.setCredential(username, password);
				CredentialManager.setActualUser(username);
				sendDataAndCheckResponse(CredentialManager.getCredential(username, false));
			}
			else
			{
				// Already logged in once, so check if data entered is correct

				if(CredentialManager.bUserExists(username))
				{
					if(CredentialManager.bPasswordCorrect(username, password))
					{
						CredentialManager.setActualUser(username);
						sendDataAndCheckResponse(CredentialManager.getCredential(username, true));
					}
					else
					{
						notifyUser("Wrong password.");
					}
				}
				else
				{
					notifyUser("Username does not exist.");
				}
			}
		}
		else
		{
			notifyUser("Invalid user/password length. Max: "
						+ CredentialManager.kPasswordMaxLength + " chars.");
		}

	}

	private void sendDataAndCheckResponse(String _credential)
	{

		SHCommunicationProtocol Protocol = new SHCommunicationProtocol( );

		String DataToSend = _credential + "," + Protocol.getFunctionID(login);
		write(DataToSend);

		if(SHBluetoothNetworkManager.DEBUG)
		{
			Log.d(TAG, "Sent data:\n" + DataToSend);
			Log.d(TAG, "Response variable is in SendData:" + response);
		}

		if(response == null)
		{
			notifyUser("No response received. Please try again.");
			return;
		}

		if(SHBluetoothNetworkManager.DEBUG) Log.d(TAG, "Response contains: "
														+ response);
		// Response must be [user][login ok]
		if(response.contains("login ok"))
		{
			notifyUser("Login OK !");
			Intent intent = new Intent(this, HomeActivity.class);
			preventCancel = true;
			startActivity(intent);
		}
		else
		{
			notifyUser("Login fail");
		}
	}

	private void notifyUser(String _string)
	{
		Toast.makeText(getApplicationContext( ), _string, Toast.LENGTH_LONG).show( );
	}

	@Override
	public boolean handleMessage(Message _msg)
	{
		if(_msg.what == SHBluetoothNetworkManager.MSG_READ)
		{
			if(SHBluetoothNetworkManager.DEBUG) Log.d(TAG, "Response found, changing response string");
			response = ((String) _msg.obj).toLowerCase( );
			notifyUser("Received:" + response);
		}
		return super.handleMessage(_msg);
	}

}
