package com.example.smarthome;

import android.app.Activity;
import android.os.Bundle;
import android.view.ActionMode;

public class DoorAdminActivity extends Activity {

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.door_admin);
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onActionModeFinished(android.view.ActionMode)
	 */
	@Override
	public void onActionModeFinished(ActionMode mode) {
		// TODO Auto-generated method stub
		super.onActionModeFinished(mode);
	}

	

}
