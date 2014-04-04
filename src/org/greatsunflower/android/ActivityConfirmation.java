package org.greatsunflower.android;

import java.util.Calendar;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class ActivityConfirmation extends SherlockFragmentActivity {

	private Button startButton;
	private String text = "Yes";
	final ActivityConfirmation confirmation = this;
	private ObservationsDataSource datasource;
	private SQLiteObservations observation = null;
	private SharedPreferences pref;
	private int sessionId, count;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_confirmation);

		datasource = new ObservationsDataSource(confirmation);
		datasource.open();
		pref = getApplicationContext().getSharedPreferences("APP_PREFERENCES",
				MODE_PRIVATE);

		sessionId = pref.getInt("SESSION_ID", -1);
		startButton = (Button) findViewById(R.id.startButton);
		if (pref.getString("OBSERVATION_TYPE", null).equals("Stationary")) {
			confirmation.setTitle("Step 2 of 5");
		} else
			confirmation.setTitle("Step 2 of 4");
		count = datasource.getImageCount(sessionId);

		RadioGroup radioGroup = (RadioGroup) findViewById(R.id.optionRadioGroup);
		radioGroup
				.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(RadioGroup group, int checkedId) {
						RadioButton checkedRadioButton = (RadioButton) findViewById(checkedId);
						text = checkedRadioButton.getText().toString();
						if (text.equalsIgnoreCase("No")) {
							if (pref.getString("OBSERVATION_TYPE", null)
									.equals("Stationary")) {
								startButton.setText("Step 4: Identify Plant");
							} else {
								startButton
										.setText("Step 4: Submit Observation");
							}
						} else {
							startButton.setText("Step 3: Identify Pollinators");
						}

						Log.d("START SCREEN", text);
					}
				});

		startButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent1, intent2;
				if (count == 0) {
					intent1 = new Intent(confirmation,
							ObservationAnnotation.class);
				} else {
					// intent1 = new Intent(confirmation,
					// GridViewActivity.class);
					intent1 = new Intent(confirmation,
							MultiSelectGridview.class);
				}
				intent1 = new Intent(confirmation, ObservationAnnotation.class);
				if (pref.getString("OBSERVATION_TYPE", null).equals(
						"Stationary")) {
					intent2 = new Intent(confirmation, PlantAnnotation.class);
				} else {
					intent2 = new Intent(confirmation, SubmissionActivity.class);
				}
				if (text.equalsIgnoreCase("yes")) {
					startActivity(intent1);
				} else {
					startActivity(intent2);
				}
				// Intent intent = new Intent(getSherlockActivity(),
				// SubmissionActivity.class);
				// intent.putExtra("OBSERVATION_TYPE", text);
				// Log.d("SESSION DETAILS", "Session Id: " +
				// pref.getInt("SESSION_ID", -1));
			}
		});
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		MenuInflater inflater = new MenuInflater(getApplicationContext());
		inflater.inflate(R.menu.activity_start, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if (item.getItemId() == R.id.menu_info) {
			Log.d("CAMERA", "Entered menu info item");
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
					this);
			alertDialogBuilder
					.setTitle("Confirmation")
					.setMessage(
							"This step is to make sure that pollinators visited during this session. Choose 'No' if pollinators did not visit.")
					.setCancelable(true);
			alertDialogBuilder.setPositiveButton("Close",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();
						}
					});
			AlertDialog alertDialog = alertDialogBuilder.create();
			alertDialog.show();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

}
