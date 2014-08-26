package ch.hearc.smarthome.networktester;

import android.content.Intent;
import android.os.Bundle;
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

	public static short kfirstUse = 0;

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

		if (kfirstUse == 1) {
			/*
			 * First use of our application. So we login with the details
			 * entered and send them to the other device.
			 */

			CredentialManager.setCredential(username, password);
			write(CredentialManager.getCredential(username));

			kfirstUse = 0;

			Intent intent = new Intent(this, HomeActivity.class);
			intent.putExtra(EXTRA_MESSAGE, username);
			startActivity(intent);

		} else {
			/* Already logged in once, so check if data entered is correct */
			if (CredentialManager.bUserExists(username)) {
				if(CredentialManager.bPasswordCorrect(username, password)){
					Toast.makeText(SHLogin.this, "Login OK !",Toast.LENGTH_LONG).show();
					
					// TODO find a way to get all users and password from PIC?
					write(CredentialManager.getCredential(username));
					
					Intent intent = new Intent(this, HomeActivity.class);
					intent.putExtra(EXTRA_MESSAGE, username);
					startActivity(intent);
				}else {
					Toast.makeText(SHLogin.this,"Mauvaise combinaison login/mdp !", Toast.LENGTH_LONG).show();
				}
			} else {
				Toast.makeText(SHLogin.this,"Username does not exist", Toast.LENGTH_LONG).show();
			}
		}

	}
}