package ch.hearc.smarthome.networktester;

import java.util.Set;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import ch.hearc.smarthome.CredentialManager;
import ch.hearc.smarthome.HomeActivity;
import ch.hearc.smarthome.R;
//import ch.hearc.smarthome.PopupMessages;

public class SHBluetoothTesting extends Activity {

	private static final String NAME = "SHBluetoothTesting";

	/* View Components */
	private EditText et_userName;
	private EditText et_password;
	
	/* List Layout Views */
    //private TextView 	mTitle;
    private ListView 	mConversationView;
    private EditText 	mOutEditText;
    private Button		mSendButton;

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
	public static final int MESSAGE_DEVICE_ADDRESS = 5;
	public static final int MESSAGE_TOAST = 6;

	/* Key names received from the BluetoothCommandService Handler */
	public static final String DEVICE_NAME = "device_name";
	public static final String DEVICE_ADDRESS = "device_address";
	public static final String TOAST = "toast";

	/* Name of the connected device */
	private String mConnectedDeviceName = null;

	/* Local Bluetooth adapter */
	private BluetoothAdapter mBluetoothAdapter = null;

	/* Member object for Bluetooth Network Manager */
	private BluetoothNetworkManager mBtNetworkManager = null;

	/* Our local array of Bluetooth devices, 
	 * if we want to connect to more than one */
	Set<BluetoothDevice> mBtDevices;

	/* String Buffer for outgoing messages */
	private StringBuffer mOutStringBuffer;

	/* Array adapter for the conversation thread */
    private ArrayAdapter<String> mConversationArrayAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(NAME, "###On Create###");
		
		/* Start with device list 1st as testing goes */
		//setContentView(R.layout.fragment_login);
		
		/* Set up the window layout */
		setContentView(R.layout.main);
		//requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		//getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title);
		
