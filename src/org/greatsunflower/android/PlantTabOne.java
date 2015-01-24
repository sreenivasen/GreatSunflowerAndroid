package org.greatsunflower.android;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
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

public class PlantTabOne extends SherlockFragment {

	ImageView addPollinator;
	Button goToNextTab;
	PlantAnnotation parentActivity;
	
	AutoCompleteTextView pollinatorView;
	String[] pollinators, middle_levels;
	private CSVtoSQLiteDataSource csvsource;
	private CSVtoSQLiteTaxas taxas = null;
	ArrayAdapter<String> pollinatorAdapter, middleLevelAdapter;
	
	FragmentCommunicator comm;
	ListView listView;
    List<SQLiteObservations> rowItems;
    CustomListViewPlantTab1 adapter;
    
	private ObservationsDataSource datasource;
	private SQLiteObservations observation = null;
	
	Button addButton, nextButton, doneButton;
	EditText addCount;
	int pollinatorCount = 0, sessionId;
	String count;
	
	ImageView removePollinator, addImages;
	


	


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.planttab1, container,
				false);
				
		datasource = new ObservationsDataSource(getSherlockActivity());
		datasource.open();
		
		parentActivity = new PlantAnnotation();
		
		listView = (ListView) rootView.findViewById(R.id.listviewTab1); 
		rowItems = new ArrayList<SQLiteObservations>();
		
		SharedPreferences pref = this.getActivity().getSharedPreferences("APP_PREFERENCES", 0);
		Log.d("FRAGMENT 1", "session id: " + pref.getInt("SESSION_ID", -1) + " ");
		sessionId = pref.getInt("SESSION_ID", -1);
		List<SQLiteObservations> allObservations = datasource.getAllPlantObservations(sessionId);

		
		for(SQLiteObservations temp: allObservations){
			// my_array.add(0,temp.getId() + ": " + temp.getPollinator());
			 rowItems.add(0,temp);			 
			 }
		
        adapter = new CustomListViewPlantTab1(getSherlockActivity(),R.layout.listviewitem, rowItems);
        Log.d("SREENI", "STEP 2");
        
        listView.setAdapter(adapter);

			
		csvsource = new CSVtoSQLiteDataSource(getSherlockActivity());
		csvsource.open();
		
		taxas = new CSVtoSQLiteTaxas();
		pollinatorView = (AutoCompleteTextView) rootView.findViewById(R.id.pollinatorSearch);
		pollinators = csvsource.getDistinctPlantLevel0();

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
				
		addButton = (Button) rootView.findViewById(R.id.AddButton);
		addButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Log.d("TAB 0", "touch event acknowledged");
				observation = parentActivity.insertPlants(sessionId, getSherlockActivity(),datasource);
				pollinatorView.setText("");
				rowItems.add(0,observation);
				adapter.setNotifyOnChange(true);
				adapter.notifyDataSetChanged();
			}
		});
		
		doneButton = (Button) rootView.findViewById(R.id.DoneButton);
		doneButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0){
				Intent intent1 = new Intent(getSherlockActivity(), SubmissionActivity.class);
				startActivity(intent1);
			}
		});
		
		return rootView;
	}
	


	
	@Override
	public void onActivityCreated(Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);
		comm = (FragmentCommunicator) getSherlockActivity();
	}

}
