package ch.hearc.smarthome.bluetooth;

import java.util.ArrayList;
import java.util.Set;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import ch.hearc.smarthome.R;
import ch.hearc.smarthome.SHLogin;

/**
 * This activity is launched at application start after the Bluetooth Manager.
 * It will present the user with the already paired Bluetooth devices and allows
 * the user to search for other devices in the area.
 */
public class SHDeviceSelectActivity extends Activity implements Handler.Callback
{

	// Debugging
	private static final String			NAME				= "SHDeviceSelectActivity";
	private static final String			TAG					= "DeviceSelect";
	private static final boolean		DEBUG				= true;

	// Bluetooth members
	private BluetoothAdapter			mBtAdapter;
	private SHBluetoothNetworkManager	mBtNetworkManager;

	public SHDevice						mConnectedDevice;

	// View components
	private ArrayList<SHDevice>			devAvailableList;
	private ArrayList<SHDevice>			devPairedList;

	private ListView					devAvailableListView;
	private ListView					devPairedListView;

	private SHDeviceListBaseAdapter		devAvailableListAdapter;
	private SHDeviceListBaseAdapter		devPairedListAdapter;

	private ProgressDialog				mConnectionProgressDialog;
	private Button						b_device_list_scan;

	// Intent request codes
	private static final int			REQUEST_LOGIN		= 1;
	private static final int			REQUEST_ENABLE_BT	= 2;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// Request the spinner to show progress
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.device_list);

		mBtNetworkManager = (SHBluetoothNetworkManager) getApplicationContext( );

		// Start the bluetooth adapter
		mBtAdapter = BluetoothAdapter.getDefaultAdapter( );

		if(SHBluetoothNetworkManager.DEBUG) Toast.makeText(getApplicationContext( ), "On Create", Toast.LENGTH_SHORT).show( );

		// Setup Bluetooth devices lists with custom rows
		devPairedListView = (ListView) findViewById(R.id.lv_device_list_paired_devices);
		devPairedList = new ArrayList<SHDevice>( );
		devPairedListAdapter = new SHDeviceListBaseAdapter(this, devPairedList);
		devPairedListView.setAdapter(devPairedListAdapter);
		devPairedListView.setOnItemClickListener(mDeviceClickListener);

		devAvailableListView = (ListView) findViewById(R.id.lv_device_list_new_devices);
		devAvailableList = new ArrayList<SHDevice>( );
		devAvailableListAdapter = new SHDeviceListBaseAdapter(this, devAvailableList);
		devAvailableListView.setAdapter(devAvailableListAdapter);
		devAvailableListView.setOnItemClickListener(mDeviceClickListener);

		// Register a receiver to handle Bluetooth actions
		registerReceiver(mReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
		registerReceiver(mReceiver, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));

		// Initialize the button to perform device discovery
		b_device_list_scan = (Button) findViewById(R.id.b_device_list_scan);
	}

	/**
	 * Start device discover with the BluetoothAdapter
	 */
	public void doDiscovery(View _view)
	{
		if(DEBUG) Log.d(NAME, "doDiscovery()");

		b_device_list_scan.setText("Cancel scanning");

		Toast.makeText(getApplicationContext( ), "doDiscovery", Toast.LENGTH_SHORT).show( );

		// Prevent phones without Bluetooth from using this application
		if(!checkBlueToothState( ))
		{
			finish( );
			return;
		}

		// Indicate scanning in the title
		setProgressBarIndeterminateVisibility(true);
		setTitle(R.string.scanning);

		// If we're already discovering, stop it
		if(mBtAdapter.isDiscovering( ))
		{
			mBtAdapter.cancelDiscovery( );
			// Reset the button
			b_device_list_scan.setText(R.string.button_scan);
		}
		else
		{
			// Turn off sub-title for new devices
			findViewById(R.id.tv_device_list_new_devices).setVisibility(View.VISIBLE);

			devPairedList.clear( );
			devPairedListAdapter.notifyDataSetChanged( );

			// Show already paired devices in the upper list
			Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices( );
			if(pairedDevices.size( ) > 0)
			{
				findViewById(R.id.tv_device_list_paired_devices).setVisibility(View.VISIBLE);
				for(BluetoothDevice device : pairedDevices)
				{
					devPairedList.add(new SHDevice(device.getName( ), device.getAddress( )));
				}
				// Tell the list adapter that its data has changed so it would
				// update itself
				devPairedListAdapter.notifyDataSetChanged( );
			}

			devAvailableList.clear( );
			devAvailableListAdapter.notifyDataSetChanged( );

			// Request discover from BluetoothAdapter
			mBtAdapter.startDiscovery( );
		}
	}

	/**
	 * This method tells the caller if the Bluetooth is enabled or if it even
	 * exists on the phone.
	 * 
	 * @return State of Bluetooth
	 */
	private boolean checkBlueToothState( )
	{
		// Inform user that the phone does not have Bluetooth
		if(mBtAdapter == null)
		{
			Toast.makeText(getApplicationContext( ), "Bluetooth not available.", Toast.LENGTH_SHORT).show( );
			return false;
		}
		else if(!mBtAdapter.isEnabled( ))
		{
			startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), REQUEST_ENABLE_BT);
			return false;
		}
		return true;
	}

	@Override
	protected void onDestroy( )
	{
		super.onDestroy( );

		// Make sure we're not doing discovery anymore
		if(mBtAdapter != null)
		{
			mBtAdapter.cancelDiscovery( );
		}

		// Unregister broadcast listeners
		this.unregisterReceiver(mReceiver);
	}

	@Override
	protected void onResume( )
	{
		if(SHBluetoothNetworkManager.DEBUG) Log.d(TAG, "Set handler");
		mBtNetworkManager.setActivityHandler(new Handler(this));
		super.onResume( );
	}

	@Override
	protected void onPause( )
	{
		mBtAdapter.cancelDiscovery( );
		super.onPause( );
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);

		switch(requestCode)
		{
			case REQUEST_LOGIN:
				// Send messages from the parent to the handler
				Message.obtain(new Handler(this), resultCode);
				break;
			case REQUEST_ENABLE_BT:
				// For the Bluetooth connection handle messages here
				if(!mBtAdapter.isEnabled( ))
				{
					Toast.makeText(getApplicationContext( ), "Bluetooth must be enabled", Toast.LENGTH_SHORT).show( );
				}
				else
				{
					doDiscovery(findViewById(R.id.b_device_list_scan));
				}
				break;
		}
	}

	@Override
	public void onBackPressed( )
	{
		if(mBtAdapter.isDiscovering( ))
		{
			mBtAdapter.cancelDiscovery( );
			this.unregisterReceiver(mReceiver);
		}
		if(mBtAdapter.isEnabled( ) && !mBtAdapter.isDiscovering( ))
		{
			mBtNetworkManager.stop( );
			System.exit(0);
		}
	}

	/**
	 * When a message comes from either the Bluetooth activity (when enabling
	 * Bluetooth) or the child activity it's processed here.
	 * 
	 * @return
	 */
	public boolean handleMessage(Message msg)
	{
		// In case the connection dialog hasn't disappeared yet
		if(mConnectionProgressDialog != null)
		{
			mConnectionProgressDialog.dismiss( );
		}

		switch(msg.what)
		{
			case SHBluetoothNetworkManager.MSG_OK:
				// The child activity ended gracefully
				break;
			case SHBluetoothNetworkManager.MSG_CONNECT_FAIL:
				// Could not connect, inform user.
				notifyUser("Could not connect. Retrying.");
			case SHBluetoothNetworkManager.MSG_CANCEL:
				// The child activity did not end gracefully (connection lost,
				// failed,etc.)
				if(msg.obj != null)
				{
					// If some text came with the message show in a toast
					if(SHBluetoothNetworkManager.DEBUG) Log.d(TAG, "Message: " + msg.obj);
					notifyUser((String) msg.obj);
					// Toast.makeText(SHDeviceSelectActivity.this, (String)
					// msg.obj, Toast.LENGTH_SHORT).show( );
				}
				break;
			case SHBluetoothNetworkManager.MSG_CONNECTED:
				// When connected to a device start the activity select
				if(SHBluetoothNetworkManager.DEBUG) Log.i(TAG, "Connection successful to " + msg.obj);
				// User now needs to login
				setTitle(getResources( ).getString(R.string.title_connected_to) + " " + mConnectedDevice.getName( ));
				startActivityForResult(new Intent(getApplicationContext( ), SHLogin.class), REQUEST_LOGIN);
				break;
		}
		return false;
	}

	private void notifyUser(String _string)
	{
		// TODO Auto-generated method stub
		Toast.makeText(SHDeviceSelectActivity.this, _string, Toast.LENGTH_SHORT).show( );
	}

	//@formatter:off
	/** The on-click listener for all devices in the ListViews */
    private OnItemClickListener mDeviceClickListener = new OnItemClickListener() {
        public void onItemClick(AdapterView<?> _parent, View _view, int _position, long _id) {

        	// Cancel discovery because it's costly and we're about to connect
            mBtAdapter.cancelDiscovery();

            // Get the selected device
            SHDevice device = (SHDevice) _parent.getItemAtPosition(_position);
            
			// Show connection dialog and make so that the connection it can be canceled
			mConnectionProgressDialog = ProgressDialog.show(SHDeviceSelectActivity.this, "","Establishing connection...", false, true);
			mConnectionProgressDialog.setOnCancelListener(new OnCancelListener() {
				public void onCancel(DialogInterface _di) {
					// The user can back out at any moment
					mConnectionProgressDialog.dismiss();
					mBtNetworkManager.disconnect();
					if (DEBUG) Log.i(NAME, "Canceled connection progress");
					return;
					}
				});

			mConnectedDevice = device;
			mBtNetworkManager.connect(BluetoothAdapter.getDefaultAdapter().getRemoteDevice(device.getAddress()));
		}
    };


    /** The BroadcastReceiver that listens for discovered devices and changes the title when discovery is finished */
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
    	@Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device */
            if (BluetoothDevice.ACTION_FOUND.equals(action)) 
            {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                SHDevice foundDevice = new SHDevice(device.getName(), device.getAddress());
                // If it's already paired, skip it, because it's been listed already
                if (device.getBondState() != BluetoothDevice.BOND_BONDED)
                {
                	devAvailableList.add(foundDevice);
                    devAvailableListAdapter.notifyDataSetChanged();
                    findViewById(R.id.tv_device_list_paired_devices).setVisibility(View.VISIBLE); 	
                }
            
            } 
            else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) 
            {
            	// When discovery is finished, change the Activity title
            	// Stop scanning in progress bar
            	setProgressBarIndeterminateVisibility(false);
                setTitle(R.string.select_device);
                b_device_list_scan.setText(R.string.button_scan);
               
            }
        }
    };
	//@formatter:on
}
