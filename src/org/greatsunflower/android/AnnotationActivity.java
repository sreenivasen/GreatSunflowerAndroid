package org.greatsunflower.android;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.datatype.Duration;

import android.app.Notification.Style;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;

import de.keyboardsurfer.android.widget.crouton.Crouton;

public class AnnotationActivity extends SherlockFragmentActivity {
	private ObservationsDataSource datasource;
	private SQLiteObservations observation = null;
	LinearLayout inHorizontalScrollView, inScrollView;
	LinearLayout myGallery;
	
	final AnnotationActivity annotationActivity = this;
	
	TableLayout table;
	TableRow row;
	TextView text1, text2;
	
	ImageView addPollinator;
	ListView listItems;

	AutoCompleteTextView pollinatorView, middle_levelView;
	String[] pollinators, middle_levels;
	private CSVtoSQLiteDataSource csvsource;
	private CSVtoSQLiteTaxas taxas = null;

	ArrayAdapter<String> pollinatorAdapter, middleLevelAdapter;

	private Button nextButton, prevButton, submitButton;
	TextView steps, subHeader;
	private int i = 1;

	String extraData, sessionId;

	Intent submitIntent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.annotate_observation);

		submitIntent = new Intent(this, SubmissionActivity.class);

		datasource = new ObservationsDataSource(this);
		datasource.open();

		csvsource = new CSVtoSQLiteDataSource(this);
		csvsource.open();

		// addListenerOnSpinnerItemSelection();

		Uri imagePath = null;
		// ImageView imageView = (ImageView) findViewById(R.id.imageView1);

		Intent sender = getIntent();
//		sessionId = sender.getExtras().getString("SESSIONID");
//		extraData = sender.getExtras().getString("OBSERVATION_TYPE");

		SQLiteObservations selectedItem;
		int[] ids = null;

		int session = Integer.parseInt(sessionId);

		try {
			ids = datasource.getSessionObservations(session);
		} catch (Exception e) {
			Log.d("ANNOTATION SCREEN",
					"error caught message" + e.getLocalizedMessage());
		}

//		myGallery = (LinearLayout) findViewById(R.id.mygallery);

		if (ids != null) {
			for (int k = 0; k < ids.length; k++) {
				selectedItem = datasource.getObservation(ids[k]);

				File imgFile = new File(selectedItem.getImagePath());
//				myGallery.addView(insertPhoto(imgFile.getAbsolutePath()));
			}
		}

		prevButton = (Button) findViewById(R.id.PrevButton);
		nextButton = (Button) findViewById(R.id.NextButton);
		
		table = (TableLayout) findViewById(R.id.pollinatorTable);
		row = (TableRow) findViewById(R.id.tableRow);
		table.removeAllViews();
		text1 = (TextView) findViewById(R.id.attrib_name);
		text2 = (TextView) findViewById(R.id.attrib_value);		    	        	
//		text1.setText("Pollinator:");
//    	text2.setText("BumbleBee");
		
		
		
		addPollinator = (ImageView) findViewById(R.id.AddPollinator);
		addPollinator.setOnClickListener(new OnClickListener() {
		    public void onClick(View v) {
		       // your code here
		    	addNewRow();
		    }
		});

		prevButton.setEnabled(false);
		nextButton.setEnabled(false);

//		submitButton = (Button) findViewById(R.id.SubmitButton);
		steps = (TextView) findViewById(R.id.Steps);
//		subHeader = (TextView) findViewById(R.id.subHeader);

		taxas = new CSVtoSQLiteTaxas();
		pollinatorView = (AutoCompleteTextView) findViewById(R.id.pollinatorSearch);
		pollinators = csvsource.getDistinctTaxa();
		// pollinatorAdapter = new ArrayAdapter<String>(this,
		// android.R.layout.select_dialog_item, pollinators);
		pollinatorAdapter = new ArrayAdapter<String>(this,
				R.layout.custom_dropdown, pollinators);
		pollinatorView.setAdapter(pollinatorAdapter);

		pollinatorView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long rowId) {
				String selection = (String) parent.getItemAtPosition(position);
				if (i == 1) {
					taxas.setTaxa(selection);
					nextButton.setEnabled(true);
				}
				if (i == 2) {
					taxas.setMiddleLevel(selection);
				}
				// TODO Do something with the selected text
			}
		});

		prevButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (i == 2) {
					i--;
					nextButton.setEnabled(true);
					prevButton.setEnabled(false);
					steps.setText("STEP " + String.valueOf(i) + " OF 2");
					subHeader.setText("Pollinator:");
					// pollinatorView.setHint("Search Pollinator");
					pollinatorView.setText(taxas.getTaxa());
					pollinatorView.setAdapter(pollinatorAdapter);
				}

			}
		});

		nextButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (i == 1) {
					i++;
					nextButton.setEnabled(false);
					prevButton.setEnabled(true);
					steps.setText("STEP " + String.valueOf(i) + " OF 2");
					subHeader.setText("Middle Level Names: ");
					pollinatorView.setHint("Search Middle Level Names");
					pollinatorView.setText(taxas.getMiddleLevel());
					updateAutoComplete();
				}

			}
		});

