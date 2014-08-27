package ch.hearc.smarthome.door;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import ch.hearc.smarthome.CredentialManager;
import ch.hearc.smarthome.PopupMessages;
import ch.hearc.smarthome.R;
import ch.hearc.smarthome.networktester.BluetoothActivity;

public class DoorActivity extends BluetoothActivity {

	/* Our view references */
	Button b_door_main_Open;
	CheckBox cb_door_main_Change_Password;
	EditText et_door_main_Password;

	/* Our password string */
	String myPassword;
	
	/* Functions of SHDoorActivity */
	private static final String open = "d open";

	/* Our intent and context in case of password change decision */
	Intent i;
	Context c;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.door_main);

		initializeReferences();

		b_door_main_Open.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				/*
				 * When we click on our open button we send the password but
				 * since we don't have a send method yet, let's show it
				 */
				c = DoorActivity.this;

				boolean checker = cb_door_main_Change_Password.isChecked();
				/*
				 * If the user checked that he wants to change password: we
				 * compare the password, then send it, and then launch a new
				 * intent
				 */
				
				myPassword = convertEditTextContentToStrings(et_door_main_Password);
				boolean password_ok = myPassword.equals(CredentialManager.getDoorPass());
				
				if (password_ok == true) {
					if (checker == true) {
						i = new Intent(c, DoorAdminActivity.class);
						startActivity(i);
					}else {
						Toast.makeText(getApplicationContext(), "Opening door.",
								Toast.LENGTH_SHORT).show();
					}
				} else {
					// Show pop-up
					PopupMessages.launchPopup("Open door.",
							"Wrong password, please retry.", DoorActivity.this);
				}
			}
		});

	}

	/**
	 * Convert the content of an {@link EditText} view widget to a
	 * {@link String}.
	 */
	public static String convertEditTextContentToStrings(EditText et) {
		// String textContent = et.getText().toString();
		return et.getText().toString();
	}

	/** Initialize all our used references for this activity */
	private void initializeReferences() {
		b_door_main_Open = (Button) findViewById(R.id.b_door_main_Open);
		cb_door_main_Change_Password = (CheckBox) findViewById(R.id.cb_door_main_Change_Password);
		et_door_main_Password = (EditText) findViewById(R.id.et_door_main_Password);
	}

}
