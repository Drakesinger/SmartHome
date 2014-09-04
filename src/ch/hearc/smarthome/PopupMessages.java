package ch.hearc.smarthome;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class PopupMessages
{

	/**
	 * Open a pop-up containing the title {@link messageType} and the
	 * {@link messageContent}.
	 */
	public static void launchPopup(String messageType, String messageContent, Context c)
	{
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

		popupConfirmed.setOnClickListener(new OnClickListener( )
			{

				@Override
				public void onClick(View v)
				{

					myDialog.dismiss( );

				}

			});
		myDialog.show( );
	}

	public static void errorPopup(Context _context, Exception _exception)
	{
		AlertDialog.Builder adb = new AlertDialog.Builder(_context);
		adb.setTitle("Oups...");
		adb.setMessage("The app would have crashed:\n" + _exception.getLocalizedMessage( ));
		adb.setPositiveButton("OK", new AlertDialog.OnClickListener( )
			{
				public void onClick(DialogInterface dialog, int which)
				{
					dialog.dismiss( );
				}
			});
		adb.show( );

	}

	public static void popup(Context _context, String _errorDesc)
	{
		AlertDialog.Builder adb = new AlertDialog.Builder(_context);
		adb.setTitle("Oups...");
		adb.setMessage(_errorDesc);
		adb.setPositiveButton("OK", new AlertDialog.OnClickListener( )
			{
				public void onClick(DialogInterface dialog, int which)
				{
					dialog.dismiss( );
				}
			});
		adb.show( );
		
	}
}
