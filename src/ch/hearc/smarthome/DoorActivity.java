package ch.hearc.smarthome;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import ch.hearc.smarthome.PasswordManager;

public class DoorActivity extends Activity {

	/* Our view references */
	Button b_door_main_Open;
	CheckBox cb_door_main_Change_Password;
	EditText et_door_main_Password;

	/* Our password string */
	// String myPassword;

	/* Our intent and context in case of password change decision */
	Intent i;
	Context c;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.door_main);

		initialize_references();

		// We initialize the password just in case
		PasswordManager.setActual_pass("1111");

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
				boolean password_ok = NetworkManager.send_password();
				if (password_ok == true) {
					if (checker == true) {
						i = new Intent(c, DoorAdminActivity.class);
						startActivity(i);
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
	public static String convert_EditTextContent_to_Strings(EditText et) {
		// String textContent = et.getText().toString();
		return et.getText().toString();
	}

	/** Initialize all our used references for this activity */
	private void initialize_references() {
		b_door_main_Open = (Button) findViewById(R.id.b_door_main_Open);
		cb_door_main_Change_Password = (CheckBox) findViewById(R.id.cb_door_main_Change_Password);
		et_door_main_Password = (EditText) findViewById(R.id.et_door_main_Password);
	}

}