//		submitButton.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				submitIntent.putExtra("OBSERVATION_TYPE", extraData);
//				submitIntent.putExtra("SESSIONID", sessionId);
//				submitIntent.putExtra("TAXA", taxas.getTaxa());
//				submitIntent.putExtra("MIDDLE_LEVEL", taxas.getMiddleLevel());
//				submitIntent.putExtra("COMMON_NAME", taxas.getCommonName());
//				submitIntent.putExtra("VISITOR_GENUS", taxas.getVisitorGenus());
//				submitIntent.putExtra("VISITOR_SPECIES",
//						taxas.getVisitorSpecies());
//				submitIntent.putExtra("PLANT_FAMILY", taxas.getPlantFamily());
//				submitIntent.putExtra("PLANT_GENUS", taxas.getPlantGenus());
//				submitIntent.putExtra("PLANT_SPECIES", taxas.getPlantSpecies());
//				submitIntent.putExtra("VAR_SUBSPECIES",
//						taxas.getVarSubSpecies());
//				startActivity(submitIntent);
//
//			}
//		});

	}

	public void updateAutoComplete() {
		//middle_levels = csvsource.getDistinctMiddleLevel(taxas.getTaxa());
		middleLevelAdapter = new ArrayAdapter<String>(this,
				R.layout.custom_dropdown, middle_levels);
		pollinatorView.setAdapter(middleLevelAdapter);
	}

	View insertPhoto(String path) {
		Bitmap bm = decodeSampledBitmapFromUri(path, 220, 220);

		LinearLayout layout = new LinearLayout(getApplicationContext());
		layout.setLayoutParams(new LayoutParams(250, 250));
		layout.setGravity(Gravity.CENTER);

		ImageView imageView = new ImageView(getApplicationContext());
		imageView.setLayoutParams(new LayoutParams(220, 220));
		imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
		imageView.setImageBitmap(bm);

		layout.addView(imageView);
		return layout;
	}

	public Bitmap decodeSampledBitmapFromUri(String path, int reqWidth,
			int reqHeight) {
		Bitmap bm = null;

		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, options);

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		bm = BitmapFactory.decodeFile(path, options);

		return bm;
	}

	public int calculateInSampleSize(

	BitmapFactory.Options options, int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {
			if (width > height) {
				inSampleSize = Math.round((float) height / (float) reqHeight);
			} else {
				inSampleSize = Math.round((float) width / (float) reqWidth);
			}
		}

		return inSampleSize;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		MenuInflater inflater = new MenuInflater(getApplicationContext());
		inflater.inflate(R.menu.annotate_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	public void addNewRow(){
		for (int i=0;i<2;i++){

	        //Create a new row to be added.
	        TableRow tr = new TableRow(this);
	        tr.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));

	        //Create text views to be added to the row.
	        TextView name = new TextView(this);
	        name.setText("Pollinator:");
	        name.setTextSize(14);
	        name.setGravity(Gravity.RIGHT);
	        name.setTypeface(Typeface.DEFAULT_BOLD);
	        TableRow.LayoutParams nameParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 0.6f);
	        nameParams.setMargins(7, 5, 0, 0);

	        TextView quantity = new TextView(this);
	        quantity.setText("bummblebee");
	        quantity.setTextSize(14);
	        quantity.setTypeface(Typeface.DEFAULT_BOLD);
	        TableRow.LayoutParams quantityParams = new TableRow.LayoutParams( TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 0.2f);
	        quantityParams.setMargins(0, 5, 0, 0);

	        tr.addView(name,nameParams);
	        tr.addView(quantity,quantityParams);

	        table.addView(tr);
		}
	}

}
