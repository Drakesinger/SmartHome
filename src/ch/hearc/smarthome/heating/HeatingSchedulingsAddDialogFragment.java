package ch.hearc.smarthome.heating;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import ch.hearc.smarthome.R;

public class HeatingSchedulingsAddDialogFragment extends DialogFragment {

	EditText etName, etDate, etTemp;
	
	@Override
	public void setTargetFragment(Fragment fragment, int requestCode) {
		// TODO Auto-generated method stub
		super.setTargetFragment(fragment, requestCode);
		
		
	}

	public Dialog onCreateDialog(Bundle savedInstanceState) {

		etName = (EditText)getActivity().findViewById(R.id.heating_scheduling_add_edittext_name);
		etDate = (EditText)getActivity().findViewById(R.id.heating_scheduling_add_edittext_date);
		etTemp = (EditText)getActivity().findViewById(R.id.heating_scheduling_add_edittext_temp);
		
		AlertDialog.Builder b = new AlertDialog.Builder(getActivity())
				.setTitle("Add heating scheduling")
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						//TODO add
						getActivity().runOnUiThread(new Runnable() {
					        @Override
					        public void run() {
					            Toast.makeText(getActivity(), "Add", Toast.LENGTH_SHORT).show();
					        }
					    });

						dialog.dismiss();
					}
				})
				.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								dialog.dismiss();
							}
						});

		LayoutInflater i = getActivity().getLayoutInflater();

		View v = i.inflate(R.layout.heating_scheduling_addschedulingdialog,
				null);

		b.setView(v);
		return b.create();

	}
}
