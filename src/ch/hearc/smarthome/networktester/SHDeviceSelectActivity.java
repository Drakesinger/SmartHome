package ch.hearc.smarthome.networktester;

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
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import ch.hearc.smarthome.R;

public class SHDeviceSelectActivity extends Activity implements Handler.Callback {

	
	/* Debugging */
	private static final String 	NAME = "SHDeviceSelectActivity";
	private static final String 	TAG = "DeviceSelect";
    private static final boolean 	DEBUG = true;

    /* Bluetooth members */
    private BluetoothAdapter 			mBtAdapter;
    private SHBluetoothNetworkManager 	mBtNetworkManager;
    
    private Device						mConnectedDevice;
    
    /* View components */
    private ArrayList<Device> 		devAvailableList;
    private ArrayList<Device> 		devPairedList;
    
    private ListView devAvailableListView, devPairedListView;
    
    private DeviceListBaseAdapter 	devAvailableListAdapter;
    private DeviceListBaseAdapter	devPairedListAdapter;
    
    private ProgressDialog 			mConnectionProgressDialog;
    private Button 					b_device_list_scan;
    
    
    /* Intent request codes */
	private static final int REQUEST_ACTION_LIST = 0;
	private static final int REQUEST_CONNECT_DEVICE = 1;
	private static final int REQUEST_ENABLE_BT = 2;

	/* Message types sent from the BluetoothChatService Handler */
	public static final int MESSAGE_STATE_CHANGE	= 1;
	public static final int MESSAGE_READ 			= 2;
	public static final int MESSAGE_WRITE 			= 3;
	public static final int MESSAGE_DEVICE_NAME 	= 4;
	public static final int MESSAGE_DEVICE_ADDRESS 	= 5;
	public static final int MESSAGE_TOAST 			= 6;

