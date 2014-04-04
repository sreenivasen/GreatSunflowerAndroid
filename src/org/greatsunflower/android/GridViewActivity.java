package org.greatsunflower.android;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

//import org.greatsunflower.android.ObservationListFragment.ActionModeCallback;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.GridView;

public class GridViewActivity extends SherlockFragmentActivity {

	private GalleryUtils utils;
	private ArrayList<String> imagePaths = new ArrayList<String>();
	private GridViewImageAdapter adapter;
	private GridView gridView;
	private int columnWidth;
	private int sessionId;
	private SharedPreferences pref;
	private Button nextButton;

	final GridViewActivity gridViewActivity = this;
	
	private ActionMode mActionMode;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_grid_view);

		gridView = (GridView) findViewById(R.id.grid_view);

		utils = new GalleryUtils(this);

		pref = getApplicationContext().getSharedPreferences("APP_PREFERENCES",
				MODE_PRIVATE);
		Log.d("SESSION DETAILS", "Session Id: " + pref.getInt("SESSION_ID", -1));
		sessionId = pref.getInt("SESSION_ID", -1);

		nextButton = (Button) findViewById(R.id.nextButton);

		nextButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(gridViewActivity,
						ObservationAnnotation.class);
				startActivity(intent);
			}
		});

		// Initilizing Grid View
		InitilizeGridLayout();

		// loading all image paths from SD card
		imagePaths = utils.getFilePaths(sessionId);

		// Gridview adapter
		adapter = new GridViewImageAdapter(GridViewActivity.this, imagePaths,
				columnWidth);

		// setting grid view adapter
		gridView.setAdapter(adapter);

		gridView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> adapterView, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Log.d("IMAGE", "long press detected");
				onListItemCheck(position);
				return true;
			}

		});
	}

	private void InitilizeGridLayout() {
		Resources r = getResources();
		float padding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				AppConstant.GRID_PADDING, r.getDisplayMetrics());

		columnWidth = (int) ((utils.getScreenWidth() - ((AppConstant.NUM_OF_COLUMNS + 1) * padding)) / AppConstant.NUM_OF_COLUMNS);

		gridView.setNumColumns(AppConstant.NUM_OF_COLUMNS);
		gridView.setColumnWidth(columnWidth);
		gridView.setStretchMode(GridView.NO_STRETCH);
		gridView.setPadding((int) padding, (int) padding, (int) padding,
				(int) padding);
		gridView.setHorizontalSpacing((int) padding);
		gridView.setVerticalSpacing((int) padding);
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
	
	public class ActionModeCallback implements ActionMode.Callback {

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
			SparseBooleanArray selected = adapter.getSelectedIds();
			ArrayList<Integer> idList = new ArrayList<Integer>();
			StringBuilder message = new StringBuilder();
			SQLiteObservations selectedItem;
			Log.d("SREENI", "SPARSE ARRAY SIZE: " + selected.size() );
			for(int j=0;j<selected.size();j++){
				//selectedItem = adapter.getItem(selected.keyAt(j));
				//Log.d("SREENI", "ADAPTER / SQLITE ID: " + selectedItem.getId());
				//idList.add(selectedItem.getId());				
			}
			
			//for (int k: idList) Log.d("SREENI", "SQLITE IDLIST: " + k);
			
			switch (item.getItemId()) {
			case R.id.contextual_delete:
				for (int k: idList) {
					//selectedItem = datasource.getObservation(k);	
					//datasource.deleteObservation(k);
					//Log.d("SREENI", "SQLITE IDLIST: " + selectedItem.getId());
					//Log.d("SREENI", "ROW ITEM POSITION: " + adapter.getPosition(selectedItem));
				}
				mode.finish();
				//rowItems.clear();
				//rowItems.addAll(0,datasource.getAllObservations());
				//Collections.reverse(rowItems);
				//adapter.setNotifyOnChange(true);
				//adapter.notifyDataSetChanged();
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