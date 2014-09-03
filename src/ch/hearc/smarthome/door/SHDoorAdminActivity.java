package ch.hearc.smarthome.door;

import android.os.Bundle;
import android.os.Message;
import android.text.InputType;
import android.util.Log;
import android.view.ActionMode;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import ch.hearc.smarthome.CredentialManager;
import ch.hearc.smarthome.R;
import ch.hearc.smarthome.bluetooth.SHBluetoothActivity;
import ch.hearc.smarthome.bluetooth.SHBluetoothNetworkManager;
import ch.hearc.smarthome.bluetooth.SHCommunicationProtocol;

public class SHDoorAdminActivity extends SHBluetoothActivity implements OnClickListener
{

	// View components
	private EditText						et_door_admin_old_pass;
	private EditText						et_door_admin_new_pass1;
	private EditText						et_door_admin_new_pass2;
	private CheckBox						cb_show;
	private Button							b_change;
	private TextView						tv_door_admin_Message;

	// EditText Table containing our 3 EditText views
	private EditText						ets[]				= new EditText[3];

	// String table containing password
	private String							stringTable[]		= { "old" , "new" , "confirmed" };
	private String							newPassword;

	// Functions of this activity
	private final String					changeDoorPassword	= "d change pass";

	// Data received from PIC
	private static String					dataReceived		= null;
	// Used to get the function numbers
	public static SHCommunicationProtocol	Protocol;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.door_admin);
		Protocol = new SHCommunicationProtocol( );
		initializeReferences( );

		cb_show.setOnClickListener(this);
		b_change.setOnClickListener(this);
	}

	/** Checks the validity of each password saved in our {@link String} table. */
	private void checkValidity(String[ ] _stringTable)
	{
		tv_door_admin_Message.setText("");

		if(_stringTable[0].equals(CredentialManager.getDoorPass( )))
		{
			// Old password inserted is correct, proceed
			if(_stringTable[1].compareTo(_stringTable[2]) == 0)
			{
				newPassword = _stringTable[2];
				// New password is confirmed, change it
				String dataToSend = Protocol.generateDataToSend(changeDoorPassword, newPassword);
				write(dataToSend);

			}
			else
			{
				tv_door_admin_Message.setText("The new password fields do not coencide. Please try again.");
			}
		}
		else
		{
			notifyUser("The old password you have entered is not valid.");
		}
	}

	/**
	 * Fills our string table with the text content of our {@link EditText}
	 * components.
	 */
	private void fillStringTable( )
	{
		for(int i = 0; i < 3; i++)
		{
			stringTable[i] = convertEditTextContentToStrings(ets[i]);
		}
	}

	/**
	 * Convert the content of an {@link EditText} view widget to a
	 * {@link String}.
	 */
	private String convertEditTextContentToStrings(EditText et)
	{
		return et.getText( ).toString( );
	}

	@Override
	public void onActionModeFinished(ActionMode mode)
	{
		notifyUser("onActionModeFinished()\nActionMode:" + mode);
		super.onActionModeFinished(mode);
	}

	/** Initialize all our used references for this activity. */
	private void initializeReferences( )
	{
		b_change = (Button) findViewById(R.id.door_admin_b_change);
		cb_show = (CheckBox) findViewById(R.id.door_admin_cb_show);
		et_door_admin_old_pass = (EditText) findViewById(R.id.door_admin_et_old_pass);
		et_door_admin_new_pass1 = (EditText) findViewById(R.id.door_admin_et_new_pass1);
		et_door_admin_new_pass2 = (EditText) findViewById(R.id.door_admin_et_new_pass2);
		tv_door_admin_Message = (TextView) findViewById(R.id.door_admin_tv_message);
		ets[0] = et_door_admin_old_pass;
		ets[1] = et_door_admin_new_pass1;
		ets[2] = et_door_admin_new_pass2;
	}

	@Override
	public void onClick(View _view)
	{
		switch(_view.getId( ))
		{
			case R.id.door_admin_cb_show:

				if(cb_show.isChecked( ))
				{
					// Show all passwords entered
					for(int i = 0; i < ets.length; i++)
					{
						ets[i].setInputType(InputType.TYPE_CLASS_NUMBER);
					}
				}
				else
				{
					Log.i("Checked", "false");
					for(int i = 0; i < ets.length; i++)
					{
						ets[i].setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
					}
				}
				break;

			case R.id.door_admin_b_change:
				fillStringTable( );
				checkValidity(stringTable);
				break;

			default:
				break;
		}

	}

	@Override
	public boolean handleMessage(Message _msg)
	{

		if(_msg.what == SHBluetoothNetworkManager.MSG_READ)
		{
			dataReceived = ((String) _msg.obj).toLowerCase( );
			if(dataReceived.contains("change pass ok"))
			{
				CredentialManager.setDoorPass(newPassword);
				notifyUser("New door password has been set.");
				finish();
			}
		}

		return super.handleMessage(_msg);
	}

}
