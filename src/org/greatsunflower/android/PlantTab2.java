package org.greatsunflower.android;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragment;
 
public class PlantTab2 extends SherlockFragment {
	
    ListView list;
    ArrayAdapter adapter;
    PlantAnnotation parentActivity;
    static View rootView;
    Context currentActivity = getActivity();
    ArrayList<String> yourlist = new ArrayList<String>();
	private CSVtoSQLiteDataSource csvsource;
	private CSVtoSQLiteTaxas taxas = null;
	FragmentCommunicator comm;
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.planttab2, container, false);
        
        parentActivity = new PlantAnnotation();
        
		csvsource = new CSVtoSQLiteDataSource(getSherlockActivity());
		csvsource.open();
		
		taxas = new CSVtoSQLiteTaxas();
        
     // Locate the ListView in fragmenttab1.xml
        list = (ListView) rootView.findViewById(R.id.listview);
        
        
        yourlist.add("");
        adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1, yourlist);
     // Binds the Adapter to the ListView
        list.setAdapter(adapter);
          
        // Capture clicks on ListView items
        list.setOnItemClickListener(new OnItemClickListener() {
        	 
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
            	parentActivity.setCurrentTab(0);
            	Log.d("FRAGMENT 2", "item clicked: " + adapter.getItem(position).toString());
            	comm.respond(adapter.getItem(position).toString());

            }
 
        });
        
        Button button1 = (Button) rootView.findViewById(R.id.Button1);
        button1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				parentActivity.setCurrentTab(0);
			}
		});
        
        return rootView;
    }
    

    
    public void changeText(String data){
    	yourlist.clear();
    	yourlist.addAll(csvsource.getDistinctPlantLevel1(data));
    	adapter.notifyDataSetChanged();
    }
    
	@Override
	public void onActivityCreated(Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);
		comm = (FragmentCommunicator) getSherlockActivity();
	}
    

 
}
