package org.greatsunflower.android;

import java.io.File;
import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class SubmissionActivity extends SherlockFragmentActivity {

	private ObservationsDataSource datasource;
	private SQLiteObservations observation = null;
	Intent intent;
	final SubmissionActivity submit = this;

	TextView observationType, startDateTime, endDateTime, pollinatorsCount;
	TextView pollinatorsDistinctCount, plantCount;

	SQLiteObservations selectedItem;
	int[] ids = null;
	int[] polliCount = new int[2];
	int pCount;
	String[] sessionDetails = new String[3];

	String extraData, taxaValue, middleLevelValue, commonNameValue;
	String visitorGenusValue, visitorSpeciesValue, plantFamilyValue,
			plantGenusValue;
	String plantSpeciesValue, varSubSpeciesValue, submitText, emailIdValue;
	
	private SharedPreferences pref;
	private int sessionId;
	Button submitButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_submit);
		intent = new Intent(this, StartActivity.class);

		datasource = new ObservationsDataSource(this);
		datasource.open();
		
		submitButton = (Button) findViewById(R.id.SubmitButton);
		submitButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				sendEmail();
			}
		});
		pref = getApplicationContext().getSharedPreferences("APP_PREFERENCES", MODE_PRIVATE);
		sessionId = pref.getInt("SESSION_ID", -1);
		if (pref.getString("OBSERVATION_TYPE", null).equals("Stationary")){
			submit.setTitle("Step 5 of 5");
		}
		else
			submit.setTitle("Step 4 of 4");

		observationType = (TextView) findViewById(R.id.ObservationType);
		startDateTime = (TextView) findViewById(R.id.StartDateTime);
		endDateTime = (TextView) findViewById(R.id.EndDateTime);
		pollinatorsCount = (TextView) findViewById(R.id.PollinatorsCount);
//		pollinatorsDistinctCount = (TextView) findViewById(R.id.PollinatorsDistinctCount);
//		plantCount = (TextView) findViewById(R.id.PlantCount); 


		sessionDetails = datasource.getSessionObservationDetails(sessionId);
		polliCount = datasource.getPollinatorsCount(sessionId);
		pCount = datasource.getPlantsCount(sessionId);
		
		observationType.setText(sessionDetails[0]);
		startDateTime.setText(sessionDetails[1]);
		endDateTime.setText(sessionDetails[2]);
//		pollinatorsDistinctCount.setText(polliCount[0] + "");
		pollinatorsCount.setText(polliCount[1] + "");
//		plantCount.setText(pCount + "");
		
//		pollinatorsCount.setText(String.valueOf(ids.length));
//		observationType.setText(extraData);

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
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
			alertDialogBuilder
					.setTitle("Summary")
					.setMessage(
							"Please verify the details.")
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

	protected void sendEmail() {
		Log.i("Send email", "");
		prepareMessageBody();
		String[] TO = { "sreenivasen@gmail.com" };
		String[] CC = { "mmurphy@sfsu.edu" };
		Intent emailIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
		emailIntent.setData(Uri.parse("mailto:"));
		emailIntent.setType("message/rfc822");

		emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
		// emailIntent.putExtra(Intent.EXTRA_CC, CC);
		emailIntent
				.putExtra(Intent.EXTRA_SUBJECT, "GreatSunFlower Observation");
		emailIntent.putExtra(Intent.EXTRA_TEXT, submitText);

		ArrayList<Uri> uris = new ArrayList<Uri>();

		if (ids != null) {
			for (int k = 0; k < ids.length; k++) {
				selectedItem = datasource.getObservation(ids[k]);

				File imgFile = new File(selectedItem.getImagePath());
				Uri u = Uri.fromFile(imgFile);
				uris.add(u);
			}
		}
		emailIntent.putExtra(Intent.EXTRA_STREAM, uris);

		try {
			// startActivity(Intent.createChooser(emailIntent, "Send mail..."));
			startActivityForResult(
					Intent.createChooser(emailIntent, "Send mail..."), 1);
			// finish();
			// startActivity(intent);

			Log.i("Finished sending email...", "");
		} catch (android.content.ActivityNotFoundException ex) {
			Toast.makeText(SubmissionActivity.this,
					"There is no email client installed.", Toast.LENGTH_SHORT)
					.show();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// Check which request we're responding to
		if (requestCode == 1) {
			startActivity(intent);
			// Make sure the request was successful
			if (resultCode == RESULT_OK) {
				startActivity(intent);
			}
		}
	}

	public void prepareMessageBody() {
		submitText = "User Email ID: \t" + emailIdValue + "\n";
		submitText = submitText + "Pollinators Count: \t"
				+ pollinatorsCount.getText().toString() + "\n\n";
		submitText = submitText + "Observation Type: \t" + extraData + "\n";
		submitText = submitText + "Taxa: \t" + taxaValue + "\n";
		submitText = submitText + "Middle Level: \t" + middleLevelValue + "\n";
		submitText = submitText + "Common Name: \t" + commonNameValue + "\n";
		submitText = submitText + "Visitor Genus: \t" + visitorGenusValue
				+ "\n";
		submitText = submitText + "Species Value: \t" + visitorSpeciesValue
				+ "\n";
		submitText = submitText + "Plant Family: \t" + plantFamilyValue + "\n";
		submitText = submitText + "Plant Genus: \t" + plantGenusValue + "\n";
		submitText = submitText + "Plant Species: \t" + plantSpeciesValue
				+ "\n";
		submitText = submitText + "Var (Subspecies): \t" + varSubSpeciesValue
				+ "\n";
	}
}
