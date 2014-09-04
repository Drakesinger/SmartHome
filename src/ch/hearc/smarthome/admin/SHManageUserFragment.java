package ch.hearc.smarthome.admin;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import ch.hearc.smarthome.R;

public class SHManageUserFragment extends DialogFragment
{

	// global variables used in this class, will be passed to the calling alert
	// dialog
	private static String	mUserName;
	private static String	mUserPass;

	private static String	mOldUserName	= "old name";
	private static String	mOldUserPass	= "old pass";

	// View components
	private EditText		ad_et_username;
	private EditText		ad_et_password;

	public interface NoticeDialogListener
	{
		public void onManageUserDialogModifyClick(DialogFragment dialog);

		public void onManageUserDialogRemoveClick(DialogFragment dialog);
	}

	// Click listener
	NoticeDialogListener	mListener;

	/**
	 * (non-Javadoc)
	 * 
	 * @see android.app.DialogFragment#onAttach(android.app.Activity)
	 */
	@Override
	public void onAttach(Activity activity)
	{
		// TODO Auto-generated method stub
		super.onAttach(activity);

		try
		{
			// Instantiate the NoticeDialogListener so we can send events to the
			// host
			mListener = (NoticeDialogListener) activity;
		}
		catch(ClassCastException e)
		{
			// The activity doesn't implement the interface, throw exception
			throw new ClassCastException(activity.toString( ) + " must implement NoticeDialogListener");
		}

	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity( ));
		// Get the layout inflater
		LayoutInflater inflater = getActivity( ).getLayoutInflater( );
		// Inflate and set the layout for the dialog
		// Pass null as the parent view because its going in the dialog layout
		final View myDialogView = inflater.inflate(R.layout.administration_modify_dialog, null);

		ad_et_username = (EditText) myDialogView.findViewById(R.id.ad_et_username);
		ad_et_password = (EditText) myDialogView.findViewById(R.id.ad_et_password);

		// Show the old username and password
		ad_et_username.setText(mOldUserName);
		ad_et_password.setText(mOldUserPass);

		builder.setView(myDialogView);

		// Add action buttons
		builder.setPositiveButton(R.string.adm_modify, new DialogInterface.OnClickListener( )
			{
				@Override
				public void onClick(DialogInterface dialog, int id)
				{
					// convert the ET values to Strings and send them to the
					// caller
					mUserName = ad_et_username.getText( ).toString( );
					mUserPass = ad_et_password.getText( ).toString( );
					mListener.onManageUserDialogModifyClick(SHManageUserFragment.this);
				}
			});
		builder.setNegativeButton(R.string.adm_remove, new DialogInterface.OnClickListener( )
			{
				public void onClick(DialogInterface dialog, int id)
				{
					mListener.onManageUserDialogRemoveClick(SHManageUserFragment.this);
					SHManageUserFragment.this.getDialog( ).cancel( );
				}
			});
		builder.setNeutralButton(R.string.adm_cancel, new DialogInterface.OnClickListener( )
			{
				public void onClick(DialogInterface dialog, int id)
				{

					SHManageUserFragment.this.getDialog( ).cancel( );
				}
			});

		return builder.create( );

	}/**/

	/**
	 * @return the mUserName
	 */
	public String getUserName( )
	{
		return mUserName;
	}

	/**
	 * @return the mUserPass
	 */
	public String getUserPass( )
	{
		return mUserPass;
	}


	/**
	 * @return mOldUserName
	 */
	private static String getOldUserName( )
	{
		return mOldUserName;
	}
	
	/**
	 * @param mUserName
	 *            the mUserName to set
	 */
	private static void setOldUserName(String _userName)
	{
		mOldUserName = _userName;
	}

	/**
	 * @param mUserPass
	 *            the mUserPass to set
	 */
	private static void setOldUserPass(String _userPass)
	{
		mOldUserPass = _userPass;
	}

	public void showPreviousData(String _actualUserName, String _actualUserPass)
	{
		setOldUserName(_actualUserName);
		setOldUserPass(_actualUserPass);

	}

}
