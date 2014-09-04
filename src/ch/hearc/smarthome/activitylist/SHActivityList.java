package ch.hearc.smarthome.activitylist;

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

	private ArrayList<SHActivityC>	mActivityList	= new ArrayList<SHActivityC>( );
	private ListView				lv_ActivityList;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_list);

		lv_ActivityList = (ListView) findViewById(R.id.al_lv_action_list);

		// Remote control activities
		mActivityList.add(new SHActivityC("Door Control", "Open doors and change the code required for entry.", "ch.hearc.smarthome.door.", "SHDoorActivity"));
		mActivityList.add(new SHActivityC("Heating Management", "Check temperature history and manage all aspects of your home's heating.", "ch.hearc.smarthome.heating.", "SHHeatingMainActivity"));
		mActivityList.add(new SHActivityC("Post-it Management", "View the post-its saved for you or for the public, create new post-its and delete the ones you don't want.", "ch.hearc.smarthome.notes.", "SHNoteMenu"));
		mActivityList.add(new SHActivityC("Administration", "Manage users and their passwords, remove or add new users.", "ch.hearc.smarthome.admin.", "SHAdministration"));
		
		// SHBluetoothTesting video surveillance
		mActivityList.add(new SHActivityC("Buzzer", "To infinite and beyond", "ch.hearc.smarthome.buzzer.", "BuzzerActivity"));

		lv_ActivityList.setAdapter(new SHActivityListBaseAdapter(this, mActivityList));
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
