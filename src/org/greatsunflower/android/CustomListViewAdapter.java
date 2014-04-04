package org.greatsunflower.android;

import java.io.File;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
	        TextView observationType;
	        TextView startdatetime;
	        TextView enddatetime;
	        TextView submission;
	        TextView sessionId;
	    }
	    
	 
	    public View getView(int position, View convertView, ViewGroup parent) {
	        ViewHolder holder = null;
	        BitmapFactory.Options options = new BitmapFactory.Options();
	        SQLiteObservations observation = getItem(position);
	 
	        LayoutInflater mInflater = (LayoutInflater) context
	                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
	        if (convertView == null) {
	            convertView = mInflater.inflate(R.layout.listviewitem, null);
	            holder = new ViewHolder();
	            holder.observationType = (TextView) convertView.findViewById(R.id.observationType);
	            holder.startdatetime = (TextView) convertView.findViewById(R.id.startdatetime);
	            holder.enddatetime = (TextView) convertView.findViewById(R.id.enddatetime);
	            holder.submission = (TextView) convertView.findViewById(R.id.submission);
	            holder.sessionId = (TextView) convertView.findViewById(R.id.sessionId);
	            convertView.setTag(holder);
	        } else
	            holder = (ViewHolder) convertView.getTag();
	 
	        convertView.setBackgroundColor(mSelectedItemsIds.get(position)? 0x9962B1F6 : Color.TRANSPARENT);
	        
	        holder.observationType.setText(observation.getObservationType());
	        holder.startdatetime.setText(observation.getStartDateTime());
	        holder.enddatetime.setText(observation.getEndDateTime());	        
	        if(observation.getIsSubmitted() == 0){
	        	holder.submission.setText( "Submission: Pending");
	        }
	        else{
	        	holder.submission.setText( "Submission: Completed");
	        }
	        holder.sessionId.setText(String.valueOf(observation.getSessionId()));
	        	 
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
