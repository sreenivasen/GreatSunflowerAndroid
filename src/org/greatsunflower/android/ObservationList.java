package org.greatsunflower.android;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.R.color;
import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class ObservationList extends SherlockListFragment {
	ActionMode actionBar;
	private boolean isChangedCount;
	private int count = 0;
	private SparseBooleanArray checked;
	private MultiChoiceModeListener listener;

	// Array of strings storing country names
	String[] countries = new String[] { "India", "Pakistan", "Sri Lanka",
			"China", "Bangladesh", "Nepal", "Afghanistan", "North Korea",
			"South Korea", "Japan" };

	// Array of integers points to images stored in /res/drawable/
	int[] flags = new int[] { R.drawable.india};

	// Array of strings to store currencies
	String[] currency = new String[] { "Indian Rupee", "Pakistani Rupee",
			"Sri Lankan Rupee", "Renminbi", "Bangladeshi Taka",
			"Nepalese Rupee", "Afghani", "North Korean Won",
			"South Korean Won", "Japanese Yen" };

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		// Each row in the list stores country name, currency and flag
		List<HashMap<String, String>> aList = new ArrayList<HashMap<String, String>>();

		for (int i = 0; i < 10; i++) {
			HashMap<String, String> hm = new HashMap<String, String>();
			hm.put("txt", countries[i]);
			hm.put("cur", currency[i]);
			hm.put("flag", Integer.toString(flags[i]));
			aList.add(hm);
		}

		// Keys used in Hashmap
		String[] from = { "flag", "txt", "cur" };

		// Ids of views in listview_layout
		int[] to = { R.id.flag, R.id.txt, R.id.cur };

		// Instantiating an adapter to store each items
		// R.layout.listview_layout defines the layout of each item
		SimpleAdapter adapter = new SimpleAdapter(getSherlockActivity(), aList,
				R.layout.activity_observations, from, to);

		setListAdapter(adapter);

		return super.onCreateView(inflater, container, savedInstanceState);
	}
	
	@SuppressLint("NewApi")
	private int getCheckedItemCount(ListView listView)
	{
	    if (Build.VERSION.SDK_INT >= 11) 
	    	count = listView.getCheckedItemCount();
	    else
	    {
	        count = 0;
	        for (int i = listView.getCount() - 1; i >= 0; i--)
	            if (listView.isItemChecked(i)) count++;
	        
	    }
	    return count;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		getListView().setVerticalScrollBarEnabled(false);
		getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		getListView()
				.setDivider(getResources().getDrawable(R.drawable.divider));
		getListView().setDividerHeight(25);

		setHasOptionsMenu(true);

		
		getListView().setOnItemLongClickListener(
				new AdapterView.OnItemLongClickListener() {
					@Override
					public boolean onItemLongClick(AdapterView<?> parent,
							View view, int position, long id) {


						getListView().setItemChecked(position, true);
						getListView().setSelected(true);
						checked = getListView().getCheckedItemPositions();
						//count = getCheckedItemCount(getListView());
						count = checked.size();
						
						boolean hasCheckedElement = false;
						
						for(int i =0; i < checked.size() && !hasCheckedElement; i++){
							hasCheckedElement = checked.valueAt(i);
						}

						
						Log.d(ObservationList.class.getSimpleName(),
								count + " ITEMS LONG CLICKED");

						Log.d(ObservationList.class.getSimpleName(),
								" ENTERING ACTION MODE");
						
						getListView().setBackgroundResource(
								R.drawable.activated_background_holo_light);
						
					/*	if(hasCheckedElement) {
							if(actionBar == null) {
								actionBar = getSherlockActivity().startActionMode(new ListViewActionMode());
							}
						} else {
							if(actionBar != null) {
								actionBar.finish();
							}
						}*/
						
						
						actionBar = getSherlockActivity().startActionMode(new ListViewActionMode()); 
						
						Log.d(ObservationList.class.getSimpleName(),
								" QUITTING ACTION MODE");
						
						
						
						
						return true;
					}
				});
		
		/*getListView().setOnLongClickListener(new View.OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				if(actionBar != null) {
					return false;
					
				}
				
				actionBar = getSherlockActivity().startActionMode(new ListViewActionMode());
				v.setSelected(true);
				return true;
			}
		});*/
		
		/*getListView().setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				checked = getListView().getCheckedItemPositions();
				//count = getCheckedItemCount(getListView());
				count = checked.size();
				Log.d("Inside single click", "Count is" + String.valueOf(count));
				
				boolean hasCheckedElement = false;
				
				for(int i =0; i < checked.size() && !hasCheckedElement; i++){
					hasCheckedElement = checked.valueAt(i);
				}
				
			}
		});*/

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// TODO Auto-generated method stub
		inflater.inflate(R.menu.observationlist_menu, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		if (item.getItemId() == R.id.menu_camera) {
			Toast.makeText(getSherlockActivity(), "CAMERA WILL BE TRIGGERED",
					Toast.LENGTH_SHORT).show();
		}
		return true;
	}

	private final class ListViewActionMode implements ActionMode.Callback{

		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			MenuInflater inflater = mode.getMenuInflater();
			inflater.inflate(R.menu.contextual_menu, menu);
			//count = getCheckedItemCount(getListView());
			Log.d(ObservationList.class.getSimpleName(),
					count + " I AM IN CREATE ACTION MODE");
			return true;
			// menu.add("Delete").setIcon(android.R.drawable.ic_delete)
			// .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
			//
			// menu.add("Annotate").setIcon(android.R.drawable.ic_input_add)
			// .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

		}

		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			long [] selected = getListView().getCheckedItemIds();
			count = selected.length;
			
			actionBar = mode;
			 if (actionBar == null) {
	                MenuInflater inflater = mode.getMenuInflater();
	                inflater.inflate(R.menu.contextual_menu, menu);
					Log.d(ObservationList.class.getSimpleName(),
							count + " nothing(1) is happening here");
					//actionBar.setTitle( String.valueOf(count) + "Item");
	                return false;
	            }
			 else if(actionBar == null && count > 0){
	                MenuInflater inflater = mode.getMenuInflater();
	                actionBar.setTitle(String.valueOf(count) + " Items");
	                inflater.inflate(R.menu.contextual_menu, menu);
					Log.d(ObservationList.class.getSimpleName(),
							count + " nothing(2) is happening here");
	                return true;
			 }
			 if (count == 0) {
				 actionBar.finish();
					Log.d(ObservationList.class.getSimpleName(),
							count + " items selected");
	            } else if (count == 1 && actionBar != null) {
	            	actionBar.setTitle(String.valueOf(count) + " item");
	                MenuInflater inflater = mode.getMenuInflater();
	                inflater.inflate(R.menu.contextual_menu, menu);
					Log.d(ObservationList.class.getSimpleName(),
							count + " items selected");
	                return true;
	            } else if (count > 1) {
	            	actionBar.setTitle(String.valueOf(count) + " items");
					Log.d(ObservationList.class.getSimpleName(),
							count + " items selected");
	                MenuInflater inflater = mode.getMenuInflater();
	                inflater.inflate(R.menu.contextual_menu, menu);
	                return true;
	            }
			
			/*if(actionBar == null) {
				Log.d("Action mode count 0", "Nothing to display here");
				actionBar.setTitle("Test");
				MenuInflater inflater = mode.getMenuInflater();
				menu.clear();
				inflater.inflate(R.menu.contextual_menu, menu);
				return false;
			}
			
			if(count == 0) {
				actionBar.finish();
			} else if (count == 1 && actionBar != null) {
				actionBar.setTitle(String.valueOf(count) + " item");
				MenuInflater inflater = mode.getMenuInflater();
				menu.clear();
				inflater.inflate(R.menu.contextual_menu, menu);
				return true;
			} else if (count > 1) {
				actionBar.setTitle(String.valueOf(count) + " items");
				MenuInflater inflater = mode.getMenuInflater();
				menu.clear();
				inflater.inflate(R.menu.contextual_menu, menu);
				return true;
			}*/
			
	            return true;



		}

		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			Toast.makeText(getSherlockActivity(), "Implement actions here",
					Toast.LENGTH_SHORT).show();
			
			

			actionBar.finish();
			return true;
		}

		@Override
		public void onDestroyActionMode(ActionMode mode) {
			getListView().clearChoices();
			getListView().clearFocus();
			
			for(int i = 0; i < getListAdapter().getCount() ; i++)
				getListView().setItemChecked(i, false);
			
			if( mode == actionBar) {
				actionBar = null;
			}

		}

		

	}

}
