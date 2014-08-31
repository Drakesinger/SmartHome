package ch.hearc.smarthome.door;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import ch.hearc.smarthome.CredentialManager;
import ch.hearc.smarthome.PopupMessages;
import ch.hearc.smarthome.R;
import ch.hearc.smarthome.bluetooth.SHBluetoothActivity;
import ch.hearc.smarthome.bluetooth.SHBluetoothNetworkManager;
import ch.hearc.smarthome.networktester.SHCommunicationProtocol;

public class DoorActivity extends SHBluetoothActivity
{

	// Our view references
	Button									b_door_main_Open;
	CheckBox								cb_door_main_Change_Password;
	EditText								et_door_main_Password;

	// Our password string
	String									doorPassword;

	// Functions of SHDoorActivity
	private static final String				openRequest		= "d open";

	// By default we do not want to change password after sending the open
	// request
	boolean									checker			= false;
	// Data received from PIC
	private static String					dataReceived	= null;
	// Used to get the function numbers
	public static SHCommunicationProtocol	Protocol;

	// Our intent and context in case of password change decision
	Intent									intent;
	Context									context;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.door_main);

		initializeReferences( );
	}

	public void requestDoorOpen(View v)
	{
		// When we click on our open button we send the password

		context = DoorActivity.this; // TODO check if we do not need
										// getApplicationContext
		doorPassword = convertEditTextContentToStrings(et_door_main_Password);
		checker = cb_door_main_Change_Password.isChecked( );

		String dataToSend = CredentialManager.getActualUser( ) + "," + Protocol.getFunctionID(openRequest) + "," + doorPassword;
		write(dataToSend);
	}

	/**
	 * Convert the content of an {@link EditText} view widget to a
	 * {@link String}.
	 */
	public static String convertEditTextContentToStrings(EditText et)
	{
		return et.getText( ).toString( );
	}

	/** Initialize all our used references for this activity */
	private void initializeReferences( )
	{
		b_door_main_Open = (Button) findViewById(R.id.b_door_main_Open);
		cb_door_main_Change_Password = (CheckBox) findViewById(R.id.cb_door_main_Change_Password);
		et_door_main_Password = (EditText) findViewById(R.id.et_door_main_Password);
	}

	@Override
	public boolean handleMessage(Message _msg)
	{

		if(_msg.what == SHBluetoothNetworkManager.MSG_READ)
		{
			dataReceived = ((String) _msg.obj).toLowerCase( );
			treatResponse(dataReceived);

		}
		return super.handleMessage(_msg);
	}

	private void treatResponse(String _answer)
	{

		if(_answer.contains("door,open"))
		{
			// If the user checked that he wants to change password: we launch a
			// new intent
			if(checker == true)
			{
				// Password entered was correct, save it so that the user cannot
				// change it if he doesn't recall it
				CredentialManager.setDoorPass(doorPassword);

				notifyUser("Opening door.\nYou may now change the password.");
				intent = new Intent(context, DoorAdminActivity.class);
				preventCancel = true;
				startActivity(intent);
			}
			else
			{
				notifyUser("Opening door.");
			}
		}
		else if(_answer.contains("door,not,open"))
		{

			notifyUser("Wrong password, please retry.");

			// Show pop-up dialog
			// TODO
			//@formatter:off
			// PopupMessages.launchPopup("Open door.", "Wrong password, please retry.", DoorActivity.this);
			//@formatter:on
		}
	}

}
