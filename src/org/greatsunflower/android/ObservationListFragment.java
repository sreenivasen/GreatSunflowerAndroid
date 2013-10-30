package org.greatsunflower.android;

import java.io.File;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;

//import org.greatsunflower.android.CustomListViewAdapter.ViewHolder;

import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Files.FileColumns;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckedTextView;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class ObservationListFragment extends SherlockListActivity {

	private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
	private Uri fileUri;

	public static final int MEDIA_TYPE_IMAGE = 1;

	private ActionMode mActionMode;

	private ObservationsDataSource datasource;
	private SQLiteObservations observation = null;
	private ArrayList<String> my_array = new ArrayList<String>();
	private ArrayList<SQLiteObservations> observation_array = new ArrayList<SQLiteObservations>();	
	SelectableAdapter listAdapter;
	CustomListViewAdapter adapter;
	
	ListView listView;
    List<SQLiteObservations> rowItems;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Log.d("SREENI", "Entering observation list fragment");

		setContentView(R.layout.observations_fragment);
		Log.d("SREENI", "STEP 1");
		getListView().setVerticalScrollBarEnabled(true);
		getListView().setDivider(getResources().getDrawable(R.drawable.divider));
		getListView().setDividerHeight(15);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		Log.d("SREENI", "STEP 1.1");
		datasource = new ObservationsDataSource(this);
		datasource.open();

		// add on long click listener to start action mode
		getListView().setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> adapterView,
					View view, int position, long id) {
				Log.d("SREENI","the position being passed on long item checked: " + position);
				Log.d("SREENI","the ID being passed on long item checked: " + id);
				onListItemCheck(position);
				return true;
			}
		});

		rowItems = new ArrayList<SQLiteObservations>();
		List<SQLiteObservations> allObservations = datasource.getAllObservations();

		
		for(SQLiteObservations temp: allObservations){
			// my_array.add(0,temp.getId() + ": " + temp.getPollinator());
			 rowItems.add(0,temp);			 
			 }
		
		

		//Collections.reverse(my_array);
//		listAdapter = new SelectableAdapter(this, my_array);
//		setListAdapter(listAdapter);
		
//        listView = (ListView) findViewById(R.id.list);

        adapter = new CustomListViewAdapter(this,R.layout.listviewitem, rowItems);
        Log.d("SREENI", "STEP 2");
        getListView().setAdapter(adapter);

		
	}
	


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		MenuInflater inflater = new MenuInflater(getApplicationContext());
		inflater.inflate(R.menu.observationlist_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int nextInt;
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		case R.id.menu_camera:
			// create Intent to take a picture and return control to the calling
			// application
			Intent imageIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

			File imagesFolder = new File(Environment.getExternalStorageDirectory(), "MyImages");
			imagesFolder.mkdirs(); // <----
			String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
			String imageFileName = "gsp_" + timeStamp + ".jpg";
			File image = new File(imagesFolder, imageFileName);
			Uri uriSavedImage = Uri.fromFile(image);
			imageIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage);
			startActivityForResult(imageIntent, 0);
			return true;
			
		case R.id.menu_additem:
			String[] rating = new String[] { "Hi", "Very nice", "Hello" };
			nextInt = new Random().nextInt(3);
			// save the new observation to the database
			observation = datasource.createObservations(rating[nextInt]);
			rowItems.add(0, observation);
//			my_array.add(0,observation.getId() + ": " + observation.getPollinator());
//			listAdapter.notifyDataSetChanged();
			adapter.setNotifyOnChange(true);
			adapter.notifyDataSetChanged();
			return true;
			
		default:
			return super.onOptionsItemSelected(item);
		}

	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		if (mActionMode == null) {
			// no items selected, so perform item click actions
			Intent intent = new Intent(this, AnnotationActivity.class);
            startActivity(intent);
		} else
			// add or remove selection for current list item
			onListItemCheck(position);
	}

	private void onListItemCheck(int position) {
		//SelectableAdapter adapter = (SelectableAdapter) getListAdapter();
		//adapter.toggleSelection(position);
		
//		CustomListViewAdapter adapter = (CustomListViewAdapter) getListAdapter();
		Log.d("SREENI","the position being passed on list item checked: " + position);
		adapter.toggleSelection(position);
		
		boolean hasCheckedItems = adapter.getSelectedCount() > 0;

		if (hasCheckedItems && mActionMode == null)
			// there are some selected items, start the actionMode
			mActionMode = startActionMode(new ActionModeCallback());
		else if (!hasCheckedItems && mActionMode != null)
			// there no selected items, finish the actionMode
			mActionMode.finish();

		if (mActionMode != null)
			mActionMode.setTitle(String.valueOf(adapter.getSelectedCount()));
	}

