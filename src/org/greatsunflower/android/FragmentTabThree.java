package org.greatsunflower.android;

import java.util.ArrayList;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

import com.actionbarsherlock.app.SherlockFragment;
 
public class FragmentTabThree extends SherlockFragment {
	
	   ListView list;
	    ArrayAdapter adapter;
	    ObservationAnnotation parentActivity;
	    static View rootView;
	    Context currentActivity = getActivity();
	    ArrayList<String> yourlist = new ArrayList<String>();
		private CSVtoSQLiteDataSource csvsource;
		private CSVtoSQLiteTaxas taxas = null;
		FragmentCommunicator comm;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragmenttab3, container, false);
parentActivity = new ObservationAnnotation();
        
		csvsource = new CSVtoSQLiteDataSource(getSherlockActivity());
		csvsource.open();
		
		taxas = new CSVtoSQLiteTaxas();
        
     // Locate the ListView in fragmenttab1.xml
        list = (ListView) rootView.findViewById(R.id.listview3);
        
        
        yourlist.add("");
        adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1, yourlist);
     // Binds the Adapter to the ListView
        list.setAdapter(adapter);
          
        // Capture clicks on ListView items
        list.setOnItemClickListener(new OnItemClickListener() {
        	 
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
            	parentActivity.setCurrentTab(0);
            	comm.respond(adapter.getItem(position).toString());

            }
 
        });
        
        Button button3 = (Button) rootView.findViewById(R.id.Button3);
        button3.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				parentActivity.setCurrentTab(0);
			}
		});
        return rootView;
    }
    
    public void changeText(String data){
    	yourlist.clear();
//    	yourlist.addAll(csvsource.getDistinctCommonName(data));
    	yourlist.addAll(csvsource.getDistinctPollinatorLevel2(data));
    	adapter.notifyDataSetChanged();
    }
    
	@Override
	public void onActivityCreated(Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);
		comm = (FragmentCommunicator) getSherlockActivity();

	}
 
}
