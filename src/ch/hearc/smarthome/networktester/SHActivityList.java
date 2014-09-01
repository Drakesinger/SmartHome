package ch.hearc.smarthome.networktester;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import ch.hearc.smarthome.R;
import ch.hearc.smarthome.bluetooth.SHBluetoothActivity;
import ch.hearc.smarthome.bluetooth.SHBluetoothNetworkManager;

public class SHActivityList extends SHBluetoothActivity
{

	private ArrayList<SHAction>	mActivityList	= new ArrayList<SHAction>( );
	private ListView			lv_ActivityList;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_list);

		lv_ActivityList = (ListView) findViewById(R.id.al_lv_action_list);

		// Remote control activities
		mActivityList.add(new SHAction("Door Control", "Open the door and change the door's password", "ch.hearc.smarthome.door.", "DoorActivity"));
		mActivityList.add(new SHAction("Heating Management", "Manage all aspects of your home's heating.", "ch.hearc.smarthome.heating.", "HeatingMainActivity"));
		mActivityList.add(new SHAction("Post-it", "View the post-its saved for you or for the public, create new post-its.", "ch.hearc.smarthome.notes.", "NoteMenu"));

		// SHBluetoothTesting
		mActivityList.add(new SHAction("TEST DA TOOTH THAT'S BLUE", "NIGGA NIGGA NIGGA NIGGA NIGGA", "ch.hearc.smarthome.networktester.", "SHBluetoothTesting"));

		lv_ActivityList.setAdapter(new SHActionListBaseAdapter(this, mActivityList));
		lv_ActivityList.setOnItemClickListener(new OnItemClickListener( )
			{
				public void onItemClick(AdapterView<?> arg0, View v, int position, long id)
				{
					String activity = mActivityList.get(position).getClassName( );
					String pack = mActivityList.get(position).getPackageName( );
					try
					{
						// Start the selected activity and prevent quitting
						preventCancel = true;
						Class<?> activityClass = Class.forName(pack + activity);
						Intent intent = new Intent(SHActivityList.this, activityClass);
						startActivityForResult(intent, 0);
					}
					catch(ClassNotFoundException e)
					{
						e.printStackTrace( );
					}
				}
			});
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
	public void onBackPressed( )
	{
		// When quitting the activity select reset and disconnect from the
		// device
		disconnect( );
		super.onBackPressed( );
	}

}