	/* Key names received from the BluetoothCommandService Handler */
	public static final String DEVICE_NAME 		= "device_name";
	public static final String DEVICE_ADDRESS 	= "device_address";
	public static final String TOAST 			= "toast";

	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		/* Request the spinner to show progress */
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.device_list);
        
        mBtNetworkManager = (SHBluetoothNetworkManager) getApplicationContext();
        
        /* Start the bluetooth adapter */
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();

        if (SHBluetoothNetworkManager.DEBUG) {
			Toast.makeText(getApplicationContext(), "On Create",Toast.LENGTH_SHORT).show();
		}
        
		// Setup Bluetooth devices lists with custom rows
		devPairedListView = (ListView) findViewById(R.id.lv_device_list_paired_devices);
		devPairedList = new ArrayList<Device>();
		devPairedListAdapter = new DeviceListBaseAdapter(this, devPairedList);
		devPairedListView.setAdapter(devPairedListAdapter);
		devPairedListView.setOnItemClickListener(mDeviceClickListener);

		devAvailableListView = (ListView) findViewById(R.id.lv_device_list_new_devices);
		devAvailableList = new ArrayList<Device>();
		devAvailableListAdapter = new DeviceListBaseAdapter(this, devAvailableList);
		devAvailableListView.setAdapter(devAvailableListAdapter);
		devAvailableListView.setOnItemClickListener(mDeviceClickListener);
		
		// Register a receiver to handle Bluetooth actions
		registerReceiver(mReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
		registerReceiver(mReceiver, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));
        
        /* Initialize the button to perform device discovery */
        b_device_list_scan = (Button) findViewById(R.id.b_device_list_scan);
        b_device_list_scan.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                doDiscovery();
            }
        });
        
    }


	/**
     * Start device discover with the BluetoothAdapter
     */
    public void doDiscovery() {
        if (DEBUG) Log.d(NAME, "doDiscovery()");
        Toast.makeText(getApplicationContext(), "doDiscovery", Toast.LENGTH_SHORT).show();
        
        // Prevent phones without Bluetooth from using this application

		if (!checkBlueToothState()){
			finish();
			return;
		}

        // Indicate scanning in the title
        setProgressBarIndeterminateVisibility(true);
        setTitle(R.string.scanning);

        // Turn off sub-title for new devices
        findViewById(R.id.tv_device_list_new_devices).setVisibility(View.GONE);
        
        devPairedList.clear();
		devPairedListAdapter.notifyDataSetChanged();
		
        // If we're already discovering, stop it
        if (mBtAdapter.isDiscovering()) {
            mBtAdapter.cancelDiscovery();
        }

		// Show already paired devices in the upper list
		Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();
		if(pairedDevices.size() > 0)
		{
			findViewById(R.id.tv_device_list_paired_devices).setVisibility(View.VISIBLE);
			for(BluetoothDevice device : pairedDevices)
			{
				// Signal strength isn't available for paired devices
				devPairedList.add(new Device(device.getName(), device.getAddress()));
			}
			// Tell the list adapter that its data has changed so it would update itself
			devPairedListAdapter.notifyDataSetChanged();
		}

		devAvailableList.clear();
		devAvailableListAdapter.notifyDataSetChanged();
        
        // Request discover from BluetoothAdapter
        mBtAdapter.startDiscovery();
    }

	/**
	 * This method tells the caller if the Bluetooth is enabled or if it even
	 * exists on the phone.
	 * 
	 * @return State of Bluetooth
	 */
	private boolean checkBlueToothState()
	{
		// Inform user that the phone does not have Bluetooth
		if(mBtAdapter == null)
		{
			Toast.makeText(getApplicationContext(), "Bluetooth not available.", Toast.LENGTH_SHORT).show();
			return false;
		}
		else if(!mBtAdapter.isEnabled())
		{
			startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), REQUEST_ENABLE_BT);
			return false;
		}
		return true;
	}


	@Override
	protected void onDestroy()
	{
		super.onDestroy();

		// Make sure we're not doing discovery anymore
		if(mBtAdapter != null)
		{
			mBtAdapter.cancelDiscovery();
		}

		// Unregister broadcast listeners
		this.unregisterReceiver(mReceiver);
	}

	@Override
	protected void onResume()
	{
		if(SHBluetoothNetworkManager.DEBUG) Log.d(TAG, "Set handler");
		//Toast.makeText(getApplicationContext(), "On Resume", Toast.LENGTH_SHORT).show();
		mBtNetworkManager.setActivityHandler(new Handler(this)); 
		super.onResume();
	}

	@Override
	protected void onPause()
	{
		mBtAdapter.cancelDiscovery();
		super.onPause();
	}


	/* (non-Javadoc)
	 * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		switch(requestCode)
		{
		case REQUEST_ACTION_LIST:
			// Send messages from the parent to the handler
			Message.obtain(new Handler(this), resultCode);
			break;
		case REQUEST_ENABLE_BT:
			// For the Bluetooth connection handle messages here
			if(!mBtAdapter.isEnabled())
			{
				Toast.makeText(getApplicationContext(), "Bluetooth must be enabled", Toast.LENGTH_SHORT).show();
			}
			else
			{
				doDiscovery();
			}
			break;
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
			mConnectionProgressDialog.dismiss();
		}

		switch(msg.what)
		{
		case SHBluetoothNetworkManager.MSG_OK:
			// The child activity ended gracefully
			break;
		case SHBluetoothNetworkManager.MSG_CANCEL:
			// The child activity did not end gracefully (connection lost, failed...)
			if(msg.obj != null)
			{
				// If a some text came with the message show in a toast 
				if(SHBluetoothNetworkManager.DEBUG) Log.i(TAG, "Message: " + msg.obj);
				Toast.makeText(SHDeviceSelectActivity.this, (String) msg.obj, Toast.LENGTH_SHORT).show();
			}
			break;
		case SHBluetoothNetworkManager.MSG_CONNECTED:
			// When connected to a device start the activity select
			if(SHBluetoothNetworkManager.DEBUG) Log.i(TAG, "Connection successful to " + msg.obj);
			/* This will be the part where the user can choose any of our 4 activities */
			
			
			setTitle(getResources().getString(R.string.title_connected_to) + " " + mConnectedDevice.getName());
			
			// TODO
			startActivityForResult(new Intent(getApplicationContext(), SHActionListActivity.class), REQUEST_ACTION_LIST);
			break;
		}
		return false;
	}

	/** The on-click listener for all devices in the ListViews */
    private OnItemClickListener mDeviceClickListener = new OnItemClickListener() {
        public void onItemClick(AdapterView<?> _parent, View _view, int _position, long _id) {
            
        	// Cancel discovery because it's costly and we're about to connect
            mBtAdapter.cancelDiscovery();

            // Get the selected device
            Device device = (Device) _parent.getItemAtPosition(_position);
            
			// Show connection dialog and make so that the connection it can be canceled
			mConnectionProgressDialog = ProgressDialog.show(SHDeviceSelectActivity.this, "","Establishing connection...", false, true);
			mConnectionProgressDialog.setOnCancelListener(new OnCancelListener() {
				public void onCancel(DialogInterface _di) {
					// The user can back out at any moment
					mConnectionProgressDialog.dismiss();
					mBtNetworkManager.disconnect(); // TODO check if the same happpens on other app
					if (DEBUG) Log.i(NAME, "Canceled connection progress");
					return;
					}
				});

			// Try to connect to the selected device, once connected the handler will do the rest
			// TODO
			mConnectedDevice = device;
			mBtNetworkManager.connect(BluetoothAdapter.getDefaultAdapter().getRemoteDevice(device.getAddress()));
		}
    };
	
	
    /** The BroadcastReceiver that listens for discovered devices and changes the title when discovery is finished */
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            /* When discovery finds a device */
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                /* Get the BluetoothDevice object from the Intent */
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Device foundDevice = new Device(device.getName(), device.getAddress());
                /* If it's already paired, skip it, because it's been listed already */
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                	devAvailableList.add(foundDevice);
                    devAvailableListAdapter.notifyDataSetChanged();
                    findViewById(R.id.tv_device_list_paired_devices).setVisibility(View.VISIBLE);
                	
                }
            
            /* When discovery is finished, change the Activity title */
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                
            	/* Stop scanning in progress bar */
            	setProgressBarIndeterminateVisibility(false);
                setTitle(R.string.select_device);
               
            }
        }
    };
    
}
