package org.greatsunflower.android;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomListViewAdapter extends ArrayAdapter<SQLiteObservations> {
	 Context context;
	 
	 private SparseBooleanArray mSelectedItemsIds;
	 
	    public CustomListViewAdapter(Context context, int resourceId, List<SQLiteObservations> items) {
	        super(context, resourceId, items);
	        this.context = context;
	        mSelectedItemsIds = new SparseBooleanArray();
	    }
	 
	    /*private view holder class*/
	    private class ViewHolder {
	        ImageView imageView;
	        TextView txtPollinator;
	        TextView txtPollinatorName;
	        TextView txtFlower;
	        TextView txtFlowerName;
	        TextView txtLocation;
	        TextView txtDateTime;
	    }
	 
	    public View getView(int position, View convertView, ViewGroup parent) {
	        ViewHolder holder = null;
	        SQLiteObservations observation = getItem(position);
	 
	        LayoutInflater mInflater = (LayoutInflater) context
	                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
	        if (convertView == null) {
	            convertView = mInflater.inflate(R.layout.listviewitem, null);
	            holder = new ViewHolder();
	            holder.txtPollinator = (TextView) convertView.findViewById(R.id.pollinator);
	            holder.txtPollinatorName = (TextView) convertView.findViewById(R.id.Actualpollinator);
	            holder.txtFlower = (TextView) convertView.findViewById(R.id.flowername);
	            holder.txtFlowerName = (TextView) convertView.findViewById(R.id.Actualflowername);
	            holder.imageView = (ImageView) convertView.findViewById(R.id.icon);
	            convertView.setTag(holder);
	        } else
	            holder = (ViewHolder) convertView.getTag();
	 
	        convertView.setBackgroundColor(mSelectedItemsIds.get(position)? 0x9962B1F6 : Color.TRANSPARENT);
	        
	        holder.txtPollinator.setText("Pollinator: ");
	        holder.txtPollinatorName.setText(observation.getPollinator());
	        holder.txtFlower.setText("Plant Name: ");
	        holder.txtFlowerName.setText( String.valueOf(observation.getId()));
	        holder.imageView.setImageResource(R.drawable.india);
	 
	        return convertView;
	    }
	    
	    public void toggleSelection(int position) {
	    	Log.d("SREENI","the position being passed in toggle selection: " + position);
			selectView(position, !mSelectedItemsIds.get(position));
		}
	    
	    public void selectView(int position, boolean value) {
	    	Log.d("SREENI","the position being passed in selectview: " + position);
	    	Log.d("SREENI","the value being passed in selectview: " + value);
			if (value)
				mSelectedItemsIds.put(position, value);
			else
				mSelectedItemsIds.delete(position);

			notifyDataSetChanged();
		}
	    
	    public int getSelectedCount() {
	    	Log.d("SREENI","get selected count " + mSelectedItemsIds.size());
			return mSelectedItemsIds.size();// mSelectedCount;
		}

		public SparseBooleanArray getSelectedIds() {
			return mSelectedItemsIds;
		}
		
		public void removeSelection() {
			mSelectedItemsIds = new SparseBooleanArray();
			notifyDataSetChanged();
		}

}
