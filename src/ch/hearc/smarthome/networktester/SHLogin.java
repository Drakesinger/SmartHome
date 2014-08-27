package ch.hearc.smarthome.networktester;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import ch.hearc.smarthome.CredentialManager;
import ch.hearc.smarthome.HomeActivity;
import ch.hearc.smarthome.R;

public class SHLogin extends BluetoothActivity {

	/* View Components */
	private EditText et_userName;
	private EditText et_password;
	public final static String EXTRA_MESSAGE = "extra";

	public static short kfirstUse = 1;

	
	/* Debugging */
	private static final String TAG = "SHBluetoothTesting";	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_login);
	}

	/** Called by the login button */
	public void login(View view) {

		et_userName = (EditText) findViewById(R.id.editText1);
		et_password = (EditText) findViewById(R.id.editText2);

		String password = et_password.getText().toString();
		String username = et_userName.getText().toString();

		
		if (CredentialManager.bIsValid(username, password)) 
		{
			if (kfirstUse == 1) 
			{
				/*
				 * First use of our application. So we login with the details
				 * entered and send them to the other device.
				 */

				if (SHBluetoothNetworkManager.DEBUG)
				{
					Log.d(TAG, "Login. First use");
				}

				CredentialManager.setCredential(username, password);

				write(CredentialManager.getCredential(username));

				kfirstUse = 0;

				Intent intent = new Intent(this, HomeActivity.class);
				intent.putExtra(EXTRA_MESSAGE, username);
				startActivity(intent);
			} 
			else
			{
				/* Already logged in once, so check if data entered is correct */

				if (CredentialManager.bUserExists(username))
				{
					if (CredentialManager.bPasswordCorrect(username, password))
					{
						notifyUser("Login OK !");

						write(CredentialManager.getCredential(username));
						// TODO read response from PIC
						String response = read();

						// Response must be [user][login ok]
						if (response == username + "," + "login ok")
						{
							Intent intent = new Intent(this, HomeActivity.class);
							intent.putExtra(EXTRA_MESSAGE, username);
							startActivity(intent);
						} 
						else
						{
							notifyUser("Login fail");
						}

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
			notifyUser("Invalid user/password length. Max: "+ CredentialManager.kPasswordMaxLength + " chars.");
		}

	}

	private void notifyUser(String _string) {
		Toast.makeText(SHLogin.this,_string, Toast.LENGTH_LONG).show();
		
	}
}