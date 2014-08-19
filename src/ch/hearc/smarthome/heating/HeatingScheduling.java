package ch.hearc.smarthome.heating;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import ch.hearc.smarthome.R;

public class HeatingScheduling extends Activity {

	ListView schedulingListView;
	ArrayList<CheckBox> checkboxes = new ArrayList<CheckBox>();
	SchedulingArrayAdapter adapter;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.heating_scheduling);

		String[] namesList, datesList, tempsList;

		// Get datas from the XML scheduling_array.xml
		namesList = getResources().getStringArray(R.array.scheduling_names);
		datesList = getResources().getStringArray(R.array.scheduling_dates);
		tempsList = getResources().getStringArray(R.array.scheduling_temps);

		ArrayList<Schedulings> datas = new ArrayList<Schedulings>();

		// Put the datas in the ArrayList
		for (int i = 0; i < namesList.length; i++) {
			datas.add(new Schedulings(namesList[i], datesList[i], tempsList[i]));
		}

		// ListAdapter -> ListView
		schedulingListView = (ListView) findViewById(R.id.listViewScheduling);
		adapter = new SchedulingArrayAdapter(this,
				R.layout.heating_scheduling_list_item, datas);
		schedulingListView.setAdapter(adapter);

	}

	/*
	 * addHeating (View v) add a new heating scheduling to the array => save in
	 * the XML => Then sync
	 */
	public void addHeating(View v) {

		LayoutInflater inflater = this.getLayoutInflater();
		View promptsView = inflater.inflate(
				R.layout.heating_scheduling_addheatingdialog, null);

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				getApplicationContext());

		// set prompts.xml to alertdialog builder
		alertDialogBuilder.setView(promptsView);

		final EditText userInput = (EditText) promptsView
				.findViewById(R.id.editTextDialogUserInput);

		// set dialog message
		alertDialogBuilder.setView(promptsView);

		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();

		// show it
		alertDialog.show();
	}

	/*
	 * deleteSelection(View v)
	 * 
	 * //TODO The items will be selectionable This method will delete them =>
	 * then sync
	 */
	public void deleteSelection(View v) {

		String s = "";

		schedulingListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

		SparseBooleanArray checked = schedulingListView
				.getCheckedItemPositions();
		for (int i = 0; i < checked.size(); i++) {
			if (checked.valueAt(i) == true) {
				// Tag tag = (Tag)
				// schedulingListView.getItemAtPosition(checked.keyAt(i));
				s += i + " ";
			}
		}

		Toast.makeText(getApplicationContext(), "Selected: " + s,
				Toast.LENGTH_SHORT).show();
	}

}
