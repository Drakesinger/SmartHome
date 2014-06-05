package ch.hearc.smarthome.networktester;

import java.util.Set;

import ch.hearc.smarthome.CredentialManager;
import ch.hearc.smarthome.HomeActivity;
import ch.hearc.smarthome.PopupMessages;
import ch.hearc.smarthome.R;
import ch.hearc.smarthome.network.NetworkManager;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class SHBluetoothTesting extends Activity {

	/* View Components */
	private EditText et_userName;
	private EditText et_password;

	public final static String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";

	/* Global variables */
	public static short kfirstUse = 1;

	/* Intent request codes */
	private static final int REQUEST_CONNECT_DEVICE = 1;
	private static final int REQUEST_ENABLE_BT = 2;

	/* Message types sent from the BluetoothChatService Handler */
	public static final int MESSAGE_STATE_CHANGE = 1;
	public static final int MESSAGE_READ = 2;
	public static final int MESSAGE_WRITE = 3;
	public static final int MESSAGE_DEVICE_NAME = 4;
	public static final int MESSAGE_TOAST = 5;

	/* Key names received from the BluetoothCommandService Handler */
	public static final String DEVICE_NAME = "device_name";
	public static final String TOAST = "toast";

	/* Name of the connected device */
	private String mConnectedDeviceName = null;

	/* Local Bluetooth adapter */
	private BluetoothAdapter mBluetoothAdapter = null;

	/* Member object for Bluetooth Command Service */
	private BluetoothChatService mCommandService = null;

	/* Our local array of Bluetooth devices */
	Set<BluetoothDevice> mBtDevices;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_login);

		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mBluetoothAdapter == null) {
			// Device does not support Bluetooth
			PopupMessages.launchPopup("Bluetooth error.",
					"Device is not Bluetooth compatible.",
					getApplicationContext());
			finish();
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	@Override
	protected void onStart() {
		super.onStart();

		if (!mBluetoothAdapter.isEnabled()) {
			/* Request a Bluetooth enable */
			Intent enableBtIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
		} else {
			if (mCommandService == null) {
				// setupCommand();
			}
		}
		// getPairedDevices();
	}

	// TODO Used if we create a list adapter
	private void getPairedDevices() {
		mBtDevices = mBluetoothAdapter.getBondedDevices();
		if (mBtDevices.size() > 0) {
			Toast.makeText(getApplicationContext(), "Bluetooth devices found.",
					Toast.LENGTH_SHORT).show();
			for (BluetoothDevice device : mBtDevices) {
				// listAdapter.add(device.getName()+"\n"+device.getAddress());
			}
		} else {
			Toast.makeText(getApplicationContext(),
					"No Bluetooth devices found.", Toast.LENGTH_SHORT).show();
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		/*
		 * When Bluetooth enabling is asked for we check if user has canceled
		 * the request or not, if he has, display error then quit.
		 */
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_CANCELED) {
			PopupMessages
					.launchPopup(
							"Bluetooth not enabled.",
							"The application needs bluetooth enabled in order to continue",
							getApplicationContext());
			finish();
		}
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
				// NetworkManager.connect();
			} catch (Exception e) {
				// TODO: handle exception
			}

			NetworkManager.sendUsername(username);
			NetworkManager.sendPassword(password);

			kfirstUse = 0;

		} else {
			/* Already logged in once, so check if data entered is correct */

			// TODO CHANGE THIS to something better

			// Connection has been established
			// Start
			if (password.equals(CredentialManager.getActualPass())
					&& username.equals(CredentialManager.getActualUser())) {

				Toast.makeText(getApplicationContext(), "Login OK !",
						Toast.LENGTH_LONG).show();

				Intent intent = new Intent(this, HomeActivity.class);
				intent.putExtra(EXTRA_MESSAGE, username);
				startActivity(intent);

			} else {
				PopupMessages
						.launchPopup(
								"Login",
								"Wrong username/password combination. Please try again.",
								getApplicationContext());

				/*
				 * Toast.makeText(getApplicationContext(),
				 * "Mauvaise combinaison login/mdp !",
				 * Toast.LENGTH_LONG).show();
				 */
			}
		}

	}
}