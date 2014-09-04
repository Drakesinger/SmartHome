package ch.hearc.smarthome.heating;

import java.io.File;
import java.io.IOException;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import ch.hearc.smarthome.SHFileUtil;
import ch.hearc.smarthome.R;
import ch.hearc.smarthome.bluetooth.SHCommunicationProtocol;

public class SHHeatingSchedulingsAddDialogFragment extends DialogFragment {

	// Save Directory
	static File MAIN_DIR = new File(Environment.getExternalStorageDirectory()
			.getAbsolutePath()
			+ File.separator
			+ "data"
			+ File.separator
			+ "SmartHome" + File.separator);

	static File HEATING_DIR = new File(MAIN_DIR.getAbsolutePath()
			+ File.separator + "Heating" + File.separator);
	static String SAVE_NAME = "schedulings_save.txt";
	static File SAVE_FILEPATH = new File(HEATING_DIR.getAbsolutePath()
			+ File.separator + SAVE_NAME);

	// Views
	EditText etName;
	TimePicker timePickerStart, timePickerEnd;
	DatePicker datePickerStart, datePickerEnd;
	SeekBar seekbarTemp;
	TextView tvTemp;
	
	// Bluetooth
	private static SHCommunicationProtocol protocol;

	@Override
	public void setTargetFragment(Fragment fragment, int requestCode) {
		// TODO Auto-generated method stub
		super.setTargetFragment(fragment, requestCode);

	}

	public Dialog onCreateDialog(Bundle savedInstanceState) {
		
		protocol = new SHCommunicationProtocol();

		LayoutInflater inflater = getActivity().getLayoutInflater();
		View v = inflater.inflate(
				R.layout.heating_scheduling_addschedulingdialog, null);

		// Views instantiation
		etName = (EditText) v
				.findViewById(R.id.heating_scheduling_add_edittext_name);
		seekbarTemp = (SeekBar) v
				.findViewById(R.id.heating_scheduling_seekbartemp);
		tvTemp = (TextView) v
				.findViewById(R.id.heating_scheduling_add_textview_temp);

		tvTemp.setText("Temperature " + (seekbarTemp.getProgress() + 10)
				+ "° C");

		seekbarTemp.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				tvTemp.setText("Temperature " + (progress + 10) + "° C");

			}
		});

		timePickerStart = (TimePicker) v
				.findViewById(R.id.heating_scheduling_timepicker_start);
		datePickerStart = (DatePicker) v
				.findViewById(R.id.heating_scheduling_datepicker_start);
		timePickerEnd = (TimePicker) v
				.findViewById(R.id.heating_scheduling_timepicker_end);
		datePickerEnd = (DatePicker) v
				.findViewById(R.id.heating_scheduling_datepicker_end);

		AlertDialog.Builder b = new AlertDialog.Builder(getActivity())
				.setTitle("Add heating scheduling")
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {

						String n = etName.getText().toString();
						String t = Integer.toString(seekbarTemp.getProgress() + 10);
						int timeHStart = timePickerStart.getCurrentHour();
						int timeMStart = timePickerStart.getCurrentMinute();
						int dateDStart = datePickerStart.getDayOfMonth();
						int dateMStart = datePickerStart.getMonth();
						int dateYStart = datePickerStart.getYear();
						int timeHEnd = timePickerEnd.getCurrentHour();
						int timeMEnd = timePickerEnd.getCurrentMinute();
						int dateDEnd = datePickerEnd.getDayOfMonth();
						int dateMEnd = datePickerEnd.getMonth();
						int dateYEnd = datePickerEnd.getYear();
						String date = timeHStart + ":" + timeMStart + " "
								+ dateDStart + "/" + dateMStart + "/"
								+ dateYStart + " - " + timeHEnd + ":"
								+ timeMEnd + " " + dateDEnd + "/" + dateMEnd
								+ "/" + dateYEnd;

						// Check if a case is empty
						if (n.isEmpty() || t.isEmpty()) {
							//TODO
							// Display the user that he has to fill everything

						} else {
							addScheduling(n, date, t);
							((SHHeatingSchedulingsActivity) getActivity()).updateList();
							
							dialog.dismiss();
						}

					}
				})
				.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								dialog.dismiss();
							}
						});

		b.setView(v);
		return b.create();

	}

	private void addScheduling(String name, String date, String temp) {

		// Concat save
		String save = name + ";" + date + ";" + temp + "\n";
		
		((SHHeatingSchedulingsActivity) getActivity()).write(protocol.generateDataToSend("h add scheduling", save));

		// Check if Media is mounted ( File exists because
		// HeatingSchedulingsActivity.onCreate() create the file if it doesn't
		// exist)
		if (SHFileUtil.isMediaMounted()) {
			try {
				SHFileUtil.writeTextFile(SAVE_FILEPATH, save, true);
			} catch (IOException e) {
				Toast.makeText(getActivity(),
						"File writing error: " + e.getMessage(),
						Toast.LENGTH_SHORT).show();
			}
		}
	}
}
