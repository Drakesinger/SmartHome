package ch.hearc.smarthome.heating;

import ch.hearc.smarthome.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;

public class HeatingThresholdsSaveDialogFragment extends DialogFragment {
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Use the Builder class for convenient dialog construction
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setMessage(R.string.save_before_quit)
				.setPositiveButton(R.string.yes,
						new DialogInterface.OnClickListener() {
							// SAVE THRESHOLDS
							public void onClick(DialogInterface dialog, int id) {
								Toast.makeText(getActivity(), "Thresholds saved !", Toast.LENGTH_SHORT).show();
								getActivity().finish();
							}
						})
				.setNegativeButton(R.string.no_quit,
						new DialogInterface.OnClickListener() {
							// QUIT W/OUT SAVING
							public void onClick(DialogInterface dialog, int id) {
								getActivity().finish();
							}
						});
		// Create the AlertDialog object and return it
		return builder.create();
	}
}
