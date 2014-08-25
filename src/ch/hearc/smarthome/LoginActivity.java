package ch.hearc.smarthome;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity {
	/*blsbla*/
	private EditText et_userName;
	private EditText et_password;
	public final static String EXTRA_MESSAGE = "extra";

	public static short kfirstUse = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_login);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.option_menu, menu);
		return true;
	}

	public void Enregistrement(View view) {
		// Intent intentEnregistrement = new Intent(this, LoginActivity.class);
	}

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

			try {
				//NetworkManager.connect();
			} catch (Exception e) {
				// TODO: handle exception
			}
			/*
			NetworkManager.sendUsername(username);
			NetworkManager.sendPassword(password);
			 */
			kfirstUse = 0;
			
			Intent intent = new Intent(this, HomeActivity.class);
			intent.putExtra(EXTRA_MESSAGE, username);
			startActivity(intent);

		} else {
			/* Already logged in once, so check if data entered is correct */
			
			// TODO CHANGE THIS to something better
			
			// Connection has been established
			// Start
			if (password.equals(CredentialManager.getActualPass())
					&& username.equals(CredentialManager.getActualUser())) {

				Toast.makeText(LoginActivity.this, "Login OK !",
						Toast.LENGTH_LONG).show();
				
				Intent intent = new Intent(this, HomeActivity.class);
				intent.putExtra(EXTRA_MESSAGE, username);
				startActivity(intent);

			} else {
				Toast.makeText(LoginActivity.this,
						"Mauvaise combinaison login/mdp !", Toast.LENGTH_LONG)
						.show();
			}
		}

	}
}
