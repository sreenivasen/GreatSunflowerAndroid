package org.greatsunflower.android;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class FragmentTabOne extends SherlockFragment {

	ImageView addPollinator;
	Button goToNextTab;
	ObservationAnnotation parentActivity;
	
	AutoCompleteTextView pollinatorView;
	String[] pollinators, middle_levels;
	private CSVtoSQLiteDataSource csvsource;
	private CSVtoSQLiteTaxas taxas = null;
	ArrayAdapter<String> pollinatorAdapter, middleLevelAdapter;
	
	FragmentCommunicator comm;
	ListView listView;
    List<SQLiteObservations> rowItems;
    CustomListViewTab1 adapter;
    
	private ObservationsDataSource datasource;
	private SQLiteObservations observation = null;
	
	Button addButton, nextButton, doneButton;
	EditText addCount;
	int pollinatorCount = 0, sessionId;
	String count;
	int currentapiVersion = android.os.Build.VERSION.SDK_INT;
	
	ImageView removePollinator, addImages;

	int imagesCount;
	


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragmenttab1, container,
				false);
				
		datasource = new ObservationsDataSource(getSherlockActivity());
		datasource.open();
		
		parentActivity = new ObservationAnnotation();
		
		listView = (ListView) rootView.findViewById(R.id.listviewTab1); 
		rowItems = new ArrayList<SQLiteObservations>();
		
		final SharedPreferences pref = this.getActivity().getSharedPreferences("APP_PREFERENCES", 0);
		Log.d("FRAGMENT 1", "session id: " + pref.getInt("SESSION_ID", -1) + " ");
		sessionId = pref.getInt("SESSION_ID", -1);
		List<SQLiteObservations> allObservations = datasource.getAllPollinators(sessionId);

		
		for(SQLiteObservations temp: allObservations){
			// my_array.add(0,temp.getId() + ": " + temp.getPollinator());
			 rowItems.add(0,temp);			 
			 }
		
        adapter = new CustomListViewTab1(getSherlockActivity(),R.layout.listviewitem, rowItems);
        Log.d("SREENI", "STEP 2");
        listView.setAdapter(adapter);

			
		csvsource = new CSVtoSQLiteDataSource(getSherlockActivity());
		csvsource.open();
		
		taxas = new CSVtoSQLiteTaxas();
		pollinatorView = (AutoCompleteTextView) rootView.findViewById(R.id.pollinatorSearch);
//		pollinators = csvsource.getDistinctTaxa();
		pollinators = csvsource.getDistinctPollinatorLevel0();
		// pollinatorAdapter = new ArrayAdapter<String>(this,
		// android.R.layout.select_dialog_item, pollinators);
		pollinatorAdapter = new ArrayAdapter<String>(getSherlockActivity(),
				R.layout.custom_dropdown, pollinators);
		pollinatorView.setAdapter(pollinatorAdapter);

		pollinatorView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long rowId) {
				parentActivity.setCurrentTab(1);
				comm.respond(pollinatorAdapter.getItem(position));
				Log.d("FRAGMENT TAB 1", pollinatorAdapter.getItem(position));
								
			}
		});
		
	
		imagesCount = datasource.getImageCount(sessionId);
		
		
		addImages = (ImageView) rootView.findViewById(R.id.addImage);
		addImages.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Log.d("FRAGMENT 1", "entering add images");
				Intent gridViewIntent = new Intent(getSherlockActivity(), ImageSelectGridview.class);
				Log.d("FRAGMENT 1", "current API Version: " + currentapiVersion);
				Log.d("FRAGMENT 1", "images count: "  + imagesCount);
				if(currentapiVersion < android.os.Build.VERSION_CODES.HONEYCOMB){
					parentActivity.buildAPILevelDialogWarning(getSherlockActivity());
				}
				else if (imagesCount == 0) {
					parentActivity.buildWarningDialog(getSherlockActivity());
				}
				else{
				startActivity(gridViewIntent);			
				}
				//parentActivity.showDialogForImages(sessionId,getSherlockActivity());
				Log.d("FRAGMENT 1", " finished building alert dialog");
			}
		});
		
		addCount = (EditText) rootView.findViewById(R.id.numberInput);
		
		addButton = (Button) rootView.findViewById(R.id.AddButton);
		doneButton = (Button) rootView.findViewById(R.id.DoneButton);
		if (pref.getString("OBSERVATION_TYPE", null).equals("Stationary")){
			doneButton.setText("Step 4: Identify Plants");
		}
		else{
			doneButton.setText("Step 4: Submit Count");
		}
		addButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Log.d("TAB 0", "touch event acknowledged");
				Log.d("Images selected" , " "  + pref.getString("IMAGE_IDS", ""));
				count = addCount.getText().toString();
				pollinatorCount = Integer.valueOf(count);
				observation = parentActivity.insertPollinators(sessionId, getSherlockActivity(),datasource, pollinatorCount);
				addCount.setText("0");
				pollinatorView.setText("");
				rowItems.add(0,observation);
				adapter.setNotifyOnChange(true);
				adapter.notifyDataSetChanged();
			}
		});
		
		doneButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0){
				Intent intent1 = new Intent(getSherlockActivity(), PlantAnnotation.class);
				Intent intent2 = new Intent(getSherlockActivity(), SubmissionActivity.class);
				if (pref.getString("OBSERVATION_TYPE", null).equals("Stationary")){
					startActivity(intent1);
				}
				else{
					startActivity(intent2);
				}
				
			}
		});
		

//		
//		addPollinator = (ImageView) rootView
//				.findViewById(R.id.AddPollinatorButton);
//		goToNextTab = (Button) rootView
//				.findViewById(R.id.AddPollinatorButtonNext);
//		addPollinator.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View arg0) {
//				// TODO Auto-generated method stub
//				Log.d("TAB 0", "touch event acknowledged");
//				parentActivity.setCurrentTab(1);
//
//			}
//
//		});

		return rootView;
	}
	


	
	@Override
	public void onActivityCreated(Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);
		comm = (FragmentCommunicator) getSherlockActivity();
	}
	
	

}
