package ch.hearc.smarthome.heating;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import ch.hearc.smarthome.R;

public class SHHeatingThresholdsSaveDialogFragment extends DialogFragment {
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Use the Builder class for convenient dialog construction
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setMessage(R.string.save_before_quit)
				.setPositiveButton(R.string.yes,
						new DialogInterface.OnClickListener() {
							// SAVE THRESHOLDS
							public void onClick(DialogInterface dialog, int id) {
								((SHHeatingThresholdsActivity) getActivity())
										.saveThresholds(null);
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
