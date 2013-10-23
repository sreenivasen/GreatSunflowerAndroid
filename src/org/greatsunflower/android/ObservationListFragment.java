package org.greatsunflower.android;

import java.io.File;

import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
	private String[] mItems = { "Eclair", "Froyo", "Gingerbread", "Honeycomb",
			"Ice Cream Sandwich", "Jelly Bean", "ABC", "EFG", "GHI", "XYZ",
			"LMN", "PQR" };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.observations_fragment);
		getListView().setVerticalScrollBarEnabled(false);
		getListView()
				.setDivider(getResources().getDrawable(R.drawable.divider));
		getListView().setDividerHeight(25);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		// add on long click listener to start action mode
		getListView().setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> adapterView,
					View view, int position, long id) {
				onListItemCheck(position);
				return true;
			}
		});

		setListAdapter(new SelectableAdapter(this, mItems));
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
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		case R.id.menu_camera:
			// create Intent to take a picture and return control to the calling
			// application
			Intent imageIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

			File imagesFolder = new File(
					Environment.getExternalStorageDirectory(), "MyImages");
			imagesFolder.mkdirs(); // <----
			File image = new File(imagesFolder, "image_001.jpg");
			Uri uriSavedImage = Uri.fromFile(image);
			imageIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage);
			startActivityForResult(imageIntent, 0);
			return true;


		default:
			return super.onOptionsItemSelected(item);
		}

	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		if (mActionMode == null) {
			// no items selected, so perform item click actions
		} else
			// add or remove selection for current list item
			onListItemCheck(position);
	}

	private void onListItemCheck(int position) {
		SelectableAdapter adapter = (SelectableAdapter) getListAdapter();
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

	private class SelectableAdapter extends ArrayAdapter<String> {

		private SparseBooleanArray mSelectedItemsIds;

		public SelectableAdapter(Context context, String[] objects) {
			super(context, android.R.layout.simple_list_item_2, objects);
			mSelectedItemsIds = new SparseBooleanArray();
		}

		public void toggleSelection(int position) {
			selectView(position, !mSelectedItemsIds.get(position));
		}

		public void removeSelection() {
			mSelectedItemsIds = new SparseBooleanArray();
			notifyDataSetChanged();
		}

		public void selectView(int position, boolean value) {
			if (value)
				mSelectedItemsIds.put(position, value);
			else
				mSelectedItemsIds.delete(position);

			notifyDataSetChanged();
		}

		public int getSelectedCount() {
			return mSelectedItemsIds.size();// mSelectedCount;
		}

		public SparseBooleanArray getSelectedIds() {
			return mSelectedItemsIds;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			if (convertView == null) {
				LayoutInflater inflater = (LayoutInflater) getContext()
						.getSystemService(LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(
						android.R.layout.simple_list_item_1, null);
			}
			((TextView) convertView).setText(getItem(position));
			// change background color if list item is selected
			convertView
					.setBackgroundColor(mSelectedItemsIds.get(position) ? 0x9934B5E4
							: Color.TRANSPARENT);

			return convertView;
		}

	}

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

		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			// retrieve selected items and print them out
			SelectableAdapter adapter = (SelectableAdapter) ObservationListFragment.this
					.getListAdapter();
			SparseBooleanArray selected = adapter.getSelectedIds();
			StringBuilder message = new StringBuilder();
			for (int i = 0; i < selected.size(); i++) {
				if (selected.valueAt(i)) {
					String selectedItem = adapter.getItem(selected.keyAt(i));
					message.append(selectedItem + "\n");
				}
			}
			Toast.makeText(ObservationListFragment.this, message.toString(),
					Toast.LENGTH_LONG).show();

			// close action mode
			mode.finish();
			return false;
		}

		@Override
		public void onDestroyActionMode(ActionMode mode) {
			// remove selection
			SelectableAdapter adapter = (SelectableAdapter) getListAdapter();
			adapter.removeSelection();
			mActionMode = null;
		}

	}

}
