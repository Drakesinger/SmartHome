package ch.hearc.smarthome.debuging;

import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import ch.hearc.smarthome.R;
import ch.hearc.smarthome.bluetooth.SHBluetoothActivity;
import ch.hearc.smarthome.bluetooth.SHBluetoothNetworkManager;

public class SHBluetoothDebugger extends SHBluetoothActivity
{

	// Debugging
	private static final String		TAG						= "SHBluetoothTesting";

	// List Layout Views
	private ListView				mConversationView;
	private EditText				mOutEditText;
	private Button					mSendButton;

	public final static String		EXTRA_MESSAGE			= "com.example.myfirstapp.MESSAGE";

	// Global variables
	public static short				kfirstUse				= 1;

	// Constants to indicate message contents
	public static final int			MSG_OK					= 0;
	public static final int			MSG_READ				= 1;
	public static final int			MSG_WRITE				= 2;
	public static final int			MSG_CANCEL				= 3;
	public static final int			MSG_CONNECTED			= 4;

	// Key names received from the BluetoothCommandService Handler
	public static final String		DEVICE_NAME				= "device_name";
	public static final String		DEVICE_ADDRESS			= "device_address";
	public static final String		TOAST					= "toast";

	// Name of the connected device
	public String					mConnectedDeviceName	=  "PIC";

	// Local Bluetooth adapter
	// public BluetoothAdapter mBluetoothAdapter;

	// String Buffer for outgoing messages
	public StringBuffer				mOutStringBuffer;

	// Array adapter for the conversation thread
	public ArrayAdapter<String>		mConversationArrayAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		// Set up the window layout
		setContentView(R.layout.main);
		setTitle(R.string.app_name);
		
		//@formatter:off
		/*
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		
		if (mBluetoothAdapter == null) {
			// Device does not support Bluetooth
			Toast.makeText(getApplicationContext(),"Bluetooth error.\n"+"Device is not Bluetooth compatible.",Toast.LENGTH_LONG).show();
			//PopupMessages.launchPopup("Bluetooth error.","Device is not Bluetooth compatible.",this.getApplicationContext());
			finish();
			return;
		}		
		 */
		//@formatter:on
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater( ).inflate(R.menu.option_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{

		// Intent serverIntent = null;
		switch(item.getItemId( ))
		{
			case R.id.it_disconnect:
				// TODO make a disconnect method
				disconnect( );
				return true;
			case R.id.it_quit:
				onBackPressed( );
				return true;
		}
		return false;
	}

	@Override
	protected void onStart( )
	{
		super.onStart( );
		setupNetworkManager( );
	}

	private void setupNetworkManager( )
	{

		// Initialize the array adapter for the conversation thread
		mConversationArrayAdapter = new ArrayAdapter<String>(this, R.layout.message);
		mConversationView = (ListView) findViewById(R.id.in);
		mConversationView.setAdapter(mConversationArrayAdapter);

		// Initialize the compose field with a listener for the return key
		mOutEditText = (EditText) findViewById(R.id.edit_text_out);

		// Initialize the send button with a listener that for click events
		mSendButton = (Button) findViewById(R.id.button_send);
		mSendButton.setOnClickListener(new OnClickListener( )
			{
				public void onClick(View v)
				{
					// Send a message using content of the edit text widget
					TextView view = (TextView) findViewById(R.id.edit_text_out);
					String message = view.getText( ).toString( );
					sendMessage(message);
				}
			});
		// Initialize the buffer for outgoing messages
		mOutStringBuffer = new StringBuffer("");
	}

	@Override
	public boolean handleMessage(Message _msg)
	{
		Log.d(TAG, "handler::handleMessage(" + _msg.what + ")");
		switch(_msg.what)
		{
			case SHBluetoothNetworkManager.MSG_READ:
				byte[ ] readBuffer = (byte[ ]) _msg.obj;
				// Create a string from the readBuffer
				String readMessage = new String(readBuffer, 0, _msg.arg1);
				mConversationArrayAdapter.add(mConnectedDeviceName + ": "
												+ readMessage);
				break;
			case SHBluetoothNetworkManager.MSG_WRITE:
				byte[ ] writeBuffer = (byte[ ]) _msg.obj;
				// Create a string from the writeBuffer
				String writeMessage = new String(writeBuffer);
				mConversationArrayAdapter.add("Me: " + writeMessage);
				break;
		}
		return super.handleMessage(_msg);
	}

	private void sendMessage(String message)
	{
		// Check that we're actually connected before trying anything

		// Check that there's actually something to send
		if(message.length( ) > 0)
		{
			// Get the message bytes and tell the BluetoothManager to write
			String message_for_PIC = message;
			write(message_for_PIC);
			Toast.makeText(getApplicationContext( ), "Message sent: "
														+ message_for_PIC, Toast.LENGTH_SHORT).show( );

			// Reset out string buffer to zero and clear the edit text field
			mOutStringBuffer.setLength(0);

			mOutEditText.setText(mOutStringBuffer);
		}
	}
}
