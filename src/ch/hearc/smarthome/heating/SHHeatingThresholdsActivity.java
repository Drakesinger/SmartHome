package ch.hearc.smarthome.heating;

import java.io.File;
import java.io.IOException;

import android.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import ch.hearc.smarthome.SHFileUtil;
import ch.hearc.smarthome.R;
import ch.hearc.smarthome.bluetooth.SHBluetoothActivity;
import ch.hearc.smarthome.bluetooth.SHCommunicationProtocol;

public class SHHeatingThresholdsActivity extends SHBluetoothActivity {

	// Save Directory
	static String SAVE_NAME = "thresholds_save.txt";
	static File SAVE_FILEPATH = new File(SHFileUtil.HEATING_DIR.getAbsolutePath()
			+ File.separator + SAVE_NAME);

	// Views
	SeekBar defaultTemp, maxLight, minLight;
	TextView txtTemp, txtMax, txtMin;

	boolean thresholdsSaved = true;
	private static SHCommunicationProtocol protocol;
	

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.heating_thresholds);

		//Bluetooth
		protocol = new SHCommunicationProtocol();
		
		// Initialize every views of the layout
		initViews();

		// Update Seek bars
		updateSeekBars();
	}

	public void saveThresholds(View v) {
		String save = defaultTemp.getProgress() + "\n" + maxLight.getProgress()
				+ "\n" + minLight.getProgress();
		
		// Bluetooth save
		write(protocol.generateDataToSend("h sav thresholds", save));

		if (SHFileUtil.isMediaMounted()) {
			try {
				SHFileUtil.writeTextFile(SAVE_FILEPATH, save, false);
				Toast.makeText(getApplicationContext(), "Thresholds saved !",
						Toast.LENGTH_SHORT).show();
			} catch (IOException e) {
				Toast.makeText(getApplicationContext(),
						"File writing error: " + e.getMessage(),
						Toast.LENGTH_SHORT).show();
			}
		}
		thresholdsSaved = true;
		Log.d("SAVED","saveThresholds: "+thresholdsSaved);
		finish();
	}

	@Override
	public void onBackPressed() {

		// If there is a modification ask to save, else quit
		if (!thresholdsSaved) {
			DialogFragment dialog = new SHHeatingThresholdsSaveDialogFragment();
			dialog.show(getFragmentManager(), "DialogThresholdsNotSaved");
		}else{
			finish();
		}
	}

	/*
	 * This init all views, textviews and seekbars
	 */
	public void initViews() {
		defaultTemp = (SeekBar) findViewById(R.id.heating_thresholds_default);
		maxLight = (SeekBar) findViewById(R.id.heating_thresholds_max);
		minLight = (SeekBar) findViewById(R.id.heating_thresholds_min);
		txtTemp = (TextView) findViewById(R.id.heating_thresholds_txt_default);
		txtMax = (TextView) findViewById(R.id.heating_thresholds_txt_max);
		txtMin = (TextView) findViewById(R.id.heating_thresholds_txt_min);

		defaultTemp.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				thresholdsSaved = false;
				Log.d("SAVED","progChange: "+thresholdsSaved);

			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				txtTemp.setText("Default Temperature " + (progress + 10)
						+ "� C");

			}
		});

		maxLight.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				thresholdsSaved = false;
				Log.d("SAVED","progChange: "+thresholdsSaved);

			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				txtMax.setText("Turn off above " + progress + "%");
				if (progress < minLight.getProgress()) {
					minLight.setProgress(progress);
				}
			}
		});

		minLight.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				thresholdsSaved = false;
				Log.d("SAVED","progChange: "+thresholdsSaved);

			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				txtMin.setText("Turn on below " + progress + "%");
				if (progress > maxLight.getProgress()) {
					maxLight.setProgress(progress);
				}
			}
		});
	}

	public void updateSeekBars() {
		String content = null, info = "";

		if (SHFileUtil.isMediaMounted()) {
			if (SHFileUtil.HEATING_DIR.exists()) {
				if (SAVE_FILEPATH.exists()) {
					try {
						content = SHFileUtil.readTextFile(SAVE_FILEPATH);
					} catch (IOException e) {
						info = "File reading error: " + e.getMessage();
					}
				} else {
					// Create File and restart function
					SHFileUtil.createFile(SAVE_FILEPATH);
					updateSeekBars();
					return;
				}
			} else {
				// Create Tree and restart function
				SHFileUtil.createTree(SHFileUtil.HEATING_DIR);
				updateSeekBars();
				return;
			}
		} else {
			info = "No SD found";
		}

		if (!info.equals("")) {
			Toast.makeText(getApplicationContext(), info, Toast.LENGTH_SHORT)
					.show();
		}

		if (content.equals("")) {
			Toast.makeText(getApplicationContext(), "No save found",
					Toast.LENGTH_SHORT).show();
		} else {
			String[] values = content.split("\n");
			defaultTemp.setProgress(Integer.parseInt(values[0]));
			maxLight.setProgress(Integer.parseInt(values[1]));
			minLight.setProgress(Integer.parseInt(values[2]));
		}

	}
}
