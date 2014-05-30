package ch.hearc.smarthome.door;

import ch.hearc.smarthome.PasswordManager;
import ch.hearc.smarthome.PopupMessages;
import ch.hearc.smarthome.R;
import android.app.Activity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.ActionMode;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

public class DoorAdminActivity extends Activity implements OnClickListener {

	/* View components */
	EditText et_door_admin_old_pass, et_door_admin_new_pass1,
			et_door_admin_new_pass2;
	CheckBox cb_show;
	Button b_change;
	TextView tv_door_admin_Message;

	/* EditText Table */
	EditText ets[] = new EditText[3];

	/* String table containing password */
	String stringTable[] = { "old", "new", "confirmed" };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.door_admin);
		initialize_references();
		cb_show.setOnClickListener(this);
		b_change.setOnClickListener(this);
	}

	/** Checks the validity of each password saved in our {@link String}table. */
	private void checkValidity(String[] sT) {

		if (sT[0].compareTo(PasswordManager.getActual_pass()) == 0) {
			// Old password inserted is correct, proceed
			if (sT[1].compareTo(sT[2]) == 0) {
				// New password is confirmed, change it
				PasswordManager.setActual_pass(sT[2]);
				tv_door_admin_Message.setText("");
			} else {
				/*
				 * TODO either show a pop-up or display a warning with a text
				 * view item
				 */
				tv_door_admin_Message
						.setText("The new password fields do not coencide.\nPlease try again.");
			}
		} else {
			/*
			 * TODO either show a pop-up or display a warning with a text view
			 * item
			 */
			PopupMessages.launchPopup("Change password.",
					"The old password you have entered is  not valid",
					DoorAdminActivity.this);
		}
	}

	/**
	 * Fills our string table with the text content of our {@link EditText}
	 * components.
	 */
	private void fillStringTable() {

		for (int i = 0; i < 3; i++) {
			stringTable[i] = convert_EditTextContent_to_Strings(ets[i]);
			// TODO Problem should be solved now
			/*
			 * Log.i("stringTable[" + i + "] = ", stringTable[i]); Log.i("ets["
			 * + i + "] = ", convert_EditTextContent_to_Strings((ets[i])));
			 */
		}
	}

	/**
	 * Convert the content of an {@link EditText} view widget to a
	 * {@link String}.
	 */
	private String convert_EditTextContent_to_Strings(EditText et) {
		return et.getText().toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onActionModeFinished(android.view.ActionMode)
	 */
	@Override
	public void onActionModeFinished(ActionMode mode) {
		// TODO Auto-generated method stub
		super.onActionModeFinished(mode);
	}

	/** Initialize all our used references for this activity. */
	private void initialize_references() {
		b_change = (Button) findViewById(R.id.b_change);
		cb_show = (CheckBox) findViewById(R.id.cb_show);
		et_door_admin_old_pass = (EditText) findViewById(R.id.et_door_admin_old_pass);
		et_door_admin_new_pass1 = (EditText) findViewById(R.id.et_door_admin_new_pass1);
		et_door_admin_new_pass2 = (EditText) findViewById(R.id.et_door_admin_new_pass2);
		tv_door_admin_Message = (TextView) findViewById(R.id.tv_door_admin_Message);
		ets[0] = et_door_admin_old_pass;
		ets[1] = et_door_admin_new_pass1;
		ets[2] = et_door_admin_new_pass2;
	}

	@Override
	public void onClick(View v) { // TODO Auto-generated methodstub
		switch (v.getId()) {
		case R.id.cb_show:

			if (cb_show.isChecked()) {
				// Show all passwords entered
				for (int i = 0; i < ets.length; i++) {
					ets[i].setInputType(InputType.TYPE_CLASS_NUMBER);
				}
			} else {
				Log.i("Checked", "false");
				for (int i = 0; i < ets.length; i++) {
					ets[i].setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD );
				}
			}
			break;

		case R.id.b_change:
			fillStringTable();
			checkValidity(stringTable);
			break;

		default:
			break;
		}

	}

}
