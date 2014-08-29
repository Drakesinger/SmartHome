package ch.hearc.smarthome.networktester;

import java.util.ArrayList;

import ch.hearc.smarthome.bluetooth.SHBluetoothActivity;
import ch.hearc.smarthome.bluetooth.SHBluetoothNetworkManager;

import android.os.Bundle;
import android.os.Message;
import android.widget.ListView;


public class SHActionListActivity extends SHBluetoothActivity{

	private ArrayList<Action> activityList = new ArrayList<Action>();
	private ListView lvActionList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public boolean handleMessage(Message msg)
	{
		super.handleMessage(msg);
		// When a child activity returns it passes ok or cancel message
		if(msg.what == SHBluetoothNetworkManager.MSG_OK)
		{
			// When quitting an activity automatically reset the connection
			write("r");
		}
		return false;
	}

	@Override
	public void onBackPressed()
	{
		// When quitting the activity select reset and disconnect from the device
		disconnect();
		super.onBackPressed();
	}
	
	
}
