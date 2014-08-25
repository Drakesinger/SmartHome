package ch.hearc.smarthome.heating;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import ch.hearc.smarthome.R;

public class HeatingSchedulingsAddDialogFragment extends DialogFragment {

	// Save directory
	final File SAVE_DIR = new File(Environment.getExternalStorageDirectory()
			.getAbsolutePath()
			+ File.separator
			+ "data"
			+ File.separator
			+ "SmartHome" + File.separator + "Heating");

	// Views
	EditText etName;
	TimePicker timePickerStart, timePickerEnd;
	DatePicker datePickerStart, datePickerEnd;
	SeekBar seekbarTemp;
	TextView tvTemp;

	@Override
	public void setTargetFragment(Fragment fragment, int requestCode) {
		// TODO Auto-generated method stub
		super.setTargetFragment(fragment, requestCode);

	}

	public Dialog onCreateDialog(Bundle savedInstanceState) {

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

		tvTemp.setText("Temperature " + (seekbarTemp.getProgress()+10)+"° C");

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
				tvTemp.setText("Temperature " + (progress + 10)+"° C");

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
						String t = Integer.toString(seekbarTemp.getProgress()+10);
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
							getActivity().runOnUiThread(new Runnable() {
								@Override
								public void run() {
									Toast.makeText(getActivity(),
											"Fill everything please",
											Toast.LENGTH_SHORT).show();
								}
							});

						} else {
							addScheduling(n, date, t);
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

		String state = Environment.getExternalStorageState();
		if (!state.equals(Environment.MEDIA_MOUNTED)) {
			Toast.makeText(getActivity(), "No external storage mounted",
					Toast.LENGTH_SHORT).show();
		} else {
			SAVE_DIR.mkdirs();
			File textFile = new File(SAVE_DIR + File.separator
					+ "schedulings_save.txt");

			// Check if a save exist
			if (!textFile.exists()) {
				try {
					textFile.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
					Log.d("ADD_FRGMNT", "Creation Err" + e.getMessage());
				}
			}
			try {
				// Read file
				String fileContent = readTextFile(textFile);
				// save concat.
				fileContent = name + ";" + date + ";" + temp + "\n";
				// Write file
				writeTextFile(textFile, fileContent);
				Log.d("ADD_FRGMNT", "Save");

			} catch (IOException e) {
				Toast.makeText(getActivity(),
						"Something went wrong! " + e.getMessage(),
						Toast.LENGTH_LONG).show();
				Log.d("ADD_FRGMNT", "Writing Err: " + e.getMessage());

			}
		}

	}

	/*** écriture fichier texte ***/
	private void writeTextFile(File file, String text) throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));
		writer.write(text);
		writer.close();
	}

	/**** Reading Text File ****/
	private String readTextFile(File file) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(file));
		StringBuilder text = new StringBuilder();
		String line;
		while ((line = reader.readLine()) != null) {
			text.append(line);
			text.append("\n");
		}
		reader.close();
		return text.toString();
	}
}
