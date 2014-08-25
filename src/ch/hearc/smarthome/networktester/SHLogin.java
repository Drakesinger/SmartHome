package ch.hearc.smarthome.networktester;

import java.util.Set;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import ch.hearc.smarthome.CredentialManager;
import ch.hearc.smarthome.HomeActivity;
import ch.hearc.smarthome.R;

public class SHLogin extends Activity {

	/* View Components */
	private EditText et_userName;
	private EditText et_password;
	public final static String EXTRA_MESSAGE = "extra";

	public static short kfirstUse = 0;

	/* Debugging */
	private static final String TAG = "SHBluetoothTesting";
	private static final boolean DEBUG = true;

	/* List Layout Views */
	private ListView mConversationView;
	private EditText mOutEditText;
	private Button mSendButton;

	/* Intent request codes */
	private static final int REQUEST_CONNECT_DEVICE = 1;
	private static final int REQUEST_ENABLE_BT = 2;

	/* Message types sent from the BluetoothChatService Handler */
	public static final int MESSAGE_STATE_CHANGE = 1;
	public static final int MESSAGE_READ = 2;
	public static final int MESSAGE_WRITE = 3;
	public static final int MESSAGE_DEVICE_NAME = 4;
	public static final int MESSAGE_DEVICE_ADDRESS = 5;
	public static final int MESSAGE_TOAST = 6;

	/* Key names received from the BluetoothCommandService Handler */
	public static final String DEVICE_NAME = "device_name";
	public static final String DEVICE_ADDRESS = "device_address";
	public static final String TOAST = "toast";

	/*
	 * ############################################################### # Objects
	 * used by derived classes for bluetooth communication #
	 * ###############################################################
	 * 
	 * change to private if this is not the best method to implement bluetooth
	 * communcation for the whole app
	 */

	/* Name of the connected device */
	protected String mConnectedDeviceName = null;

	/* Local Bluetooth adapter */
	protected BluetoothAdapter mBluetoothAdapter = null;

	/* Member object for Bluetooth Network Manager */
	protected SHBluetoothNetworkManager mBtNetworkManager = null;

	/*
	 * Our local array of Bluetooth devices, if we want to connect to more than
	 * one
	 */
	Set<BluetoothDevice> mBtDevices;

	/* String Buffer for outgoing messages */
	protected StringBuffer mOutStringBuffer;

	/* Array adapter for the conversation thread */
	protected ArrayAdapter<String> mConversationArrayAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_login);

		/* Initialize pour local bluetooth adapter */
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

		if (mBluetoothAdapter == null) {
			// Device does not support Bluetooth
			Toast.makeText(
					getApplicationContext(),
					"Bluetooth error.\n"
							+ "Device is not Bluetooth compatible.",
					Toast.LENGTH_LONG).show();
			// PopupMessages.launchPopup("Bluetooth error.","Device is not Bluetooth compatible.",this.getApplicationContext());
			finish();
			return;
		}
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

			try {
				// NetworkManager.connect();
			} catch (Exception e) {
				// TODO: handle exception
			}
			/*
			 * NetworkManager.sendUsername(username);
			 * NetworkManager.sendPassword(password);
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

				Toast.makeText(SHLogin.this, "Login OK !", Toast.LENGTH_LONG)
						.show();

				Intent intent = new Intent(this, HomeActivity.class);
				intent.putExtra(EXTRA_MESSAGE, username);
				startActivity(intent);

			} else {
				Toast.makeText(SHLogin.this,
						"Mauvaise combinaison login/mdp !", Toast.LENGTH_LONG)
						.show();
			}
		}

	}
}