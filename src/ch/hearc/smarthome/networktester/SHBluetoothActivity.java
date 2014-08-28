package ch.hearc.smarthome.networktester;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MenuItem;

/**
 * This class is used to build different Bluetooth activities in order to
 * interact with the PIC module. It contains all the necessary to deal with
 * communication. The front end and activity itself is left for the class
 * extending it.
 */
public class SHBluetoothActivity extends Activity implements Handler.Callback
{
	private static SHBluetoothNetworkManager	mBtNetworkManager;
	// When launching a new activity and this one stops it doesn't mean something bad (no connection loss)
	protected boolean							preventCancel;
	private static String						TAG;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// Launched when the activity is created
		super.onCreate(savedInstanceState);
		getActionBar( ).setDisplayHomeAsUpEnabled(true);
		mBtNetworkManager = (SHBluetoothNetworkManager) getApplicationContext( );
	}

	/**
	 * Function used to send string data through bluetooth
	 * 
	 * @param _message
	 *            the string that we want to send
	 * @return True if message was sent
	 *         <dl>
	 *         False if we got an error
	 * */
	protected boolean write(String _message)
	{
		// Send command to the Bluetooth device
		return mBtNetworkManager.write(_message);
	}

	/** Disconnect from the Bluetooth device */
	protected void disconnect( )
	{
		if(SHBluetoothNetworkManager.DEBUG) Log.i(TAG, "Connection end request");
		mBtNetworkManager.disconnect( );
	}

	/**
	 * Can be implemented in derived classes in case a response from the PIC
	 * module is needed.
	 * <dl>
	 * 
	 * Example:
	 * <dl>
	 * <code>
	 * if (msg.what == SHBluetoothNetworkManager.MSG_READ)
	 * <br>
	 * response = ((String) msg.obj).toLowerCase();
	 * </code>
	 * </dl>
	 * 
	 * @param _msg
	 *            the message received
	 * @return false
	 * */
	public boolean handleMessage(Message _msg)
	{
		switch(_msg.what)
		{
			case SHBluetoothNetworkManager.MSG_WRITE:

				if(SHBluetoothNetworkManager.DEBUG) Log.i(TAG, "writing out");
				break;
			case SHBluetoothNetworkManager.MSG_OK:
				// When a child activity returns safely
				if(SHBluetoothNetworkManager.DEBUG) Log.i(TAG, "Result of child activity OK");
				break;
			case SHBluetoothNetworkManager.MSG_CANCEL:
				// When a child activity returns after being canceled
				// (ex: if the connection is lost) cancel this activity
				if(SHBluetoothNetworkManager.DEBUG) Log.e(TAG, "Got canceled");
				setResult(SHBluetoothNetworkManager.MSG_CANCEL, new Intent( ));
				finish( );
				break;
		}
		return false;
	}

	@Override
	protected void onActivityResult(int _requestCode, int _resultCode, Intent _data)
	{
		// Send activity result messages to the handler
		Message.obtain(new Handler(this), _resultCode).sendToTarget( );
	}

	@Override
	protected void onResume( )
	{
		// This is called when the activity resumes
		TAG = getLocalClassName( );
		if(SHBluetoothNetworkManager.DEBUG) Log.i(TAG, "Set handler");
		// Set the handler to receive messages from the main application class
		mBtNetworkManager.setActivityHandler(new Handler(this));
		preventCancel = false;
		super.onResume( );
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem _item)
	{
		// When the user clicks the application icon on the top left
		if(_item.getItemId( ) == android.R.id.home)
		{
			// Behave as if the back button was clicked
			onBackPressed( );
			return true;
		}
		return super.onOptionsItemSelected(_item);
	}

	@Override
	public void onBackPressed( )
	{
		super.onBackPressed( );
		// Pressing the back button quits the activity and informs the parent
		// activity
		if(SHBluetoothNetworkManager.DEBUG) Log.i(TAG, "Back pressed");
		setResult(SHBluetoothNetworkManager.MSG_OK, new Intent( ));
		finish( );
	}

	@Override
	public void finish( )
	{
		// Remove the handler from the main application class
		mBtNetworkManager.setActivityHandler(null);
		super.finish( );
	}

	@Override
	protected void onPause( )
	{
		// Pausing an activity isn't allowed, unless it has been prevented
		if(!preventCancel)
		{
			// Tell itself to cancel
			Message.obtain(new Handler(this), SHBluetoothNetworkManager.MSG_CANCEL).sendToTarget( );
		}
		super.onPause( );
	}
}