		/* Set up the custom title */
        //mTitle = (TextView) findViewById(R.id.title_left_text);
        //mTitle.setText(R.string.app_name);
        //mTitle = (TextView) findViewById(R.id.title_right_text);

		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		
		if (mBluetoothAdapter == null) {
			// Device does not support Bluetooth
			Toast.makeText(getApplicationContext(),"Bluetooth error.\n"+"Device is not Bluetooth compatible.",Toast.LENGTH_LONG).show();
			//PopupMessages.launchPopup("Bluetooth error.","Device is not Bluetooth compatible.",this.getApplicationContext());
			finish();
			return;
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.option_menu, menu);
		return true;
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		Intent serverIntent = null;
        switch (item.getItemId()) {
        case R.id.it_connect_scan:
            /* Launch the DeviceListActivity to see devices and do a scan */
            serverIntent = new Intent(this, SHDeviceListActivity.class);
            startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
            return true;
        
        case R.id.it_discoverable:
            /* Ensure this device is discoverable by others */
            ensureDiscoverable();
            return true;
        }
        return false;
    }
	
	@Override
	protected void onStart() {
		super.onStart();

		/* Check if our Bluetooth is enabled or not. */
		if (!mBluetoothAdapter.isEnabled()) {
			/*
			 * Request a Bluetooth enable. setupNetworkManager() called in
			 * onActivityResult.
			 */
			Intent enableBtIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
		} else {
			if (mBtNetworkManager == null) {
				setupNetworkManager();
			}
		}
		getPairedDevices();
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.d(NAME, "### onResume ###");
		
		if (mBtNetworkManager != null) {
			/*
			 * We do not know the state yet. Check if Bluetooth service has
			 * started. If not, start the service.
			 */
			if (mBtNetworkManager.getState() == BluetoothNetworkManager.STATE_NONE) {
				mBtNetworkManager.start();
			}
		}

	}

	private void setupNetworkManager() {
		
		
		// Initialize the array adapter for the conversation thread
        mConversationArrayAdapter = new ArrayAdapter<String>(this, R.layout.message);
        mConversationView = (ListView) findViewById(R.id.in);
        mConversationView.setAdapter(mConversationArrayAdapter);

        // Initialize the compose field with a listener for the return key
        mOutEditText = (EditText) findViewById(R.id.edit_text_out);
        //mOutEditText.setOnEditorActionListener(mWriteListener);
        mOutEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			
			@Override
			public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
				// TODO Auto-generated method stub
				// If the action is a key-up event on the return key, send the message
	            if (actionId == EditorInfo.IME_NULL && event.getAction() == KeyEvent.ACTION_UP) {
	                String message = view.getText().toString();
	                sendMessage(message);
	            }
	            Log.i(NAME, "END onEditorAction");
	            return true;
			}
		});

        // Initialize the send button with a listener that for click events
        mSendButton = (Button) findViewById(R.id.button_send);
        mSendButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // Send a message using content of the edit text widget
                TextView view = (TextView) findViewById(R.id.edit_text_out);
                String message = view.getText().toString();
                sendMessage(message);
            }
        });
		
		
		/*
		 * Initialize the BluetoothNetworkManager to perform Bluetooth
		 * connections
		 */
		mBtNetworkManager = new BluetoothNetworkManager(this, mHandler);

		// Initialize the buffer for outgoing messages
		mOutStringBuffer = new StringBuffer("");

	}


	@Override
	protected void onDestroy() {
		Log.d(NAME, "### onDestroy ###");
		super.onDestroy();
		if (mBtNetworkManager != null) {
			Log.d(NAME, "mBtNetworkManager != null");
			
			mBtNetworkManager.stop();
			Toast.makeText(getApplicationContext(),"Activity closing.\n" + "Android is destroying BluetoothTesting",Toast.LENGTH_LONG).show();
	
		}
	}


	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message _msg) {
			// super.handleMessage(_msg);
			Log.d(SHBluetoothTesting.NAME, "handler::handleMessage("
					+ _msg.what + ")");
			switch (_msg.what) {
			case MESSAGE_STATE_CHANGE:
				switch (_msg.arg1) {
				case BluetoothNetworkManager.STATE_CONNECTED:
					//mTitle.setText(R.string.title_connected_to);
					//mTitle.append(mConnectedDeviceName);
					Toast.makeText(getApplicationContext(), "Connected to "
                            + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
					mConversationArrayAdapter.clear();
					break;
				case BluetoothNetworkManager.STATE_CONNECTING:
					//mTitle.setText(R.string.title_connecting);
					Toast.makeText(getApplicationContext(), "Connecting.", Toast.LENGTH_SHORT).show();
					break;
				case BluetoothNetworkManager.STATE_LISTEN:
				case BluetoothNetworkManager.STATE_NONE:
					//mTitle.setText(R.string.title_not_connected);
					Toast.makeText(getApplicationContext(), "Not connected", Toast.LENGTH_SHORT).show();
				default:
					break;
				}
				break;
			case MESSAGE_READ:
				byte[] readBuffer = (byte[]) _msg.obj;
				/* Create a string from the readBuffer */
				String readMessage = new String(readBuffer,0,_msg.arg1);
				mConversationArrayAdapter.add(mConnectedDeviceName + ": " + readMessage);
				break;
			case MESSAGE_WRITE:
				byte[] writeBuffer = (byte[]) _msg.obj;
				/* Create a string from the writeBuffer */
				String writeMessage = new String(writeBuffer);
				mConversationArrayAdapter.add("Me: " + writeMessage);
				break;
			case MESSAGE_DEVICE_NAME:
				/* Save the connected device's name */
                mConnectedDeviceName = _msg.getData().getString(DEVICE_NAME);
                Toast.makeText(getApplicationContext(), "Connected to "
                               + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                break;
			case MESSAGE_TOAST:
				Toast.makeText(getApplicationContext(), _msg.getData().getString(TOAST), Toast.LENGTH_SHORT).show();
				break;
			}
		}

	};

	// TODO Used if we create a list adapter
	// NOT USED HERE
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

		Log.i(NAME, "onActivityResult(reqC: " + requestCode + ",resC: "
				+ resultCode + ")");

		switch (requestCode) {
		case REQUEST_ENABLE_BT:
			/*
			 * When Bluetooth enabling is asked for we check if user has
			 * canceled the request or not, if he has, display error then quit.
			 */
			super.onActivityResult(requestCode, resultCode, data);

			if (resultCode == Activity.RESULT_OK) {
				/*
				 * Bluetooth has been enabled by the user we can setup our
				 * NetworkManager.
				 */
				setupNetworkManager();
			} else if (resultCode == RESULT_CANCELED) {
				/*
				 * User has canceled the enabling of Bluetooth. Show error popup
				 * and quit.
				 */
				
				Toast.makeText(this, "Bluetooth not enabled.\n"+"The application needs bluetooth enabled in order to continue.", Toast.LENGTH_SHORT).show();
				/* Check if do-able with a handler */
				//PopupMessages.launchPopup("Bluetooth not enabled.","The application needs bluetooth enabled in order to continue.",getApplicationContext());
				
				Log.d(NAME, "onActivityResult. Result canceled. Launch popup done.");
				finish();
			} else {

				/* In case another type of ActivityResult was called */
				Log.i(NAME, "onActivityResult(" + resultCode + ")");
			}
			break;

		case REQUEST_CONNECT_DEVICE:
			// TODO document
			if (resultCode == Activity.RESULT_OK) {
				connectDevice(data);
			}

			break;
		default:
			break;
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

			sendMessage(username + password);
			/*
			 * NetworkManager.sendUsername(username);
			 * NetworkManager.sendPassword(password);
			 */
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
				//PopupMessages.launchPopup("Login","Wrong username/password combination. Please try again.",getApplicationContext());

				Toast.makeText(getApplicationContext(),"Mauvaise combinaison login/mdp !", Toast.LENGTH_LONG).show();

			}
		}

	}

	private void sendMessage(String message) {
		/* Check that we're actually connected before trying anything */
		if (mBtNetworkManager.getState() != BluetoothNetworkManager.STATE_CONNECTED) {
			Toast.makeText(getApplicationContext(),
					"You are not connected to any device.", Toast.LENGTH_SHORT)
					.show();
			return;
		}

		/* Check that there's actually something to send */
		if (message.length() > 0) {
			/* Get the message bytes and tell the BluetoothManager to write */
			byte[] send = message.getBytes();
			mBtNetworkManager.write(send);
			Toast.makeText(getApplicationContext(), "Message sent: " + message,
					Toast.LENGTH_SHORT).show();

			/* Reset out string buffer to zero and clear the edit text field */
			mOutStringBuffer.setLength(0);
			
			
			// WARNING
			mOutEditText.setText(mOutStringBuffer);
		}
	}

	private void connectDevice(Intent _data) {
		
		/* Get the address of the device chosen to connect with */
		//String address = _data.getExtras().getString(DEVICE_NAME);
		String address = _data.getExtras().getString(SHDeviceListActivity.EXTRA_DEVICE_ADDRESS);
		
		BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
		mBtNetworkManager.connect(device);
	}
	
	 private void ensureDiscoverable() {
	        Log.d(NAME, "ensure discoverable");
	        if (mBluetoothAdapter.getScanMode() !=
	            BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
	            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
	            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
	            startActivity(discoverableIntent);
	        }
	    }
}