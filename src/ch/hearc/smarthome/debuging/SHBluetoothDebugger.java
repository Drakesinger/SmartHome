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
	private static final String	TAG						= "SHBluetoothTesting";

	// List Layout Views
	private ListView			mConversationView;
	private EditText			mOutEditText;
	private Button				mSendButton;

	public final static String	EXTRA_MESSAGE			= "com.example.myfirstapp.MESSAGE";

	// Global variables
	public static short			kfirstUse				= 1;

	// Key names received from the BluetoothCommandService Handler
	public static final String	DEVICE_NAME				= "device_name";
	public static final String	DEVICE_ADDRESS			= "device_address";
	public static final String	TOAST					= "toast";

	// Name of the connected device
	public String				mConnectedDeviceName	= "PIC";

	// String Buffer for outgoing messages
	public StringBuffer			mOutStringBuffer;

	// Array adapter for the conversation thread
	public ArrayAdapter<String>	mConversationArrayAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		// Set up the window layout
		setContentView(R.layout.debugging_screen);
		setTitle("Debugger");
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
		mConversationArrayAdapter = new ArrayAdapter<String>(this, R.layout.debugging_screen_message);
		mConversationView = (ListView) findViewById(R.id.debugging_screen_lv_in);
		mConversationView.setAdapter(mConversationArrayAdapter);

		// Initialize the compose field with a listener for the return key
		mOutEditText = (EditText) findViewById(R.id.debugging_screen_et_out);

		// Initialize the send button with a listener that for click events
		mSendButton = (Button) findViewById(R.id.debugging_screen_b_send);
		mSendButton.setOnClickListener(new OnClickListener( )
			{
				public void onClick(View v)
				{
					// Send a message using content of the edit text widget
					TextView view = (TextView) findViewById(R.id.debugging_screen_et_out);
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
				String readBuffer = ((String) _msg.obj);
				if(SHBluetoothNetworkManager.DEBUG) Log.d(TAG, "readBuffer = " + readBuffer);
				mConversationArrayAdapter.add(mConnectedDeviceName + ": " + readBuffer);
				break;
			case SHBluetoothNetworkManager.MSG_WRITE:
				String writeBuffer = ((String) _msg.obj);
				if(SHBluetoothNetworkManager.DEBUG) Log.d(TAG, "writeBuffer = " + writeBuffer);
				mConversationArrayAdapter.add("Me: " + writeBuffer);
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
			Toast.makeText(getApplicationContext( ), "Message sent: " + message_for_PIC, Toast.LENGTH_SHORT).show( );

			// Reset out string buffer to zero and clear the edit text field
			mOutStringBuffer.setLength(0);

			mOutEditText.setText(mOutStringBuffer);
		}
	}
}
