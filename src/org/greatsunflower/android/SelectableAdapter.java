package org.greatsunflower.android;

import android.content.Context;
import android.graphics.Color;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


public class SelectableAdapter extends ArrayAdapter<String> {

//	private SparseBooleanArray mSelectedItemsIds;
//
	public SelectableAdapter(Context context, String[] objects) {
		super(context, android.R.layout.simple_list_item_2, objects);
		//mSelectedItemsIds = new SparseBooleanArray();
	}
//
//	public void toggleSelection(int position) {
//		selectView(position, !mSelectedItemsIds.get(position));
//	}
//
//	public void removeSelection() {
//		mSelectedItemsIds = new SparseBooleanArray();
//		notifyDataSetChanged();
//	}
//
//	public void selectView(int position, boolean value) {
//		if (value)
//			mSelectedItemsIds.put(position, value);
//		else
//			mSelectedItemsIds.delete(position);
//
//		notifyDataSetChanged();
//	}
//
//	public int getSelectedCount() {
//		return mSelectedItemsIds.size();// mSelectedCount;
//	}
//
//	public SparseBooleanArray getSelectedIds() {
//		return mSelectedItemsIds;
//	}
//
//	@Override
//	public View getView(int position, View convertView, ViewGroup parent) {
//
//		if (convertView == null) {
//			LayoutInflater inflater = (LayoutInflater) getContext()
//					.getSystemService();
//			convertView = inflater.inflate(
//					android.R.layout.simple_list_item_1, null);
//		}
//		((TextView) convertView).setText(getItem(position));
//		// change background color if list item is selected
//		convertView
//				.setBackgroundColor(mSelectedItemsIds.get(position) ? 0x9934B5E4
//						: Color.TRANSPARENT);
//
//		return convertView;
//	}

}