//	private class SelectableAdapter extends ArrayAdapter<String> {
//
//		private SparseBooleanArray mSelectedItemsIds;
//
//		public SelectableAdapter(Context context, ArrayList<String> objects) {
//			super(context, R.layout.listviewitem, objects);
//			mSelectedItemsIds = new SparseBooleanArray();
//		}
//
//		public void toggleSelection(int position) {
//			selectView(position, !mSelectedItemsIds.get(position));
//		}
//
//		public void removeSelection() {
//			mSelectedItemsIds = new SparseBooleanArray();
//			notifyDataSetChanged();
//		}
//
//		public void selectView(int position, boolean value) {
//			if (value)
//				mSelectedItemsIds.put(position, value);
//			else
//				mSelectedItemsIds.delete(position);
//
//			notifyDataSetChanged();
//		}
//
//		public int getSelectedCount() {
//			return mSelectedItemsIds.size();// mSelectedCount;
//		}
//
//		public SparseBooleanArray getSelectedIds() {
//			return mSelectedItemsIds;
//		}
//
//		@Override
//		public View getView(int position, View convertView, ViewGroup parent) {
//			
//			if (convertView == null) {
//				LayoutInflater inflater = (LayoutInflater) getContext()
//						.getSystemService(LAYOUT_INFLATER_SERVICE);
//				convertView = inflater.inflate(android.R.layout.simple_list_item_1, null);
//			}
//			((TextView) convertView).setText(getItem(position));
//			// change background color if list item is selected
//			convertView
//					.setBackgroundColor(mSelectedItemsIds.get(position) ? 0x9934B5E4
//							: Color.TRANSPARENT);
//
//			return convertView;
//		}
//
//	}
	
	


	private class ActionModeCallback implements ActionMode.Callback {

		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			// inflate contextual menu
			mode.getMenuInflater().inflate(R.menu.contextual_menu, menu);
			return true;
		}

		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {

			return false;
		}

		@SuppressWarnings("null")
		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			// retrieve selected items and print them out
			int id;
			//SelectableAdapter adapter = (SelectableAdapter) ObservationListFragment.this.getListAdapter();
//			CustomListViewAdapter adapter = (CustomListViewAdapter) getListAdapter();
			SparseBooleanArray selected = adapter.getSelectedIds();
			ArrayList<Integer> idList = new ArrayList<Integer>();
			StringBuilder message = new StringBuilder();
			SQLiteObservations selectedItem;
			Log.d("SREENI", "SPARSE ARRAY SIZE: " + selected.size() );
			for(int j=0;j<selected.size();j++){
				selectedItem = adapter.getItem(selected.keyAt(j));
				Log.d("SREENI", "ADAPTER / SQLITE ID: " + selectedItem.getId());
				idList.add(selectedItem.getId());				
			}
			
			//for (int k: idList) Log.d("SREENI", "SQLITE IDLIST: " + k);
			
			switch (item.getItemId()) {
			case R.id.contextual_delete:
				for (int k: idList) {
					selectedItem = datasource.getObservation(k);	
					datasource.deleteObservation(k);
					Log.d("SREENI", "SQLITE IDLIST: " + selectedItem.getId());
					Log.d("SREENI", "ROW ITEM POSITION: " + adapter.getPosition(selectedItem));
				}
				mode.finish();
				rowItems.clear();
				rowItems.addAll(0,datasource.getAllObservations());
				Collections.reverse(rowItems);
				adapter.setNotifyOnChange(true);
				adapter.notifyDataSetChanged();
				return true;

				default: return false;
			}
			
		}

		@Override
		public void onDestroyActionMode(ActionMode mode) {
			// remove selection
			//SelectableAdapter adapter = (SelectableAdapter) getListAdapter();
//			CustomListViewAdapter adapter = (CustomListViewAdapter) getListAdapter();
			adapter.removeSelection();
			mActionMode = null;
		}

	}

}
