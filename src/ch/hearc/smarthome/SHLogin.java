package ch.hearc.smarthome;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import ch.hearc.smarthome.bluetooth.SHBluetoothActivity;
import ch.hearc.smarthome.bluetooth.SHBluetoothNetworkManager;
import ch.hearc.smarthome.networktester.SHCommunicationProtocol;

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

	public static boolean					bAlreadyLoggedIn	= false;

	// Functions of SHLogin
	private static final String				login				= "login";

	// Debugging
	private static final String				TAG					= "SHLogin";

	private static String					response			= null;
	public static SHCommunicationProtocol	Protocol;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_login);
		// Construct the Communication Protocol containing all functions used
		Protocol = new SHCommunicationProtocol( );
		// Update our credentials if there are any written on disk
		CredentialManager.update( );
		// First we need to ask the PIC if someone did log in before.
		sendRequestSomeoneAlreadyLoggedIn( );
	}

	@Override
	protected void onResume( )
	{
		notifyUser("onResume " + TAG);
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
		// System.exit(0); //TODO check which is better
		super.onBackPressed( );
	}

	/** Called by the login button */
	public void login(View _view)
	{

		et_userName = (EditText) findViewById(R.id.editText1);
		et_password = (EditText) findViewById(R.id.editText2);

		String password = et_password.getText( ).toString( );
		String username = et_userName.getText( ).toString( );

		write("\r"); 	// PIC requires an empty 1st message in order to function
						// correctly

		String dataToSend;

		if(CredentialManager.bIsValid(username, password))
		{

			if(!bAlreadyLoggedIn)
			{
				if(SHBluetoothNetworkManager.DEBUG) Log.d(TAG, "Login. First use");
				// First use of our application. So we login with the details
				// entered and send them to the other device.
				CredentialManager.setCredential(username, password);
				dataToSend = CredentialManager.getCredential(username, false);

				if(dataToSend.contains("Davy Jones' Locker!"))
				{
					notifyUser(dataToSend);
				}
				else
				{
					sendDataAndCheckResponse(dataToSend, false);
				}
			}
			else
			{
				// TODO Take out redundant code
				// Already logged in once, so check if data entered is correct
				if(CredentialManager.bUserExists(username)) // WARNING REDUNDANT
				{
					if(CredentialManager.bPasswordCorrect(username, password))// WARNING
																				// REDUNDANT
					{
						CredentialManager.setActualUser(username);
						CredentialManager.setActualPass(password);
						dataToSend = CredentialManager.getCredential(username, true);

						if(dataToSend.contains("Davy Jones' Locker!"))
						{
							notifyUser(dataToSend);
						}
						else
						{
							sendDataAndCheckResponse(dataToSend, false);
						}
					}
					else
					// WARNING REDUNDANT
					{
						notifyUser("Wrong password.");// WARNING REDUNDANT
					}
				}
				else
				// WARNING REDUNDANT
				{
					notifyUser("Username does not exist.");// WARNING REDUNDANT
				}
			}
		}
		else
		{
			notifyUser("Invalid user/password length. Max: "
						+ CredentialManager.kPasswordMaxLength + " chars.");
		}

	}

	/** Asks the PIC if it already has a user that has logged in to the device. */
	private void sendRequestSomeoneAlreadyLoggedIn( )
	{
		write("" + Protocol.getFunctionID("alreadyLoggedIn"));
	}

	private void sendDataAndCheckResponse(String _credential, boolean _bResponseReceived)
	{

		if(_bResponseReceived == false && _credential != null)
		{

			String DataToSend = _credential + ","
								+ Protocol.getFunctionID(login);
			write(DataToSend);

			if(SHBluetoothNetworkManager.DEBUG)
			{
				Log.d(TAG, "Sent data:\n" + DataToSend);
				Log.d(TAG, "Response variable is in SendData:" + response);
			}
		}
		else
		{
			// We don't send again, just verify the answer

			// Just in case the response is not available, should never happen
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
				bAlreadyLoggedIn = true; // We are the first user on the PIC
											// that has logged in

				// Now we need to save the Credentials
				CredentialManager.saveCredential(CredentialManager.getActualUser( ));

				// And start up the Home Activity screen
				Intent intent = new Intent(this, SHHomeActivity.class);
				preventCancel = true;
				startActivity(intent);
			}
			else
			{
				notifyUser("Login failed. Wrong usename or password.");
			}
		}
	}

	/* @formatter:off
	public void notifyUser(String _string)
	{
		Toast.makeText(getApplicationContext( ), _string, Toast.LENGTH_LONG).show( );
	}
	 */ //@formatter:on

	@Override
	public boolean handleMessage(Message _msg)
	{
		if(_msg.what == SHBluetoothNetworkManager.MSG_READ)
		{
			if(SHBluetoothNetworkManager.DEBUG) Log.d(TAG, "Response received, changing response string");
			response = ((String) _msg.obj).toLowerCase( );
			if(SHBluetoothNetworkManager.DEBUG) notifyUser("Received:" + response);

			// If the PIC already has a user logged in, then switch our global
			// variable accordingly
			if(response.contains("1\n"))
			{
				bAlreadyLoggedIn = true;
			}

			// quick attempt to receive and switch activity without re-clicking
			// on the login button
			sendDataAndCheckResponse(null, false);

		}
		return super.handleMessage(_msg);
	}

}
