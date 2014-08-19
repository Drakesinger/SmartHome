package ch.hearc.smarthome;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class PopupMessages {

	/**
	 * Open a pop-up containing the title {@link messageType} and the
	 * {@link messageContent}.
	 */
	public static void launchPopup(String messageType, String messageContent, Context c) {
		final Dialog myDialog;
		Button popupConfirmed;
		TextView message;

		myDialog = new Dialog(c);
		myDialog.setContentView(R.layout.pop_up_messages);
		myDialog.setTitle(messageType);
		myDialog.setCancelable(false);
		Log.e("PoupupMessages", "myDialog done");
		message = (TextView) myDialog.findViewById(R.id.tv_message_text);
		message.setText(messageContent);
		popupConfirmed = (Button) myDialog.findViewById(R.id.b_message_seen);

		popupConfirmed.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				myDialog.dismiss();

			}

		});
		myDialog.show();
	}

	public void launchPopupAlert(String messageType, String messageContent) {
		// TODO create the same thing but with an alert dialog instead of dialog

	}
}
